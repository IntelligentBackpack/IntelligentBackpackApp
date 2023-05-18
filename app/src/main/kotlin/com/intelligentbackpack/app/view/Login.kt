package com.intelligentbackpack.app.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.view.form.LoginForm
import com.intelligentbackpack.app.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory,
    ),
) {
    var openErrorDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    if (openErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                openErrorDialog = false
            },
            title = {
                Text(text = "Login error")
            },
            text = {
                Text(error)
            },
            confirmButton = {
            },
            dismissButton = {
                Button(
                    onClick = {
                        openErrorDialog = false
                    },
                ) {
                    Text("Ok")
                }
            },
        )
    }
    var openLoadingDialog by remember { mutableStateOf(false) }
    if (openLoadingDialog) {
        AlertDialog(
            modifier = Modifier.fillMaxSize(0.7f),
            onDismissRequest = {
                openLoadingDialog = false
            },
            content = {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )
    }
    LaunchedEffect(Unit) {
        loginViewModel.isUserLogged { isUserLogged ->
            if (isUserLogged) {
                loginViewModel.tryAutomaticLogin({
                    openLoadingDialog = false
                    navController.navigate(MainNavigation.home)
                }) {
                    openLoadingDialog = false
                    openErrorDialog = true
                    error = it
                }
                openLoadingDialog = true
            }
        }
    }
    val login =
        { email: String, password: String ->
            loginViewModel.login(
                email,
                password,
                {
                    openLoadingDialog = false
                    navController.navigate(MainNavigation.home)
                },
            ) { errorText ->
                openLoadingDialog = false
                error = errorText
                openErrorDialog = true
            }
            openLoadingDialog = true
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
