package com.carvana.carvana

import androidx.compose.ui.window.ComposeUIViewController
import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.sdk.CarvanaDocumentScannerSDKFactory
import com.carvana.carvana.sdk.SDKConfiguration

fun MainViewController() = ComposeUIViewController { 
    val sdk = CarvanaDocumentScannerSDKFactory.create()
    sdk.initialize(SDKConfiguration())
    
    App(
        documentScanner = DocumentScanner(),
        documentUploader = DocumentUploader(),
    )
}
