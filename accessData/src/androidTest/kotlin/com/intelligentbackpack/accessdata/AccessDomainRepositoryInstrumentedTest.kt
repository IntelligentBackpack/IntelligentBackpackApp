package com.intelligentbackpack.accessdata

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.intelligentbackpack.accessdata.datasource.AccessDomainRepositoryImpl
import com.intelligentbackpack.accessdata.datasource.AccessLocalDataStorageImpl
import com.intelligentbackpack.accessdata.datasource.AccessRemoteDataStorage
import com.intelligentbackpack.accessdata.exception.MissingUserException
import com.intelligentbackpack.accessdata.storage.UserStorageImpl
import com.intelligentbackpack.accessdomain.entities.User
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

/**
 * Instrumented test, which will execute on an Android device.
 * Tests for [AccessDomainRepositoryImpl]
 */
@RunWith(AndroidJUnit4::class)
class AccessDomainRepositoryInstrumentedTest {

    private val expectedUser = User.build {
        email = "test@gmail.com"
        password = "Test#1234"
        name = "test"
        surname = "test"
    }

    @Test
    fun loginWithData() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessRemoteDataStorage = mock(AccessRemoteDataStorage::class.java)
        `when`(
            accessRemoteDataStorage.accessWithData(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(expectedUser)
        val localAccessDataStorage = AccessLocalDataStorageImpl(UserStorageImpl(appContext))
        val accessLocalDataStorage = AccessDomainRepositoryImpl(localAccessDataStorage, accessRemoteDataStorage)
        assertFalse(localAccessDataStorage.isUserSaved())
        val user = accessLocalDataStorage.loginWithData("test@gmail.com", "Test#1234")
        verify(accessRemoteDataStorage).accessWithData("test@gmail.com", "Test#1234")
        assertEquals(expectedUser, user)
        assertEquals(expectedUser, localAccessDataStorage.getUser())
        assertTrue(localAccessDataStorage.isUserSaved())
    }

    @Test
    fun automaticLoginWithUserSaved() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessRemoteDataStorage = mock(AccessRemoteDataStorage::class.java)
        val accessLocalDataStorage = AccessLocalDataStorageImpl(UserStorageImpl(appContext))
        accessLocalDataStorage.saveUser(expectedUser)
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataStorage, accessRemoteDataStorage)
        assertTrue(accessLocalDataStorage.isUserSaved())
        assertTrue(accessDomainRepository.isUserLogged())
        val user = accessDomainRepository.automaticLogin()
        verify(accessRemoteDataStorage, never()).accessWithData("test@gmail.com", "Test#1234")
        assertEquals(expectedUser, user)
    }

    @Test
    fun automaticLoginWithoutUserSavedCreateException() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessRemoteDataStorage = mock(AccessRemoteDataStorage::class.java)
        val accessLocalDataStorage = AccessLocalDataStorageImpl(UserStorageImpl(appContext))
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataStorage, accessRemoteDataStorage)
        assertFalse(accessLocalDataStorage.isUserSaved())
        assertThrows(MissingUserException::class.java) {
            accessDomainRepository.automaticLogin()
        }
    }

    @Test
    fun logout() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessRemoteDataStorage = mock(AccessRemoteDataStorage::class.java)
        val accessLocalDataStorage = AccessLocalDataStorageImpl(UserStorageImpl(appContext))
        accessLocalDataStorage.saveUser(expectedUser)
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataStorage, accessRemoteDataStorage)
        assertTrue(accessDomainRepository.isUserLogged())
        accessDomainRepository.logoutUser()
        assertFalse(accessDomainRepository.isUserLogged())
        assertFalse(accessLocalDataStorage.isUserSaved())
    }
}