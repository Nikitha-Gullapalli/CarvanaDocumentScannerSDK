package com.carvana.carvana.sdk

/**
 * Factory for creating platform-specific SDK instances
 */
expect object CarvanaDocumentScannerSDKFactory {
    /**
     * Create a platform-specific instance of the SDK
     */
    fun create(): CarvanaDocumentScannerSDK
}