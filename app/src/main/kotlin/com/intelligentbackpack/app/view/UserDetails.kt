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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import com.intelligentbackpack.app.viewmodel.LoginViewModel

@Composable
fun UserDetails(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory,
    ),
) {
    val user = loginViewModel.user.observeAsState()
    if (user.value == null) {
        navController.navigate(MainNavigation.login)
    }
    UserDetailsPage(
        navController = navController,
        user = user.value!!,
        logout = {
            loginViewModel.logout {
                navController.navigate(MainNavigation.login)
            }
        },
        delete = {
            loginViewModel.deleteUser {
                navController.navigate(MainNavigation.login)
            }
        },
    )
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
        Role.TEACHER -> "Teacher"
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
