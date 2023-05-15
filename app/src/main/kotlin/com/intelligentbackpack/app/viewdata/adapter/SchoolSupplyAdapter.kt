package com.intelligentbackpack.app.viewdata.adapter

import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.BookAdapter.fromDomainToView
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply

object SchoolSupplyAdapter {
    fun SchoolSupply.fromDomainToView(): SchoolSupplyView {
        when (this) {
            is BookCopy -> return SchoolSupplyView(
                this.rfidCode,
                this.type,
                this.book.fromDomainToView()
            )

            else -> throw IllegalArgumentException("School supply type not supported")
        }
    }
}