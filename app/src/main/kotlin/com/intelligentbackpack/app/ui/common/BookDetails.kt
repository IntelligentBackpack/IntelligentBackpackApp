package com.intelligentbackpack.app.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.intelligentbackpack.app.viewdata.BookView

@Composable
fun BookDetails(
    book: BookView,
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
