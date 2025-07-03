package com.carvana.carvana.interfaces

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.carvana.carvana.AndroidDocumentMimeType
import com.carvana.carvana.resources.Strings.CONTEXT_UNAVAILABLE
import com.carvana.carvana.resources.Strings.SELECT_DOCUMENT
import com.carvana.carvana.resources.Strings.TYPE

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DocumentUploader(
    private val activityResultLauncher: ActivityResultLauncher<Intent>? = null,
    private val context: Context? = null
) {

    actual fun uploadDocument(onResult: (UploadResult) -> Unit) {
        currentCallback = onResult

        if (activityResultLauncher != null && context != null) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = TYPE
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_MIME_TYPES, AndroidDocumentMimeType.getAllTypes())
            }
            activityResultLauncher.launch(Intent.createChooser(intent, SELECT_DOCUMENT))
        } else {
            onResult(UploadResult.Failure(CONTEXT_UNAVAILABLE))
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
