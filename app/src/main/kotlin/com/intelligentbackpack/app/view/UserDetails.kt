package com.intelligentbackpack.app.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.viewdata.UserView
import com.intelligentbackpack.app.viewmodel.UserViewModel

@Composable
fun UserDetails(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory,
    ),
) {
    val openDialog = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf("") }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Operation error")
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
    LaunchedEffect(key1 = null) {
        userViewModel.getUser({}, {
            openDialog.value = true
            error.value = it
        })
    }
    val user = userViewModel.user.observeAsState()
    user.value?.let {
        UserDetailsPage(
            navController = navController,
            user = user.value!!,
            logout = {
                userViewModel.logout({
                    navController.navigate(MainNavigation.login)
                }, {
                    openDialog.value = true
                    error.value = it
                })
            },
            delete = {
                userViewModel.deleteUser({
                    navController.navigate(MainNavigation.login)
                }, {
                    openDialog.value = true
                    error.value = it
                })
            },
        )
    }
}

@Composable
fun UserDetailsPage(navController: NavHostController, user: UserView, logout: () -> Unit, delete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.navigate(MainNavigation.home) }) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            title = { Text("User details") },
            backgroundColor = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(12.dp))
        Icon(
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = "Account",
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
        )
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(text = "User", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Text(text = "Name: ${user.name}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Text(text = "Surname: ${user.surname}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Text(text = "Role: ${user.role}", style = MaterialTheme.typography.bodyMedium)
        }
        Button(
            onClick = delete,
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
            Text("Delete user")
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = logout,
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .padding(top = 10.dp),
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
            ),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("Logout")
        }
    }
}

fun Role.toText(): String {
    return when (this) {
        Role.USER -> "User"
        Role.PROFESSOR -> "Professor"
        Role.STUDENT -> "Student"
        else -> ""
    }
}

@Composable
@Preview
fun UserDetailsPreview() {
    val navController = rememberNavController()
    val user = UserView(
        name = "John",
        surname = "Doe",
        email = "JohnDoe@gmail.com",
        password = "Test#1234",
        role = Role.USER.toText(),
    )
    UserDetailsPage(navController, user, {}) {}
}
