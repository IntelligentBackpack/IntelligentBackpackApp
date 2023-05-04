package com.intelligentbackpack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.ui.theme.IntelligentBackpackAppTheme
import com.intelligentbackpack.app.view.CreateUser
import com.intelligentbackpack.app.view.Home
import com.intelligentbackpack.app.view.Login
import com.intelligentbackpack.app.view.UserDetails

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
            IntelligentBackpackAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = MainNavigation.login) {

                        composable(MainNavigation.login) {
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner
                            ) {
                                Login(navController)
                            }
                        }
                        composable(
                            MainNavigation.createUser,
                            arguments = listOf(navArgument(MainNavigation.createUserParam) { nullable = true })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString(MainNavigation.createUserParam)
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner
                            ) {
                                CreateUser(
                                    navController = navController,
                                    email = email ?: "",
                                )
                            }

                        }
                        composable(MainNavigation.home) {
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner
                            ) {
                                Home(navController = navController)
                            }
                        }
                        composable(MainNavigation.user) {
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner
                            ) {
                                UserDetails(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
