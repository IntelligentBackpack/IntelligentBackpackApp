package com.intelligentbackpack.app.ui.common

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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intelligentbackpack.app.ui.theme.IntelligentBackpackAppTheme
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CalendarTopBar(
    onDateChange: (LocalDate) -> Unit = {},
) {
    val today = LocalDate.now(ZoneId.systemDefault())
    var openPickerDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(today) }
    if (openPickerDialog) {
        DatePickerDialogCommon(selectedDate = selectedDate, onDateSelected = {
            selectedDate = it
            onDateChange(selectedDate)
            openPickerDialog = false
        }) {
            openPickerDialog = false
//
        }
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
            IconButton(onClick = {
                selectedDate = today
                onDateChange(selectedDate)
            }) {
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
                IconButton(onClick = {
                    selectedDate = selectedDate.minusDays(1)
                    onDateChange(selectedDate)
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous day",
                        tint = textColor,
                    )
                }
                IconButton(onClick = {
                    selectedDate = selectedDate.plusDays(1)
                    onDateChange(selectedDate)
                }) {
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
