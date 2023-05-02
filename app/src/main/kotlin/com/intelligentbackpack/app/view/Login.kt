package com.intelligentbackpack.app.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.app.ui.common.PasswordFiled
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.viewmodel.LoginViewModel

@Composable
fun Login(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory,
    )
) {
    loginViewModel.tryAutomaticLogin {
        navController.navigate(MainNavigation.home)
    }
    LoginPage(navController, loginViewModel::login)
}

@Composable
fun LoginPage(
    navController: NavHostController,
    loginFunction: (String, String, (User) -> Unit, (String) -> Unit) -> Unit,
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
                    }) {
                    Text("Ok")
                }
            }
        )
    }
    val login =
        { email: String, password: String ->
            loginFunction(
                email,
                password,
                { navController.navigate(MainNavigation.home) }
            )
            { errorText ->
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
        localFocusManager = LocalFocusManager.current,
        login,
        createUser
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(
    localFocusManager: FocusManager,
    activateLogin: (username: String, password: String) -> Unit,
    navigateToCreateUser: (username: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    ) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { TextFieldValue("Email") },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            })
        )
        PasswordFiled(
            value = password,
            onValueChange = { password = it },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = {
                activateLogin(email, password)
                localFocusManager.clearFocus()
            }),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Button(
                onClick = {
                    navigateToCreateUser(email)
                    localFocusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            )
            {
                Text("Create User")
            }
            Button(
                onClick = {
                    activateLogin(email, password)
                    localFocusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                shape = MaterialTheme.shapes.medium,
            )
            {
                Text("Login")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val navController = rememberNavController()
    LoginPage(navController) { _, _, _, _ -> }
}