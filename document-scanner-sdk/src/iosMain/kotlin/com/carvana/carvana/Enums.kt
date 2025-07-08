package com.carvana.carvana

/** Enum class for iOS document UTTypes -> iOS upload **/
enum class IOSDocumentType(val value: String) {
    IMAGE("public.image"),
    PDF("com.adobe.pdf"),
    TEXT("public.text"),
    DATA("public.data");

    companion object {
        fun getAllTypes(): List<String> = entries.map { it.value }
    }
}

enum class FileExtension(val value: String) {
    TXT("txt"),
    PDF("pdf"),
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    HEIC("heic");

    companion object {

        fun fromString(extension: String): FileExtension? = 
            entries.find { it.value.equals(extension, ignoreCase = true) }
    }
}