package com.carvana.carvana.sdk

import android.content.Context

/**
 * Android implementation of SDK factory
 * 
 * Note: This implementation avoids memory leaks by not storing Context.
 * The factory is primarily needed for cross-platform code sharing.
 * For Android-only code, you can directly instantiate AndroidCarvanaDocumentScannerSDK.
 */
actual object CarvanaDocumentScannerSDKFactory {
    
    /**
     * Create SDK instance - Android specific version with context
     */
    fun createWithContext(context: Context): CarvanaDocumentScannerSDK {
        return AndroidCarvanaDocumentScannerSDK(context.applicationContext)
    }
    
    /**
     * This method exists to satisfy the common interface.
     * On Android, always use createWithContext(context) instead.
     */
    actual fun create(): CarvanaDocumentScannerSDK {
        throw UnsupportedOperationException(
            "On Android, use createWithContext(context) or directly instantiate AndroidCarvanaDocumentScannerSDK"
        )
    }
}