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
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.SchoolSupplyView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolSupplyCard(
    navHostController: NavHostController,
    schoolSupply: SchoolSupplyView,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = {
            navHostController.navigate(MainNavigation.schoolSupply(schoolSupply.rfidCode))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = schoolSupply.type)
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp, color = Color.Black
            )
            schoolSupply.book?.let { BookDetails(it) }
        }
    }
}

@Preview
@Composable
fun SchoolSupplyCardPreview() {
    SchoolSupplyCard(
        navHostController = rememberNavController(),
        schoolSupply = SchoolSupplyView(
            rfidCode = "1234567890",
            type = "Book",
            book = BookView(
                title = "The Intelligent Backpack",
                authors = setOf("John Doe"),
                isbn = "1234567890",
            )
        ),
        Modifier.fillMaxWidth(0.9f),
    )
}