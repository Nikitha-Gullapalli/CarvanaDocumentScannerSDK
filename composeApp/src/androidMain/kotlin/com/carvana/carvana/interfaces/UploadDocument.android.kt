package com.carvana.carvana.interfaces

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DocumentUploader(
    private val activityResultLauncher: ActivityResultLauncher<Intent>? = null,
    private val context: Context? = null
) {

    actual fun uploadDocument(onResult: (UploadResult) -> Unit) {
        currentCallback = onResult
        
        if (activityResultLauncher != null && context != null) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                    "image/*",
                    "application/pdf",
                    "text/*"
                ))
            }
            activityResultLauncher.launch(Intent.createChooser(intent, "Select Document"))
        } else {
            onResult(UploadResult.Failure("Activity result launcher or context not available"))
        }
    }

    companion object {
        private var currentCallback: ((UploadResult) -> Unit)? = null

        fun handleUploadResult(result: UploadResult) {
            currentCallback?.invoke(result)
            currentCallback = null
        }
    }
}
