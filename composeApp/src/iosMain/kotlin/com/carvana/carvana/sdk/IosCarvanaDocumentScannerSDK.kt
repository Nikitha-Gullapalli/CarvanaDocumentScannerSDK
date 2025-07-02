package com.carvana.carvana.sdk

import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.interfaces.ScanResult
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.upload.IosDocumentUploadHandler
import com.carvana.carvana.sdk.converters.ResultConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import platform.Foundation.NSURL
import platform.Foundation.NSUUID

/**
 * iOS implementation of CarvanaDocumentScannerSDK
 */
class IosCarvanaDocumentScannerSDK(
    private var documentScanner: DocumentScanner? = null,
    private var documentUploader: DocumentUploader? = null
) : CarvanaDocumentScannerSDK {
    
    private var config: SDKConfiguration = SDKConfiguration()
    private val uploadHandler = IosDocumentUploadHandler()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun initialize(config: SDKConfiguration) {
        this.config = config
    }
    
    override fun scanDocument(options: ScanOptions, callback: (DocumentResult) -> Unit) {
        val scanner = documentScanner
        if (scanner == null) {
            callback(DocumentResult.Failure(
                error = DocumentError.INITIALIZATION_ERROR,
                message = "SDK must be initialized with DocumentScanner instance"
            ))
            return
        }
        
        coroutineScope.launch {
            try {
                scanner.scanDocument { result ->
                    when (result) {
                        is ScanResult.Success -> {
                            val documentId = NSUUID().UUIDString()
                            val documentResult = ResultConverters.fromScanResult(result, documentId)
                            callback(documentResult)
                        }
                        is ScanResult.Failure -> {
                            callback(DocumentResult.Failure(
                                error = DocumentError.SCAN_FAILED,
                                message = result.message
                            ))
                        }
                    }
                }
            } catch (e: Exception) {
                callback(DocumentResult.Failure(
                    error = DocumentError.UNKNOWN_ERROR,
                    message = e.message ?: "Scan failed"
                ))
            }
        }
    }
    
    override fun uploadDocument(options: UploadOptions, callback: (DocumentResult) -> Unit) {
        val uploader = documentUploader
        if (uploader == null) {
            callback(DocumentResult.Failure(
                error = DocumentError.INITIALIZATION_ERROR,
                message = "SDK must be initialized with DocumentUploader instance"
            ))
            return
        }
        
        coroutineScope.launch {
            try {
                uploader.uploadDocument { result ->
                    when (result) {
                        is UploadResult.Success -> {
                            val documentId = NSUUID().UUIDString()
                            callback(ResultConverters.fromUploadResult(result, documentId))
                        }
                        is UploadResult.Failure -> {
                            callback(DocumentResult.Failure(
                                error = DocumentError.UPLOAD_FAILED,
                                message = result.message
                            ))
                        }
                    }
                }
            } catch (e: Exception) {
                callback(DocumentResult.Failure(
                    error = DocumentError.UNKNOWN_ERROR,
                    message = e.message ?: "Upload failed"
                ))
            }
        }
    }
    
    override suspend fun processDocument(documentUri: Any, callback: (DocumentResult) -> Unit) {
        try {
            if (documentUri !is NSURL) {
                callback(DocumentResult.Failure(
                    error = DocumentError.INVALID_FORMAT,
                    message = "Invalid document URL type"
                ))
                return
            }
            
            val uploadResult = uploadHandler.processUploadedDocument(documentUri)
            val documentId = NSUUID().UUIDString()
            val result = ResultConverters.fromUploadResult(uploadResult, documentId)
            callback(result)
        } catch (e: Exception) {
            callback(DocumentResult.Failure(
                error = DocumentError.UNKNOWN_ERROR,
                message = e.message ?: "Unknown error occurred"
            ))
        }
    }
    
    override fun getVersion(): String = "1.0.0"
    
    /**
     * Clean up resources when SDK is no longer needed
     */
    fun cleanup() {
        // Cancel all coroutines to prevent memory leaks
        coroutineScope.cancel()
        
        // Clear references
        documentScanner = null
        documentUploader = null
    }
    
    /**
     * Set the document scanner instance
     */
    fun setDocumentScanner(scanner: DocumentScanner) {
        this.documentScanner = scanner
    }
    
    /**
     * Set the document uploader instance
     */
    fun setDocumentUploader(uploader: DocumentUploader) {
        this.documentUploader = uploader
    }
}