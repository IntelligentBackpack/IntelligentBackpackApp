package com.intelligentbackpack.app.view.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.intelligentbackpack.app.ui.common.EmailField
import com.intelligentbackpack.app.ui.common.PasswordField
import com.intelligentbackpack.app.viewdata.UserView

/**
 * Form for creating a new user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserForm(
    email: String,
    createUser: (user: UserView) -> Unit,
    localFocusManager: FocusManager = LocalFocusManager.current,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        var emailRemember by rememberSaveable { mutableStateOf(email) }
        var name by rememberSaveable { mutableStateOf("") }
        var surname by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordConfirm by rememberSaveable { mutableStateOf("") }
        EmailField(
            value = emailRemember,
            onValueChange = { emailRemember = it },
            modifier = Modifier.fillMaxWidth(0.8f),
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            }),
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
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            }),
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
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            }),
        )
        PasswordField(
            value = password,
            onValueChange = { password = it },
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            }),
            modifier = Modifier.fillMaxWidth(0.8f),
        )
        PasswordField(
            value = passwordConfirm,
            onValueChange = { passwordConfirm = it },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = {
                localFocusManager.clearFocus()
            }),
            modifier = Modifier.fillMaxWidth(0.8f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Button(
                onClick = {
                    createUser(
                        UserView(
                            email = emailRemember,
                            name = name,
                            surname = surname,
                            password = password,
                        ),
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
            ) {
                Text("Create User")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserPreview() {
    CreateUserForm("", {})
}
