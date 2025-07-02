package com.carvana.carvana.sdk.lifecycle

import com.carvana.carvana.sdk.CarvanaDocumentScannerSDK
import com.carvana.carvana.sdk.IosCarvanaDocumentScannerSDK
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNotificationName
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillTerminateNotification
import platform.darwin.NSObject
import platform.darwin.NSObjectProtocol
import kotlin.native.ref.WeakReference
import kotlin.experimental.ExperimentalNativeApi

/**
 * iOS implementation of lifecycle manager
 */
class IosSDKLifecycleManager(
    sdk: CarvanaDocumentScannerSDK,
    private val viewController: Any? = null
) : BaseSDKLifecycleManager(sdk) {
    
    private val notificationObservers = mutableListOf<NSObjectProtocol>()
    
    init {
        setupNotificationObservers()
    }
    
    private fun setupNotificationObservers() {
        // Listen for app lifecycle events
        val notificationCenter = NSNotificationCenter.defaultCenter
        
        // App entering background
        val backgroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                // Clean up callbacks when app goes to background
                activeCallbacks.forEach { it.clear() }
                activeCallbacks.clear()
            }
        )
        notificationObservers.add(backgroundObserver)
        
        // App terminating
        val terminateObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillTerminateNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { _ ->
                cleanup()
            }
        )
        notificationObservers.add(terminateObserver)
    }
    
    override fun onCleanup() {
        // Remove notification observers
        val notificationCenter = NSNotificationCenter.defaultCenter
        notificationObservers.forEach { observer ->
            notificationCenter.removeObserver(observer)
        }
        notificationObservers.clear()
        
        // Clean up iOS SDK if applicable
        (sdk as? IosCarvanaDocumentScannerSDK)?.cleanup()
    }
    
    override fun <T> createCallbackWrapper(callback: (T) -> Unit): CallbackWrapper<T> {
        return IosCallbackWrapper(callback)
    }
}

/**
 * iOS callback wrapper using Kotlin/Native WeakReference
 */
@OptIn(ExperimentalNativeApi::class)
class IosCallbackWrapper<T>(callback: (T) -> Unit) : CallbackWrapper<T> {
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
 * iOS implementation of the factory
 */
actual object SDKLifecycleManagerFactory {
    actual fun create(
        sdk: CarvanaDocumentScannerSDK,
        lifecycleOwner: Any?
    ): SDKLifecycleManager {
        return IosSDKLifecycleManager(sdk, lifecycleOwner)
    }
}