package com.intelligentbackpack.app.view.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intelligentbackpack.app.ui.common.EmailField
import com.intelligentbackpack.app.ui.common.PasswordField

/**
 * Form for logging in.
 */
@Composable
fun LoginForm(
    login: (username: String, password: String) -> Unit,
    openNewPage: (username: String) -> Unit,
    localFocusManager: FocusManager = LocalFocusManager.current,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        EmailField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(0.8f),
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            }),
        )
        PasswordField(
            value = password,
            onValueChange = { password = it },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = {
                login(email, password)
                localFocusManager.clearFocus()
            }),
            modifier = Modifier.fillMaxWidth(0.8f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                onClick = {
                    openNewPage(email)
                    localFocusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Create User")
            }
            Button(
                onClick = {
                    login(email, password)
                    localFocusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                ),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Login")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginForm({ _, _ -> }, {})
}
