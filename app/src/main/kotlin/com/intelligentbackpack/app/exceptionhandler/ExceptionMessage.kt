package com.intelligentbackpack.app.exceptionhandler

object ExceptionMessage {

    fun Throwable.messageOrDefault(): String {
        return this.message ?: "Unknown error"
    }
}
