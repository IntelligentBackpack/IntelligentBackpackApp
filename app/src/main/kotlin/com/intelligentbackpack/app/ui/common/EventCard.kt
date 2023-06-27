package com.intelligentbackpack.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.viewdata.EventView

/**
 * UI component that represents an event card.
 *
 * @param navHostController The navigation controller.
 * @param eventView The event view data.
 * @param modifier The modifier.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    navHostController: NavHostController,
    eventView: EventView,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        onClick = {
            navHostController.navigate(MainNavigation.event(eventView.index))
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(text = eventView.studentClass + " " + eventView.subject)
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = Color.Black,
            )
            Text(text = eventView.module)
            Text(text = eventView.professorName + " " + eventView.professorSurname)
            Text(text = eventView.startTime + " - " + eventView.endTime)
        }
    }
}

@Preview
@Composable
fun EventCardPreview() {
    EventCard(
        navHostController = rememberNavController(),
        eventView = EventView(
            index = 0,
            date = "2021-01-01",
            day = "Friday",
            subject = "Math",
            module = "Math",
            professor = "test",
            professorName = "John",
            professorSurname = "Doe",
            startTime = "08:00",
            endTime = "09:00",
            from = null,
            to = null,
            studentClass = "1A",
        ),
        Modifier.fillMaxWidth(0.9f),
    )
}
