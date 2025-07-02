package com.carvana.carvana.sdk

/**
 * Main SDK interface for Carvana Document Scanner
 * This is what calling apps will interact with
 */
interface CarvanaDocumentScannerSDK {
    /**
     * Initialize the SDK with configuration
     */
    fun initialize(config: SDKConfiguration)
    
    /**
     * Scan documents using platform-specific scanner
     * @param options Scanning options
     * @param callback Called when scan completes or fails
     */
    fun scanDocument(
        options: ScanOptions = ScanOptions(),
        callback: (DocumentResult) -> Unit
    )
    
    /**
     * Upload document from device
     * @param options Upload options
     * @param callback Called when upload completes or fails
     */
    fun uploadDocument(
        options: UploadOptions = UploadOptions(),
        callback: (DocumentResult) -> Unit
    )
    
    /**
     * Process a document from URI/path
     * @param documentUri Platform-specific document identifier
     * @param callback Called when processing completes
     */
    suspend fun processDocument(
        documentUri: Any,
        callback: (DocumentResult) -> Unit
    )
    
    /**
     * Get current SDK version
     */
    fun getVersion(): String
}

/**
 * SDK Configuration
 */
data class SDKConfiguration(
    val apiKey: String? = null,
    val enableOCR: Boolean = true,
    val enablePDFGeneration: Boolean = true,
    val maxFileSize: Long = 10 * 1024 * 1024, // 10MB
    val supportedFormats: List<DocumentFormat> = DocumentFormat.entries,
    val cacheDocuments: Boolean = true,
    val debugMode: Boolean = false
)

/**
 * Scanning options
 */
data class ScanOptions(
    val enableMultiPage: Boolean = true,
    val imageQuality: ImageQuality = ImageQuality.HIGH,
    val outputFormat: DocumentFormat = DocumentFormat.PDF,
    val enableEnhancement: Boolean = true
)

/**
 * Upload options
 */
data class UploadOptions(
    val allowedFormats: List<DocumentFormat> = DocumentFormat.values().toList(),
    val maxFileSize: Long? = null // null means use SDK config
)

/**
 * Unified document result returned to calling app
 */
sealed class DocumentResult {
    data class Success(
        val document: Document,
        val metadata: DocumentMetadata
    ) : DocumentResult()
    
    data class Failure(
        val error: DocumentError,
        val message: String
    ) : DocumentResult()
}

/**
 * Document representation
 */
data class Document(
    val id: String,
    val uri: String, // Platform-specific URI that calling app can use
    val format: DocumentFormat,
    val pages: List<DocumentPage>,
    val extractedText: String? = null,
    val pdfData: ByteArray? = null // For PDF generation
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Document

        if (id != other.id) return false
        if (uri != other.uri) return false
        if (format != other.format) return false
        if (pages != other.pages) return false
        if (extractedText != other.extractedText) return false
        if (pdfData != null) {
            if (other.pdfData == null) return false
            if (!pdfData.contentEquals(other.pdfData)) return false
        } else if (other.pdfData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + pages.hashCode()
        result = 31 * result + (extractedText?.hashCode() ?: 0)
        result = 31 * result + (pdfData?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * Document page information
 */
data class DocumentPage(
    val pageNumber: Int,
    val imageUri: String?, // URI to page image if available
    val text: String? = null,
    val width: Int,
    val height: Int
)

/**
 * Document metadata
 */
data class DocumentMetadata(
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val createdAt: Long = System.currentTimeMillis(),
    val source: DocumentSource,
    val processingTime: Long? = null,
    val additionalInfo: Map<String, String> = emptyMap()
)

/**
 * Document formats supported by SDK
 */
enum class DocumentFormat {
    PDF,
    JPEG,
    PNG,
    TEXT,
    WORD,
    UNKNOWN
}

/**
 * Document source
 */
enum class DocumentSource {
    CAMERA_SCAN,
    FILE_UPLOAD,
    EXTERNAL_URI
}

/**
 * Image quality options
 */
enum class ImageQuality {
    LOW,    // Fast, smaller file size
    MEDIUM, // Balanced
    HIGH    // Best quality, larger file size
}

/**
 * Error types
 */
enum class DocumentError {
    INITIALIZATION_ERROR,
    INVALID_FORMAT,
    FILE_TOO_LARGE,
    SCAN_CANCELLED,
    SCAN_FAILED,
    UPLOAD_FAILED,
    PROCESSING_FAILED,
    PERMISSION_DENIED,
    OCR_FAILED,
    PDF_GENERATION_FAILED,
    UNKNOWN_ERROR
}