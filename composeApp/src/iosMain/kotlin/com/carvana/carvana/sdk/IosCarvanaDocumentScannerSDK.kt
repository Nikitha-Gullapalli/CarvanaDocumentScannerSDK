package com.carvana.carvana.sdk

import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.interfaces.ScanResult
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.upload.IosDocumentUploadHandler
import com.carvana.carvana.sdk.converters.ResultConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSURL
import platform.Foundation.NSUUID

/**
 * iOS implementation of CarvanaDocumentScannerSDK
 */
class IosCarvanaDocumentScannerSDK : CarvanaDocumentScannerSDK {
    
    private var config: SDKConfiguration = SDKConfiguration()
    private val uploadHandler = IosDocumentUploadHandler()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    override fun initialize(config: SDKConfiguration) {
        this.config = config
    }
    
    override fun scanDocument(options: ScanOptions, callback: (DocumentResult) -> Unit) {
        // Set up scan result handler
        DocumentScanner.setScanResultCallback { scanResult ->
            val documentId = NSUUID().UUIDString()
            val result = ResultConverters.fromScanResult(scanResult, documentId)
            callback(result)
        }
        
        // Start scan
        DocumentScanner.initiateScanning()
    }
    
    override fun uploadDocument(options: UploadOptions, callback: (DocumentResult) -> Unit) {
        // Set up upload result handler
        DocumentUploader.setUploadResultCallback { uploadResult ->
            val documentId = NSUUID().UUIDString()
            val result = ResultConverters.fromUploadResult(uploadResult, documentId)
            callback(result)
        }
        
        // Start upload
        DocumentUploader.initiateUpload()
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
        // Clean up any iOS-specific resources
        // Currently no specific cleanup needed, but this provides
        // a consistent interface with Android
    }
}