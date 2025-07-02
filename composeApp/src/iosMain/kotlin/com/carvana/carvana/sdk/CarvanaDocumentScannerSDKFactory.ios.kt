package com.carvana.carvana.sdk

/**
 * iOS implementation of SDK factory
 */
actual object CarvanaDocumentScannerSDKFactory {
    actual fun create(): CarvanaDocumentScannerSDK {
        return IosCarvanaDocumentScannerSDK()
    }
}