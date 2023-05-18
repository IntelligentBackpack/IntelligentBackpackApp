package com.intelligentbackpack.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.intelligentbackpack.app.ui.theme.IntelligentBackpackAppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
    onDateChange: (LocalDate) -> Unit = {},
) {
    val today = LocalDate.now(ZoneId.systemDefault())
    var openPickerDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(today) }
    if (openPickerDialog) {
        val datePickerState = rememberDatePickerState(
            Calendar.getInstance(Locale.getDefault()).apply {
                set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
            }.timeInMillis + ZoneId.systemDefault().rules.getOffset(Instant.now()).totalSeconds * 1000,
        )

        DatePickerDialog(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = { openPickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDate = datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        onDateChange(selectedDate)
                        openPickerDialog = false
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
                    onClick = {
                        openPickerDialog = false
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
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            content = {
                DatePicker(state = datePickerState, modifier = Modifier)
            },
            colors = DatePickerDefaults.colors(
                selectedDayContentColor = Color.White,
            ),
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        val textColor = MaterialTheme.colorScheme.onSurface
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { openPickerDialog = true }) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(text = "Select date", color = textColor)
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Select date",
                        tint = textColor,
                    )
                }
            }
            IconButton(onClick = { selectedDate = today }) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(text = "Today", color = textColor)
                    Icon(imageVector = Icons.Outlined.Today, contentDescription = "Today", tint = textColor)
                }
            }
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous day",
                        tint = textColor,
                    )
                }
                IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next day",
                        tint = textColor,
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            color = textColor,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val month = selectedDate.month.name.firstLetterToUpperCase()
            val day = selectedDate.dayOfMonth
            val year = selectedDate.year
            val dayOfWeek = selectedDate.dayOfWeek.name.firstLetterToUpperCase()
            Text(text = "$dayOfWeek $month $day, $year", color = textColor)
        }
    }
}

fun String.firstLetterToUpperCase(): String =
    this.lowercase().replaceFirstChar { it.uppercase() }

@Preview
@Composable
fun CalendarTopBarPreview() {
    IntelligentBackpackAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            CalendarTopBar()
        }
    }
}
