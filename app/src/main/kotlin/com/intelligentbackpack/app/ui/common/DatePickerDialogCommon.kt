package com.intelligentbackpack.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

/**
 * Component that displays a date picker dialog.
 *
 * @param selectedDate Date to display as selected.
 * @param onDateSelected Callback for when a date is selected.
 * @param onDismissed Callback for when the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogCommon(
    selectedDate: LocalDate,
    onDateSelected: (date: LocalDate) -> Unit,
    onDismissed: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        Calendar.getInstance(Locale.getDefault()).apply {
            set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        }.timeInMillis + ZoneId.systemDefault().rules.getOffset(Instant.now()).totalSeconds * 1000,
    )

    DatePickerDialog(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = onDismissed,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        )
                    }
                },
                modifier = Modifier
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Ok", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissed,
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
                Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        content = {
            androidx.compose.material3.DatePicker(state = datePickerState, modifier = Modifier)
        },
        colors = DatePickerDefaults.colors(
            selectedDayContentColor = Color.White,
        ),
    )
}
