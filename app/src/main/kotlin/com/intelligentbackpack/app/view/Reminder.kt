package com.intelligentbackpack.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.intelligentbackpack.app.ui.common.CalendarTopBar
import com.intelligentbackpack.app.ui.common.SchoolSupplyCard
import com.intelligentbackpack.app.viewmodel.ReminderViewModel
import java.time.LocalDate

/**
 * UI component that represents the reminder.
 *
 * @param navController the navigation controller.
 * @param reminderViewModel the view model.
 */
@Composable
fun Reminder(
    navController: NavHostController,
    reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModel.Factory,
    ),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var openErrorDialog by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf("") }
        if (openErrorDialog) {
            AlertDialog(
                onDismissRequest = {
                    openErrorDialog = false
                },
                title = {
                    Text(text = "Reminder error")
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
        var isBackpackAssociated by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        val missing by reminderViewModel.missing.observeAsState(emptyList())
        LaunchedEffect(key1 = Unit) {
            reminderViewModel.isBackpackAssociated({
                if (it) {
                    isBackpackAssociated = true
                    reminderViewModel.getReminders(selectedDate) { message ->
                        error = message
                        openErrorDialog = true
                    }
                } else {
                    isBackpackAssociated = false
                }
            }, {
                error = it
                openErrorDialog = true
            })
        }
        CalendarTopBar { date ->
            selectedDate = date
            if (isBackpackAssociated) {
                reminderViewModel.getReminders(selectedDate) {
                    error = it
                    openErrorDialog = true
                }
            }
        }
        if (!isBackpackAssociated) {
            Text(text = "No backpack associated")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            ) {
                items(missing) { missing ->
                    SchoolSupplyCard(navHostController = navController, schoolSupply = missing)
                }
            }
        }
    }
}

@Preview
@Composable
fun ReminderPreview() {
    val navController = rememberNavController()
    Reminder(navController)
}
