package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.BookAdapter.fromDomainToView
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
                this.book.fromDomainToView()
            )

            else -> throw IllegalArgumentException("School supply type not supported")
        }
    }
}