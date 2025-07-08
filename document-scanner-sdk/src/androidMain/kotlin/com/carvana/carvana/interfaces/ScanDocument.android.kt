// androidMain
package com.carvana.carvana.interfaces

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.carvana.carvana.scan.DocumentScannerActivity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DocumentScanner(
    private val activityResultLauncher: ActivityResultLauncher<Intent>? = null,
    private val context: Context? = null
) {

    actual fun scanDocument(onResult: (ScanResult) -> Unit) {
        currentCallback = onResult

        if (activityResultLauncher != null && context != null) {
            val intent = Intent(context, DocumentScannerActivity::class.java)
            activityResultLauncher.launch(intent)
        } else {
            onResult(ScanResult.Failure("Activity result launcher or context not available"))
        }
    }

    companion object {
        const val SCAN_SUCCESS_RESULT = "SCAN_SUCCESS_RESULT"
        const val SCAN_FAILURE_RESULT = "SCAN_FAILURE_RESULT"
        const val REQUEST_SCAN_SUCCESS = 1001
        const val REQUEST_SCAN_FAILURE = 1002

        private var currentCallback: ((ScanResult) -> Unit)? = null

        fun handleScanResult(result: ScanResult) {
            currentCallback?.invoke(result)
            currentCallback = null
        }
    }
}
