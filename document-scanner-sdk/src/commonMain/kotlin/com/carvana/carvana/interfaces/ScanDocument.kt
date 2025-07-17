package com.carvana.carvana.interfaces

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DocumentScanner {
    fun scanDocument(onResult: (ScanResult) -> Unit)
}

sealed class ScanResult {
    data class Success(
        val recognizedText: String, 
        val pdfPath: String,
        val metadata: Map<String, String> = emptyMap()
    ) : ScanResult()
    data class Failure(val message: String) : ScanResult()
}

