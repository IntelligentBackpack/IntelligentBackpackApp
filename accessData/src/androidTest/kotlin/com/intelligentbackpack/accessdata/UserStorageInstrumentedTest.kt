package com.intelligentbackpack.accessdata

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.intelligentbackpack.accessdata.storage.UserStorage
import com.intelligentbackpack.accessdata.storage.UserStorageImpl
import com.intelligentbackpack.accessdomain.entities.User
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 * Tests for [UserStorage]
 */
@RunWith(AndroidJUnit4::class)
class UserStorageInstrumentedTest {

    @Test
    fun initiallyAUserIsntLogged() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val storage: UserStorage = UserStorageImpl(appContext)
        assertFalse(storage.isUserSaved())
    }

    @Test
    fun saveUser() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val storage: UserStorage = UserStorageImpl(appContext)
        val expectedUser = User.build {
            email = "test@gmail.com"
            password = "Test#1234"
            name = "test"
            surname = "test"
        }
        storage.saveUser(expectedUser)
        assertTrue(storage.isUserSaved())
        val savedUser = storage.getUser()
        assertEquals(expectedUser, savedUser)
    }

    @Test
    fun noUserSaved() {
        val appContext =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val storage: UserStorage = UserStorageImpl(appContext)
        assertFalse(storage.isUserSaved())
        assertThrows(IllegalStateException::class.java) {
            storage.getUser()
        }
    }

    @Test
    fun deleteUser() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage: UserStorage = UserStorageImpl(appContext)
        val expectedUser = User.build {
            email = "test@gmail.com"
            password = "Test#1234"
            name = "test"
            surname = "test"
        }
        storage.saveUser(expectedUser)
        assertTrue(storage.isUserSaved())
        storage.deleteUser()
        assertFalse(storage.isUserSaved())
    }

    @Test
    fun passwordIsEncrypted() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage: UserStorage = UserStorageImpl(appContext)
        val expectedUser = User.build {
            email = "test@gmail.com"
            password = "Test#1234"
            name = "test"
            surname = "test"
        }
        storage.saveUser(expectedUser)
        assertTrue(storage.isUserSaved())
        assertFalse(
            isPresentElementsFromSharedPreferences(appContext, "password"),
        )
    }

    private fun isPresentElementsFromSharedPreferences(context: Context, field: String): Boolean {
        val sharedPref = context.getSharedPreferences("IntelligentBackpackSharedPref", Context.MODE_PRIVATE)
        return sharedPref.contains(field)
    }
}
