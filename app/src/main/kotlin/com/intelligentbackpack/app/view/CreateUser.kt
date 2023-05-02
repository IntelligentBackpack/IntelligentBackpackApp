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
import com.intelligentbackpack.app.viewdata.UserView
import com.intelligentbackpack.app.viewmodel.LoginViewModel

@Composable
fun CreateUser(
    navController: NavHostController,
    email: String,
    loginViewModel: LoginViewModel = viewModel(
    )
) {
    CreateUserPage(navController = navController, email = email) { user, onSuccess, onError ->
        loginViewModel.createUser(user, onSuccess, onError)
    }
}

@Composable
fun CreateUserPage(
    navController: NavHostController,
    email: String,
    createUserFunction: (user: UserView, (User) -> Unit, (String) -> Unit) -> Unit
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
    val createUser = { data: UserView ->
        createUserFunction(data, {
            navController.navigate(MainNavigation.home)
        }) {
            openDialog.value = true
            error.value = it
        }
    }

    BackHandler {
        navController.navigate(MainNavigation.login)
    }

    CreateForm(
        localFocusManager = LocalFocusManager.current,
        email,
        createUser = createUser
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateForm(
    localFocusManager: FocusManager,
    emailInsert: String,
    createUser: (user: UserView) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    ) {
        var email by rememberSaveable { mutableStateOf(emailInsert) }
        var name by rememberSaveable { mutableStateOf("") }
        var surname by rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val passwordConfirm = rememberSaveable { mutableStateOf("") }
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
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { TextFieldValue("Name") },
            label = { Text(text = "Name") },
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
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            placeholder = { TextFieldValue("Surname") },
            label = { Text(text = "Surname") },
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
            password = password,
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            }),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        PasswordFiled(
            password = passwordConfirm,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = {
                localFocusManager.clearFocus()
            }),
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceAround
        )
        {
            Button(
                onClick = {
                    createUser(
                        UserView(
                            email = email,
                            name = name,
                            surname = surname,
                            password = password.value
                        )
                    )
                    localFocusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            )
            {
                Text("Create User")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserPreview() {
    val navController = rememberNavController()
    CreateUserPage(navController, "") { _, _, _ -> }
}
