package com.carvana.carvana.sdk

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment

/**
 * Android implementation of SDK factory
 */
actual object CarvanaDocumentScannerSDKFactory {
    private var context: Context? = null
    
    /**
     * Initialize the factory with Android context
     * Must be called before create()
     */
    fun initialize(context: Context) {
        this.context = context.applicationContext
    }
    
    actual fun create(): CarvanaDocumentScannerSDK {
        val ctx = context ?: throw IllegalStateException(
            "CarvanaDocumentScannerSDKFactory must be initialized with context before creating SDK instance"
        )
        return AndroidCarvanaDocumentScannerSDK(ctx)
    }
    
    /**
     * Create an SDK instance with Activity initialization
     * Use this when you need scanning/uploading functionality
     */
    fun createWithActivity(activity: ComponentActivity): CarvanaDocumentScannerSDK {
        val ctx = activity.applicationContext
        val (scanner, uploader) = SDKActivityManager.createFromActivity(activity)
        return AndroidCarvanaDocumentScannerSDK(ctx, scanner, uploader)
    }
    
    /**
     * Create an SDK instance with Fragment initialization
     * Use this when you need scanning/uploading functionality from a Fragment
     */
    fun createWithFragment(fragment: Fragment): CarvanaDocumentScannerSDK {
        val ctx = fragment.requireContext().applicationContext
        val (scanner, uploader) = SDKActivityManager.createFromFragment(fragment)
        return AndroidCarvanaDocumentScannerSDK(ctx, scanner, uploader)
    }
}