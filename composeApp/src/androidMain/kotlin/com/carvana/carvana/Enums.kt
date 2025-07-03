package com.carvana.carvana

/** Enum class for document MIME types -> Android upload**/
enum class AndroidDocumentMimeType(val value: String) {
    IMAGE("image/*"),
    PDF("application/pdf"),
    TEXT("text/*");

    companion object {
        fun getAllTypes(): Array<String> = entries.map { it.value }.toTypedArray()
    }
}