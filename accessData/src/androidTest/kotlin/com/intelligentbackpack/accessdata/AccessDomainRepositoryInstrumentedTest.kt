package com.intelligentbackpack.accessdata

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.intelligentbackpack.accessdata.datasource.AccessDomainRepositoryImpl
import com.intelligentbackpack.accessdata.datasource.AccessLocalDataSourceImpl
import com.intelligentbackpack.accessdata.datasource.AccessRemoteDataSource
import com.intelligentbackpack.accessdata.exception.MissingUserException
import com.intelligentbackpack.accessdata.storage.UserStorageImpl
import com.intelligentbackpack.accessdomain.entities.User
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
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
        val accessRemoteDataSource = mock(AccessRemoteDataSource::class.java)
        `when`(
            accessRemoteDataSource.accessWithData(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(expectedUser)
        val localAccessDataSource = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        val accessDomainRepository = AccessDomainRepositoryImpl(localAccessDataSource, accessRemoteDataSource)
        assertFalse(localAccessDataSource.isUserSaved())
        accessDomainRepository.loginWithData("test@gmail.com", "Test#1234", { user ->
            verify(accessRemoteDataSource).accessWithData("test@gmail.com", "Test#1234")
            assertEquals(expectedUser, user)
            assertEquals(expectedUser, localAccessDataSource.getUser())
            assertTrue(localAccessDataSource.isUserSaved())
        }, {
            assert(false)
        })
    }

    @Test
    fun automaticLoginWithUserSaved() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessRemoteDataSource = mock(AccessRemoteDataSource::class.java)
        val accessLocalDataSource = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        accessLocalDataSource.saveUser(expectedUser)
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataSource, accessRemoteDataSource)
        assertTrue(accessLocalDataSource.isUserSaved())
        accessDomainRepository.isUserLogged({
            assertTrue(it)
            accessDomainRepository.automaticLogin({ user ->
                verify(accessRemoteDataSource, never()).accessWithData("test@gmail.com", "Test#1234")
                assertEquals(expectedUser, user)
            }, {
                assert(false)
            })
        }, {
            assert(false)
        })
    }

    @Test
    fun automaticLoginWithoutUserSavedCreateException() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessRemoteDataSource = mock(AccessRemoteDataSource::class.java)
        val accessLocalDataStorage = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataStorage, accessRemoteDataSource)
        assertFalse(accessLocalDataStorage.isUserSaved())
        accessDomainRepository.automaticLogin({
            assert(false)
        }, {
            assertTrue(it is MissingUserException)
        })
    }

    @Test
    fun logout() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val accessRemoteDataSource = mock(AccessRemoteDataSource::class.java)
        val accessLocalDataSource = AccessLocalDataSourceImpl(UserStorageImpl(appContext))
        accessLocalDataSource.saveUser(expectedUser)
        val accessDomainRepository = AccessDomainRepositoryImpl(accessLocalDataSource, accessRemoteDataSource)
        accessDomainRepository.isUserLogged({ logged ->
            assertTrue(logged)
            accessDomainRepository.logoutUser({ user ->
                assertEquals(expectedUser, user)
                accessDomainRepository.isUserLogged({ loggedAfterLogout ->
                    assertFalse(loggedAfterLogout)
                    assertFalse(accessLocalDataSource.isUserSaved())
                }, {
                    assert(false)
                })
            }, {
                assert(false)
            })
        }, {
            assert(false)
        })
    }
}
