package com.intelligentbackpack.app.view

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.view.form.LoginForm
import com.intelligentbackpack.app.viewmodel.LoginViewModel

@Composable
fun Login(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory,
    ),
) {
    LaunchedEffect(Unit) {
        loginViewModel.tryAutomaticLogin({
            navController.navigate(MainNavigation.home)
        }) {
            loginViewModel.logout {
                navController.navigate(MainNavigation.login)
            }
        }
    }
    val openDialog = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf("") }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Login error")
            },
            text = {
                Text(error.value)
            },
            confirmButton = {
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    },
                ) {
                    Text("Ok")
                }
            },
        )
    }
    val login =
        { email: String, password: String ->
            loginViewModel.login(
                email,
                password,
                { navController.navigate(MainNavigation.home) },
            ) { errorText ->
                error.value = errorText
                openDialog.value = true
            }
        }
    val createUser = { email: String ->
        navController.navigate(MainNavigation.createUser(email.trim()))
    }
    BackHandler {
        navController.navigate(MainNavigation.login)
    }

    LoginForm(
        login = login,
        openNewPage = createUser,
    )
}
