package com.carvana.carvana.interfaces

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DocumentUploader {
    fun uploadDocument(onResult: (UploadResult) -> Unit)
}

sealed class UploadResult {
    data class Success(
        val recognizedText: String,
        val metadata: Map<String, String> = emptyMap()
    ) : UploadResult()
    data class Failure(val message: String) : UploadResult()
}
