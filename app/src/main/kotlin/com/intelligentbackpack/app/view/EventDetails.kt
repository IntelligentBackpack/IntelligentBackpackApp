package com.intelligentbackpack.app.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.intelligentbackpack.app.ui.common.BookDetails
import com.intelligentbackpack.app.ui.common.LargeDropdownMenu
import com.intelligentbackpack.app.ui.common.LargeDropdownMenuItem
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.EventView
import com.intelligentbackpack.app.viewmodel.CalendarViewModel
import java.time.LocalDate

/**
 * UI component that represents the event details screen.
 *
 * @param navController The navigation controller.
 * @param eventIndex The event index.
 * @param calendarViewModel The calendar view model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetails(
    navController: NavHostController,
    eventIndex: Int,
    calendarViewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModel.Factory,
    ),
) {
    var openErrorDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    if (openErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                openErrorDialog = false
            },
            title = {
                Text(text = "Event error")
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
    var eventView: EventView? by rememberSaveable { mutableStateOf(null) }
    var books: List<BookView> by rememberSaveable { mutableStateOf(emptyList()) }
    var isUserProfessor by rememberSaveable { mutableStateOf(false) }
    var openBookDialog by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = eventIndex) {
        eventView = calendarViewModel.getEventAt(eventIndex)
        calendarViewModel.getSuppliesForEvent(eventIndex, { supplies ->
            books = supplies.filter { it.book != null }.map { it.book!! }
        }) {
            error = it
            openErrorDialog = true
        }
        calendarViewModel.isUserProfessor({ isUserProfessor = it }) {
            error = it
            openErrorDialog = true
        }
    }
    if (openBookDialog) {
        Dialog(
            onDismissRequest = { openBookDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
        ) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    var selectedBook: BookView? by remember { mutableStateOf(null) }
                    var allBooks by remember { mutableStateOf(emptyList<BookView>()) }
                    LaunchedEffect(openBookDialog) {
                        calendarViewModel.getAllBooks({ all ->
                            allBooks = all
                            selectedBook = allBooks.first()
                        }) {
                            error = it
                            openErrorDialog = true
                        }
                    }
                    Text(text = "Select book to add to event")
                    LargeDropdownMenu(
                        label = "",
                        items = allBooks,
                        selectedIndex = 0,
                        onItemSelected = { _, bookSelected ->
                            selectedBook = bookSelected
                        },
                        selectedItemToString = { it.isbn + " - " + it.title },
                        drawItem = { item, _, itemEnabled, onClick ->
                            LargeDropdownMenuItem(
                                text = item.isbn + " - " + item.title,
                                enabled = itemEnabled,
                                onClick = onClick,
                            )
                        },
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    ) {
                        Button(
                            onClick = { openBookDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        ) {
                            Text(text = "Cancel")
                        }
                        Button(
                            onClick = {
                                val fromDate = eventView?.from ?: eventView!!.date
                                val toDate = eventView?.to ?: eventView!!.date
                                calendarViewModel.addSchoolSupplyToEvent(
                                    eventIndex,
                                    selectedBook!!.isbn,
                                    LocalDate.parse(fromDate),
                                    LocalDate.parse(toDate),
                                    {
                                        openBookDialog = false
                                        selectedBook = null
                                        calendarViewModel.getSuppliesForEvent(eventIndex, { supplies ->
                                            books = supplies.filter { it.book != null }.map { it.book!! }
                                        }) {
                                            error = it
                                            openErrorDialog = true
                                        }
                                    },
                                    {
                                        error = it
                                        openErrorDialog = true
                                    },
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        ) {
                            Text(text = "Add")
                        }
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopAppBar(
                title = { Text("Event details", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(MainNavigation.home)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            )
            eventView?.let {
                EventDetailsPage(
                    event = it,
                    books = books,
                )
            }
        }
        if (isUserProfessor) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .align(alignment = Alignment.BottomEnd),
                shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                onClick = {
                    openBookDialog = true
                },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    }
}

/**
 * UI for the event details page.
 *
 * @param event The event to display.
 * @param books The books to display.
 */
@Composable
fun EventDetailsPage(
    event: EventView,
    books: List<BookView>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
            ) {
                if (event.date != null) {
                    Text(text = event.date.toString())
                } else {
                    Text(text = event.from.toString() + " - " + event.to.toString())
                }
                Text(text = event.subject)
                Text(text = event.module)
                Text(text = event.day)
                Text(text = event.studentClass)
                Text(text = event.professorName + " " + event.professorSurname)
                Text(text = event.startTime + " - " + event.endTime)
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.Black,
                )
                Text(text = "Books:")
            }
        }
        items(books) { book ->
            BookDetails(book = book)
        }
    }
}

@Preview
@Composable
fun EventDetailsPagePreview() {
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
    val schoolSupplies = listOf(
        BookView(
            "123456789",
            title = "Mathematics",
            authors = setOf("John Doe"),
        ),
    )
    EventDetailsPage(
        event = event,
        books = schoolSupplies,
    )
}
