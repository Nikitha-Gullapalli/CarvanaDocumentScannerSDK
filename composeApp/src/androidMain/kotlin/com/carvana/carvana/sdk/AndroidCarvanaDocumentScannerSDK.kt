package com.carvana.carvana.sdk

import android.content.Context
import android.net.Uri
import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.interfaces.ScanResult
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.upload.AndroidDocumentUploadHandler
import com.carvana.carvana.sdk.converters.ResultConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import java.util.UUID

/**
 * Android implementation of CarvanaDocumentScannerSDK
 */
class AndroidCarvanaDocumentScannerSDK(
    private val context: Context,
    private var documentScanner: DocumentScanner? = null,
    private var documentUploader: DocumentUploader? = null
) : CarvanaDocumentScannerSDK {
    
    private var config: SDKConfiguration = SDKConfiguration()
    private val uploadHandler = AndroidDocumentUploadHandler(context)
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
                            val documentId = UUID.randomUUID().toString()
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
                    message = e.message ?: "Failed to start document scanner"
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
                            val documentId = UUID.randomUUID().toString()
                            val documentResult = ResultConverters.fromUploadResult(result, documentId)
                            callback(documentResult)
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
                    message = e.message ?: "Failed to start document uploader"
                ))
            }
        }
    }
    
    override suspend fun processDocument(documentUri: Any, callback: (DocumentResult) -> Unit) {
        try {
            if (documentUri !is Uri) {
                callback(DocumentResult.Failure(
                    error = DocumentError.INVALID_FORMAT,
                    message = "Invalid document URI type"
                ))
                return
            }
            
            val uploadResult = uploadHandler.processUploadedDocument(documentUri)
            val documentId = UUID.randomUUID().toString()
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
        coroutineScope.cancel()
        documentScanner = null
        documentUploader = null
    }
}