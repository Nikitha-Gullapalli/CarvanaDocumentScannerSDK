package com.carvana.carvana.sdk.lifecycle

import com.carvana.carvana.sdk.CarvanaDocumentScannerSDK
import com.carvana.carvana.sdk.DocumentResult
import com.carvana.carvana.sdk.ScanOptions
import com.carvana.carvana.sdk.UploadOptions

/**
 * Common interface for lifecycle management across platforms
 */
interface SDKLifecycleManager {
    /**
     * Scan document with lifecycle-aware callback
     */
    fun scanDocument(
        options: ScanOptions,
        callback: (DocumentResult) -> Unit
    )
    
    /**
     * Upload document with lifecycle-aware callback
     */
    fun uploadDocument(
        options: UploadOptions,
        callback: (DocumentResult) -> Unit
    )
    
    /**
     * Clean up resources when no longer needed
     */
    fun cleanup()
    
    /**
     * Check if the manager is still active (not cleaned up)
     */
    fun isActive(): Boolean
}

/**
 * Factory for creating platform-specific lifecycle managers
 */
expect object SDKLifecycleManagerFactory {
    /**
     * Create a lifecycle manager for the given SDK
     * Platform-specific implementations will handle lifecycle differently
     */
    fun create(
        sdk: CarvanaDocumentScannerSDK,
        lifecycleOwner: Any? = null
    ): SDKLifecycleManager
}

/**
 * Base implementation with common functionality
 */
abstract class BaseSDKLifecycleManager(
    protected val sdk: CarvanaDocumentScannerSDK
) : SDKLifecycleManager {
    
    @Volatile
    private var isCleanedUp = false
    
    protected val activeCallbacks = mutableListOf<CallbackWrapper<*>>()
    
    override fun scanDocument(
        options: ScanOptions,
        callback: (DocumentResult) -> Unit
    ) {
        if (isCleanedUp) return
        
        val wrapper = createCallbackWrapper(callback)
        activeCallbacks.add(wrapper)
        
        sdk.scanDocument(options) { result ->
            if (!isCleanedUp && wrapper.isActive()) {
                wrapper.invoke(result)
            }
            activeCallbacks.remove(wrapper)
        }
    }
    
    override fun uploadDocument(
        options: UploadOptions,
        callback: (DocumentResult) -> Unit
    ) {
        if (isCleanedUp) return
        
        val wrapper = createCallbackWrapper(callback)
        activeCallbacks.add(wrapper)
        
        sdk.uploadDocument(options) { result ->
            if (!isCleanedUp && wrapper.isActive()) {
                wrapper.invoke(result)
            }
            activeCallbacks.remove(wrapper)
        }
    }
    
    override fun cleanup() {
        isCleanedUp = true
        activeCallbacks.forEach { it.clear() }
        activeCallbacks.clear()
        onCleanup()
    }
    
    override fun isActive(): Boolean = !isCleanedUp
    
    /**
     * Platform-specific cleanup implementation
     */
    protected abstract fun onCleanup()
    
    /**
     * Create platform-specific callback wrapper
     */
    protected abstract fun <T> createCallbackWrapper(callback: (T) -> Unit): CallbackWrapper<T>
}

/**
 * Wrapper for callbacks that can be cleared
 */
interface CallbackWrapper<T> {
    fun invoke(value: T)
    fun isActive(): Boolean
    fun clear()
}