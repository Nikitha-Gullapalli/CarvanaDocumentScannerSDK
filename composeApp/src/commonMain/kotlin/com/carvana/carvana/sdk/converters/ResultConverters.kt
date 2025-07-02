package com.carvana.carvana.sdk.converters

import com.carvana.carvana.interfaces.ScanResult
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.sdk.*

/**
 * Converters to transform existing result types to SDK DocumentResult
 */
object ResultConverters {
    
    /**
     * Convert ScanResult to DocumentResult
     */
    fun fromScanResult(scanResult: ScanResult, documentId: String): DocumentResult {
        return when (scanResult) {
            is ScanResult.Success -> {
                val pdfPath = scanResult.metadata["pdfPath"] ?: ""
                val pageCount = scanResult.metadata["pageCount"]?.toIntOrNull() ?: 1
                
                val pages = (1..pageCount).map { pageNum ->
                    DocumentPage(
                        pageNumber = pageNum,
                        imageUri = null,
                        text = if (pageNum == 1) scanResult.recognizedText else null,
                        width = 0,
                        height = 0
                    )
                }
                
                val document = Document(
                    id = documentId,
                    uri = "file://$pdfPath",
                    format = DocumentFormat.PDF,
                    pages = pages,
                    extractedText = scanResult.recognizedText,
                    pdfData = null
                )
                
                val metadata = DocumentMetadata(
                    fileName = "scan_${documentId}.pdf",
                    fileSize = 0, // Would need actual file size
                    mimeType = "application/pdf",
                    source = DocumentSource.CAMERA_SCAN,
                    processingTime = null
                )
                
                DocumentResult.Success(document, metadata)
            }
            is ScanResult.Failure -> {
                val error = when {
                    scanResult.message.contains("cancel", ignoreCase = true) -> DocumentError.SCAN_CANCELLED
                    scanResult.message.contains("permission", ignoreCase = true) -> DocumentError.PERMISSION_DENIED
                    else -> DocumentError.SCAN_FAILED
                }
                DocumentResult.Failure(error, scanResult.message)
            }
        }
    }
    
    /**
     * Convert UploadResult to DocumentResult
     */
    fun fromUploadResult(uploadResult: UploadResult, documentId: String): DocumentResult {
        return when (uploadResult) {
            is UploadResult.Success -> {
                val filePath = uploadResult.metadata["filePath"] ?: ""
                val fileType = uploadResult.metadata["fileType"] ?: "unknown"
                val fileSize = uploadResult.metadata["fileSize"]?.toLongOrNull() ?: 0L
                val mimeType = uploadResult.metadata["mimeType"] ?: "application/octet-stream"
                val content = uploadResult.metadata["content"]
                
                val format = getDocumentFormat(fileType, mimeType)
                
                val document = Document(
                    id = documentId,
                    uri = "file://$filePath",
                    format = format,
                    pages = listOf(
                        DocumentPage(
                            pageNumber = 1,
                            imageUri = if (format in listOf(DocumentFormat.JPEG, DocumentFormat.PNG)) "file://$filePath" else null,
                            text = content,
                            width = 0,
                            height = 0
                        )
                    ),
                    extractedText = content,
                    pdfData = null
                )
                
                val metadata = DocumentMetadata(
                    fileName = filePath.substringAfterLast("/"),
                    fileSize = fileSize,
                    mimeType = mimeType,
                    source = DocumentSource.FILE_UPLOAD
                )
                
                DocumentResult.Success(document, metadata)
            }
            is UploadResult.Failure -> {
                val error = when {
                    uploadResult.message.contains("cancel", ignoreCase = true) -> DocumentError.UPLOAD_FAILED
                    uploadResult.message.contains("size", ignoreCase = true) -> DocumentError.FILE_TOO_LARGE
                    uploadResult.message.contains("format", ignoreCase = true) || 
                    uploadResult.message.contains("type", ignoreCase = true) -> DocumentError.INVALID_FORMAT
                    else -> DocumentError.UPLOAD_FAILED
                }
                DocumentResult.Failure(error, uploadResult.message)
            }
        }
    }
    
    private fun getDocumentFormat(fileType: String, mimeType: String): DocumentFormat {
        return when {
            fileType == "pdf" || mimeType == "application/pdf" -> DocumentFormat.PDF
            fileType in listOf("jpeg", "jpg") || mimeType == "image/jpeg" -> DocumentFormat.JPEG
            fileType == "png" || mimeType == "image/png" -> DocumentFormat.PNG
            fileType == "text" || mimeType == "text/plain" -> DocumentFormat.TEXT
            fileType == "doc" || mimeType == "application/msword" -> DocumentFormat.WORD
            fileType == "docx" || mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> DocumentFormat.WORD
            else -> DocumentFormat.UNKNOWN
        }
    }
}