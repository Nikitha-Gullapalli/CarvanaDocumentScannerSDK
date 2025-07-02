package com.carvana.carvana

import androidx.compose.ui.window.ComposeUIViewController
import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.sdk.CarvanaDocumentScannerSDKFactory
import com.carvana.carvana.sdk.SDKConfiguration

fun StartCarvanaDocumentScannerSDKViewController() = ComposeUIViewController {
    // Create document scanner and uploader instances
    val documentScanner = DocumentScanner()
    val documentUploader = DocumentUploader()
    
    // Initialize SDK - iOS doesn't need special initialization like Android
    val sdk = CarvanaDocumentScannerSDKFactory.create()
    sdk.initialize(SDKConfiguration())
    
    // If SDK is IosCarvanaDocumentScannerSDK, set the instances
    if (sdk is com.carvana.carvana.sdk.IosCarvanaDocumentScannerSDK) {
        sdk.setDocumentScanner(documentScanner)
        sdk.setDocumentUploader(documentUploader)
    }
    
    App(
        documentScanner = documentScanner,
        documentUploader = documentUploader,
        onExit = {
            // Handle SDK exit if needed
            // For iOS, this might dismiss the view controller
        }
    )
}
