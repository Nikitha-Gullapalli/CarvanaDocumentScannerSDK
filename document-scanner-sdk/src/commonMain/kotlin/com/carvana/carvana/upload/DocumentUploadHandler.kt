package com.carvana.carvana.upload

import com.carvana.carvana.interfaces.UploadResult

/**
 * Common interface for document upload handling
 */
interface DocumentUploadHandler {
    /**
     * Process an uploaded document
     * @param fileData Platform-specific file representation
     * @return UploadResult with success or failure information
     */
    suspend fun processUploadedDocument(fileData: Any): UploadResult

    /**
     * Validate file before processing
     * @param fileName Name of the file
     * @param fileSize Size of the file in bytes
     * @param mimeType MIME type of the file
     * @return Error message if validation fails, null if valid
     */
    fun validateFile(fileName: String, fileSize: Long, mimeType: String): String?

    /**
     * Clean up old cached files
     * @param daysToKeep Number of days to keep files
     */
    fun cleanupOldFiles(daysToKeep: Int = 7)
}

/**
 * Common file information data class
 */
data class FileInfo(
    val fileName: String,
    val fileSize: Long,
    val mimeType: String
)

/**
 * Common file validation logic
 */
object FileValidator {
    private const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10 MB

    private val SUPPORTED_MIME_TYPES = listOf(
        "image/jpeg",
        "image/png",
        "image/jpg",
        "application/pdf",
        "text/plain",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )

    /**
     * Validate file information
     * @param fileInfo File information to validate
     * @return Error message if validation fails, null if valid
     */
    fun validate(fileInfo: FileInfo): String? {
        // Check file size
        if (fileInfo.fileSize > MAX_FILE_SIZE) {
            return "File size exceeds maximum allowed size of ${MAX_FILE_SIZE / 1024 / 1024} MB"
        }

        // Check MIME type
        if (fileInfo.mimeType !in SUPPORTED_MIME_TYPES) {
            return "File type '${fileInfo.mimeType}' is not supported"
        }

        return null
    }
}

/**
 * Common result processing logic
 */
object UploadResultProcessor {
    fun createSuccessResult(
        fileName: String,
        filePath: String,
        fileType: String,
        fileSize: Long,
        content: String? = null
    ): UploadResult {
        val metadata = mutableMapOf(
            "filePath" to filePath,
            "fileName" to fileName,
            "fileType" to fileType,
            "fileSize" to fileSize.toString()
        )

        content?.let { metadata["content"] = it }

        return UploadResult.Success(
            recognizedText = "File uploaded successfully: $fileName",
            metadata = metadata
        )
    }

    fun createErrorResult(message: String): UploadResult {
        return UploadResult.Failure(message)
    }
}