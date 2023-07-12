package com.intelligentbackpack.app.view.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.EventView
import com.intelligentbackpack.app.viewdata.ReminderView
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

/**
 * Form for editing a reminder.
 *
 * @param reminderView Reminder to edit.
 * @param onDismissRequest Callback for when the form is dismissed.
 * @param onSave Callback for when the form is saved.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderForm(reminderView: ReminderView, onDismissRequest: () -> Unit, onSave: (ReminderView) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        var checkedState by remember { mutableStateOf(reminderView.date != null) }
        var date by remember { mutableStateOf(reminderView.date) }
        var fromDate by remember { mutableStateOf(reminderView.fromDate) }
        var toDate by remember { mutableStateOf(reminderView.toDate) }
        var inputDateType by remember { mutableStateOf(InputDateType.FROM) }
        var openPickerDialog by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        if (openPickerDialog) {
            DatePickerDialogCommon(
                selectedDate = selectedDate,
                onDateSelected = {
                    selectedDate = it
                    when (inputDateType) {
                        InputDateType.FROM -> fromDate = selectedDate.toString()
                        InputDateType.TO -> toDate = selectedDate.toString()
                        InputDateType.DATE -> date = selectedDate.toString()
                    }
                    openPickerDialog = false
                },
                onDismissed = { openPickerDialog = false },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checkedState,
                onCheckedChange = { checkedState = it },
            )
            Text(text = "Single date")
        }
        if (checkedState) {
            Text(text = "Date: ${date ?: fromDate}")
            Button(
                onClick = {
                    inputDateType = InputDateType.DATE
                    openPickerDialog = true
                },
                modifier = Modifier
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Select date", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            Text(text = "From: ${fromDate ?: date}")
            Button(
                onClick = {
                    inputDateType = InputDateType.FROM
                    openPickerDialog = true
                },
                modifier = Modifier
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Select date", color = MaterialTheme.colorScheme.onBackground)
            }
            Text(text = "To: ${toDate ?: LocalDate.parse(date).plusDays(7).toString()}")
            Button(
                onClick = {
                    inputDateType = InputDateType.TO
                    openPickerDialog = true
                },
                modifier = Modifier
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Select date", color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                onClick = {
                    onSave(
                        if (checkedState) {
                            reminderView.copy(
                                date = date,
                                fromDate = null,
                                toDate = null,
                            )
                        } else {
                            reminderView.copy(
                                date = null,
                                fromDate = fromDate,
                                toDate = toDate,
                            )
                        },
                    )
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
                androidx.compose.material3.Text("Confirm")
            }
            Button(
                onClick = {
                    onDismissRequest()
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
                androidx.compose.material3.Text("Cancel")
            }
        }
    }
}

private enum class InputDateType {
    FROM,
    TO,
    DATE,
}

@Composable
@Preview
fun EditReminderFormPreview() {
    val event = EventView(
        index = 0,
        date = LocalDate.of(2021, 10, 10).toString(),
        from = null,
        to = null,
        subject = "Math",
        module = "Algebra",
        day = "Monday",
        studentClass = "A1",
        professor = "John Doe",
        professorName = "John",
        professorSurname = "Doe",
        startTime = "10:00",
        endTime = "11:00",
    )
    val reminderView =
        ReminderView(
            SchoolSupplyView(
                "123456789",
                "Book",
                BookView(
                    "123456789",
                    title = "Mathematics",
                    authors = setOf("John Doe"),
                ),
            ),
            event,
            null,
            null,
            LocalDate.of(2021, 10, 10).toString(),

        )
    EditReminderForm(reminderView = reminderView, onDismissRequest = {}, onSave = {})
}
