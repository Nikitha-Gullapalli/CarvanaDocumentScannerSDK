package com.carvana.carvana.utils

import platform.UIKit.*

object ViewControllerHelper {
    fun getPresentingViewController(): UIViewController? {
        // Try to get the root view controller from the key window
        val keyWindow = UIApplication.sharedApplication.keyWindow
        var rootVC = keyWindow?.rootViewController
        
        // If there's a presented view controller, use that
        while (rootVC?.presentedViewController != null) {
            rootVC = rootVC.presentedViewController
        }
        
        return rootVC
    }
}