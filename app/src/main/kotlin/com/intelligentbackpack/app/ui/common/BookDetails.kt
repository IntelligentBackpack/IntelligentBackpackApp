package com.intelligentbackpack.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.intelligentbackpack.app.viewdata.BookView

/**
 * Displays the details of a book.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param book The book to display.
 */
@Composable
fun BookDetails(
    modifier: Modifier = Modifier,
    book: BookView,
) {
    Column(
        modifier = modifier,
    ) {
        Text(text = "Title: ${book.title}")
        Text(text = "ISBN: ${book.isbn}")
        if (book.authors.isNotEmpty()) {
            if (book.authors.size == 1) {
                Text(text = "Author: ${book.authors.first()}")
            } else {
                Text(text = "Authors:")
                for (author in book.authors) {
                    Text(text = "- $author")
                }
            }
        }
    }
}
