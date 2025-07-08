package com.carvana.carvana.sdk

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader

/**
 * Manages SDK initialization with proper Activity/Fragment context
 * 
 * This class helps create DocumentScanner and DocumentUploader instances
 * but does NOT store them to avoid memory leaks
 */
class SDKActivityManager {
    
    companion object {
        /**
         * Create DocumentScanner and DocumentUploader from an Activity
         * Note: The caller is responsible for storing and managing these instances
         */
        fun createFromActivity(activity: ComponentActivity): Pair<DocumentScanner, DocumentUploader> {
            val documentScanner = DocumentScanner(null, activity)
            val documentUploader = DocumentUploader(null, activity)
            
            return Pair(documentScanner, documentUploader)
        }
        
        /**
         * Create DocumentScanner and DocumentUploader from a Fragment
         * Note: The caller is responsible for storing and managing these instances
         */
        fun createFromFragment(fragment: Fragment): Pair<DocumentScanner, DocumentUploader> {
            val documentScanner = DocumentScanner(null, fragment.requireContext())
            val documentUploader = DocumentUploader(null, fragment.requireContext())
            
            return Pair(documentScanner, documentUploader)
        }
    }
}