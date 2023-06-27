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
import com.intelligentbackpack.app.ui.common.EventCard
import com.intelligentbackpack.app.viewmodel.CalendarViewModel
import java.time.LocalDate

/**
 * UI component that represents the calendar.
 */
@Composable
fun Calendar(
    navController: NavHostController,
    calendarViewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModel.Factory,
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
                    Text(text = "Calendar error")
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
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        val events by calendarViewModel.events.observeAsState(emptyList())
        LaunchedEffect(key1 = Unit) {
            calendarViewModel.getDateCalendar(selectedDate, {}, {
                error = it
                openErrorDialog = true
            })
        }
        CalendarTopBar { date ->
            selectedDate = date
            calendarViewModel.getDateCalendar(selectedDate, {}, {
                error = it
                openErrorDialog = true
            })
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        ) {
            items(events) { event ->
                EventCard(
                    navController,
                    event,
                )
            }
        }
    }
}

@Preview
@Composable
fun CalendarPreview() {
    val navController = rememberNavController()
    Calendar(navController)
}
