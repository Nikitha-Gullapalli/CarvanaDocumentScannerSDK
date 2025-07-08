package com.carvana.carvana.interfaces

interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

expect val logger: Logger
