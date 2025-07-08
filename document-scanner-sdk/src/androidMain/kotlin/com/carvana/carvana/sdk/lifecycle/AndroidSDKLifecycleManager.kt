package com.carvana.carvana.sdk.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.carvana.carvana.sdk.AndroidCarvanaDocumentScannerSDK
import com.carvana.carvana.sdk.CarvanaDocumentScannerSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.lang.ref.WeakReference

/**
 * Android implementation of lifecycle manager
 */
class AndroidSDKLifecycleManager(
    sdk: CarvanaDocumentScannerSDK,
    private val lifecycle: Lifecycle? = null
) : BaseSDKLifecycleManager(sdk), DefaultLifecycleObserver {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    init {
        lifecycle?.addObserver(this)
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        cleanup()
    }
    
    override fun onCleanup() {
        lifecycle?.removeObserver(this)
        scope.cancel()
        (sdk as? AndroidCarvanaDocumentScannerSDK)?.cleanup()
    }
    
    override fun <T> createCallbackWrapper(callback: (T) -> Unit): CallbackWrapper<T> {
        return AndroidCallbackWrapper(callback)
    }
}

/**
 * Android callback wrapper using WeakReference
 */
class AndroidCallbackWrapper<T>(callback: (T) -> Unit) : CallbackWrapper<T> {
    private var weakCallback: WeakReference<(T) -> Unit>? = WeakReference(callback)
    
    override fun invoke(value: T) {
        weakCallback?.get()?.invoke(value)
    }
    
    override fun isActive(): Boolean = weakCallback?.get() != null
    
    override fun clear() {
        weakCallback?.clear()
        weakCallback = null
    }
}

/**
 * Android implementation of the factory
 */
actual object SDKLifecycleManagerFactory {
    actual fun create(
        sdk: CarvanaDocumentScannerSDK,
        lifecycleOwner: Any?
    ): SDKLifecycleManager {
        val lifecycle = when (lifecycleOwner) {
            is LifecycleOwner -> lifecycleOwner.lifecycle
            is Lifecycle -> lifecycleOwner
            else -> null
        }
        return AndroidSDKLifecycleManager(sdk, lifecycle)
    }
}