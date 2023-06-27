package com.intelligentbackpack.app.view

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.view.form.CreateUserForm
import com.intelligentbackpack.app.viewdata.UserView
import com.intelligentbackpack.app.viewmodel.LoginViewModel

@Composable
fun CreateUser(
    navController: NavHostController,
    email: String,
    loginViewModel: LoginViewModel = viewModel(),
) {
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
    val createUser = { data: UserView ->
        loginViewModel.createUser(data, {
            navController.navigate(MainNavigation.home)
        }) {
            openDialog.value = true
            error.value = it
        }
    }

    BackHandler {
        navController.navigate(MainNavigation.login)
    }
    CreateUserForm(
        email = email,
        createUser = createUser,
        localFocusManager = LocalFocusManager.current,
    )
}
