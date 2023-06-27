package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.BookAdapter.fromDomainToView
import com.intelligentbackpack.app.viewdata.adapter.BookAdapter.fromViewToDomain
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply

/**
 * Adapter to convert a [SchoolSupply] to a [SchoolSupplyView].
 */
object SchoolSupplyAdapter {
    /**
     * Converts a [SchoolSupply] to a [SchoolSupplyView].
     */
    fun SchoolSupply.fromDomainToView(): SchoolSupplyView {
        return when (this) {
            is BookCopy -> SchoolSupplyView(
                this.rfidCode,
                this.type,
                this.book.fromDomainToView(),
            )

            else -> throw IllegalArgumentException("School supply type not supported")
        }
    }

    /**
     * Converts a [SchoolSupplyView] to a [SchoolSupply].
     */
    fun SchoolSupplyView.fromViewToDomain(): SchoolSupply {
        return when (this.type) {
            "Book" -> BookCopy.build {
                this.rfidCode = this@fromViewToDomain.rfidCode
                this.book = this@fromViewToDomain.book?.fromViewToDomain()
                    ?: throw IllegalArgumentException("Book copy without book")
            }

            else -> throw IllegalArgumentException("School supply type not supported")
        }
    }
}
