package com.intelligentbackpack.app.view.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intelligentbackpack.app.ui.common.BookDetails
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.SchoolSupplyView

/**
 * Form for viewing a school supply.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolSupplyDetailsForm(
    schoolSupplyView: SchoolSupplyView,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        TopAppBar(
            title = { Text("Book copy") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(0.8f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            schoolSupplyView.book?.let {
                BookDetails(book = it)
            }
        }
    }
}

@Preview
@Composable
fun SchoolSupplyDetailsFormPreview() {
    SchoolSupplyDetailsForm(
        schoolSupplyView = SchoolSupplyView(
            book = BookView(
                title = "The Hobbit",
                authors = setOf("J.R.R. Tolkien"),
                isbn = "9780547928227",
            ),
            rfidCode = "1234567890",
            type = "Book",
        ),
        onBack = {},
    )
}
