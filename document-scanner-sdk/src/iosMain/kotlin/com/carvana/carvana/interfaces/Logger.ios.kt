package com.carvana.carvana.interfaces

import platform.Foundation.NSLog

actual val logger: Logger = object : Logger {
    override fun d(tag: String, message: String) {
        NSLog("DEBUG: $tag: $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        val errorMessage = throwable?.message ?: "No error"
        NSLog("ERROR: $tag: $message, Throwable: $errorMessage")
    }
}
