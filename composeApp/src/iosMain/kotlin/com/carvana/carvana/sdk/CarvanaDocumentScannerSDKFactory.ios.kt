package com.carvana.carvana.sdk

/**
 * iOS implementation of SDK factory
 */
actual object CarvanaDocumentScannerSDKFactory {
    actual fun create(): CarvanaDocumentScannerSDK {
        // Create SDK without scanner/uploader - they'll be set later
        return IosCarvanaDocumentScannerSDK()
    }
}