package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.desktopdomain.entities.Book

object BookAdapter {

    fun Book.fromDomainToView() = BookView(
        isbn = isbn,
        title = title,
        authors = authors
    )
}
