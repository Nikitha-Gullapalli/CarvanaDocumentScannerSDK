package com.carvana.carvana.interfaces

/**
 * Manages callbacks for SDK operations
 */
object SDKCallbackManager {
    private var currentCallbacks: SDKCallbacks? = null
    
    fun handleSuccess(recognizedText: String) {
        currentCallbacks?.onSuccess?.invoke(recognizedText)
        currentCallbacks = null
    }
    
    fun handleFailure(errorMessage: String) {
        currentCallbacks?.onFailure?.invoke(errorMessage)
        currentCallbacks = null
    }
    
    fun handleExit() {
        currentCallbacks?.onExit?.invoke()
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