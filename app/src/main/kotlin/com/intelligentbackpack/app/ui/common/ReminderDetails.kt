package com.intelligentbackpack.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.EventView
import com.intelligentbackpack.app.viewdata.ReminderView
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import java.time.LocalDate

/**
 * Details of a reminder.
 *
 * @param modifier Modifier.
 * @param reminder Reminder to display.
 * @param onEditClicker Callback for when the edit button is clicked.
 * @param onDeleteClicker Callback for when the delete button is clicked.
 * @param isUserProfessor Whether the user is a professor, and thus can edit/delete the reminder.
 */
@Composable
fun ReminderDetails(
    modifier: Modifier = Modifier,
    reminder: ReminderView,
    onEditClicker: () -> Unit,
    onDeleteClicker: () -> Unit,
    isUserProfessor: Boolean,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
    ) {
        reminder.schoolSupplyView.book?.let {
            BookDetails(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                book = it,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(if (isUserProfessor) 0.8f else 1f),
            horizontalAlignment = Alignment.Start,
        ) {
            reminder.date?.let { Text(text = "Date: $it") }
            reminder.fromDate?.let { Text(text = "From: $it") }
            reminder.toDate?.let { Text(text = "To: $it") }
        }
        if (isUserProfessor) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End,
            ) {
                IconButton(onClick = onEditClicker) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                    )
                }
                IconButton(onClick = onDeleteClicker) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ReminderDetailsPreview() {
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
            EventView(
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
            ),
            LocalDate.of(2021, 10, 10).toString(),
            LocalDate.of(2021, 10, 17).toString(),
            null,

        )
    ReminderDetails(
        reminder = reminderView,
        onEditClicker = {},
        onDeleteClicker = {},
        isUserProfessor = true,
    )
}
