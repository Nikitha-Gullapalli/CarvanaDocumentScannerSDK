package com.carvana.carvana.interfaces

/**
 * Manages callbacks for SDK operations
 */
object SDKCallbackManager {
    private var currentCallbacks: SDKCallbacks? = null
    
    fun handleSuccess(recognizedText: String) {
        currentCallbacks?.onSuccess?.invoke(recognizedText)
        // Clear callbacks after success to prevent memory leaks and ensure fresh callbacks next time
        clearCallbacks()
    }
    
    fun handleFailure(errorMessage: String) {
        currentCallbacks?.onFailure?.invoke(errorMessage)
        // Clear callbacks after failure to prevent memory leaks and ensure fresh callbacks next time
        clearCallbacks()
    }
    
    fun handleExit() {
        currentCallbacks?.onExit?.invoke()
        clearCallbacks()
    }
    
    fun clearCallbacks() {
        currentCallbacks = null
    }
    
    fun setCallbacks(callbacks: SDKCallbacks) {
        currentCallbacks = callbacks
    }
    
    data class SDKCallbacks(
        val onSuccess: (String) -> Unit,
        val onFailure: (String) -> Unit,
        val onExit: () -> Unit
    )
}