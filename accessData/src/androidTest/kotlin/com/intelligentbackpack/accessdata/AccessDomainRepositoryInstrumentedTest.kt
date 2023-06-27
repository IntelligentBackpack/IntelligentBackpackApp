package com.intelligentbackpack.accessdata

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.intelligentbackpack.accessdata.datasource.AccessDomainRepositoryImpl
import com.intelligentbackpack.accessdata.datasource.AccessLocalDataSourceImpl
import com.intelligentbackpack.accessdata.datasource.AccessRemoteDataSource
import com.intelligentbackpack.accessdata.exception.MissingUserException
import com.intelligentbackpack.accessdata.storage.UserStorageImpl
import com.intelligentbackpack.accessdomain.entities.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

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
    private val accessRemoteDataSource = mockk<AccessRemoteDataSource>(relaxed = true)

    @Test
    fun loginWithData() = runBlocking {
        val appContext = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
        coEvery { accessRemoteDataSource.accessWithData(any(), any()) } returns expectedUser
        val localAccessDataSource = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        val accessDomainRepository = AccessDomainRepositoryImpl(localAccessDataSource, accessRemoteDataSource)
        assertFalse(localAccessDataSource.isUserSaved())
        val user = accessDomainRepository.loginWithData("test@gmail.com", "Test#1234")
        coVerify {
            accessRemoteDataSource.accessWithData("test@gmail.com", "Test#1234")
        }
        assertEquals(expectedUser, user)
        assertEquals(expectedUser, localAccessDataSource.getUser())
        assertTrue(localAccessDataSource.isUserSaved())
    }

    @Test
    fun automaticLoginWithUserSaved(): Unit = runBlocking {
        val appContext = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
        coEvery { accessRemoteDataSource.accessWithData(any(), any()) } returns expectedUser
        val accessLocalDataSource = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        accessLocalDataSource.saveUser(expectedUser)
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataSource, accessRemoteDataSource)
        assertTrue(accessLocalDataSource.isUserSaved())
        val user = accessDomainRepository.automaticLogin()
        coVerify(exactly = 1) {
            accessRemoteDataSource.accessWithData("test@gmail.com", "Test#1234")
        }
        assertEquals(expectedUser, user)
    }

    @Test
    fun automaticLoginWithoutUserSavedCreateException(): Unit = runBlocking {
        val appContext = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
        val accessLocalDataStorage = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataStorage, accessRemoteDataSource)
        assertFalse(accessLocalDataStorage.isUserSaved())
        assertThrows(MissingUserException::class.java) {
            runBlocking {
                accessDomainRepository.automaticLogin()
            }
        }
    }

    @Test
    fun logout() = runBlocking {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessLocalDataSource = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        accessLocalDataSource.saveUser(expectedUser)
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataSource, accessRemoteDataSource)
        val user = accessDomainRepository.logoutUser()
        assertEquals(expectedUser, user)
        assertFalse(accessLocalDataSource.isUserSaved())
    }
}
