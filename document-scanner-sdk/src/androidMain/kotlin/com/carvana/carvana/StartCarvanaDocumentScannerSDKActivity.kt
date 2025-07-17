package com.carvana.carvana

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.interfaces.ScanResult
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.interfaces.SDKCallbackManager
import com.carvana.carvana.scan.DocumentScannerActivity.Companion.ERROR_MESSAGE
import com.carvana.carvana.scan.DocumentScannerActivity.Companion.NO_FILE_SELECTED
import com.carvana.carvana.scan.DocumentScannerActivity.Companion.PDF_PATH
import com.carvana.carvana.scan.DocumentScannerActivity.Companion.RECOGNIZED_TEXT
import com.carvana.carvana.scan.DocumentScannerActivity.Companion.SCAN_CANCELED
import com.carvana.carvana.scan.DocumentScannerActivity.Companion.SCAN_FAILED
import com.carvana.carvana.scan.DocumentScannerActivity.Companion.UPLOAD_CANCELED
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.carvana.carvana.upload.AndroidDocumentUploadHandler
import com.carvana.carvana.sdk.CarvanaDocumentScannerSDKFactory
import com.carvana.carvana.sdk.SDKConfiguration

/**
 * Entry point Activity for the Carvana Document Scanner SDK
 * External apps should launch this activity to use the SDK
 *
 * Example usage from external app:
 * ```
 * val intent = Intent(this, SDKEntryActivity::class.java)
 * startActivityForResult(intent, REQUEST_CODE)
 * ```
 */
class StartCarvanaDocumentScannerSDKActivity : ComponentActivity() {

    companion object {
        // Result codes and extras for external apps
        const val EXTRA_DOCUMENT_PATH = "DOCUMENT_PATH"
        const val EXTRA_ERROR_MESSAGE = "ERROR_MESSAGE"

        // "scanned" or "uploaded"
        const val EXTRA_DOCUMENT_TYPE = "DOCUMENT_TYPE"
    }

    private lateinit var documentScanner: DocumentScanner
    private lateinit var documentUploader: DocumentUploader
    private lateinit var documentScannerLauncher: ActivityResultLauncher<Intent>
    private lateinit var documentUploaderLauncher: ActivityResultLauncher<Intent>
    private lateinit var uploadHandler: AndroidDocumentUploadHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize SDK - no memory leak, pass context directly
        val sdk = CarvanaDocumentScannerSDKFactory.createWithContext(this)
        sdk.initialize(SDKConfiguration())

        // Register for activity result
        documentScannerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            result.data?.let { resultData ->
                val scanSuccess = resultData.getIntExtra(DocumentScanner.SCAN_SUCCESS_RESULT, 0)
                val scanFailure = resultData.getIntExtra(DocumentScanner.SCAN_FAILURE_RESULT, 0)

                if (result.resultCode == RESULT_OK && scanSuccess == DocumentScanner.REQUEST_SCAN_SUCCESS) {
                    val recognizedText = resultData.getStringExtra(RECOGNIZED_TEXT).orEmpty()
                    val pdfPath = resultData.getStringExtra(PDF_PATH).orEmpty()
                    DocumentScanner.handleScanResult(ScanResult.Success(recognizedText, pdfPath, emptyMap()))
                } else if (result.resultCode == RESULT_OK && scanFailure == DocumentScanner.REQUEST_SCAN_FAILURE) {
                    val errorMessage = resultData.getStringExtra(ERROR_MESSAGE) ?: SCAN_FAILED
                    DocumentScanner.handleScanResult(ScanResult.Failure(errorMessage))
                } else if (result.resultCode == RESULT_CANCELED) {
                    DocumentScanner.handleScanResult(ScanResult.Failure(SCAN_CANCELED))
                }
            }
        }

        // Initialize upload handler
        uploadHandler = AndroidDocumentUploadHandler(this)

        // Register for document upload result
        documentUploaderLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    // Process the uploaded file using DocumentUploadHandler
                    lifecycleScope.launch {
                        val uploadResult = uploadHandler.processUploadedDocument(uri)
                        DocumentUploader.handleUploadResult(uploadResult)
                    }
                } ?: run {
                    DocumentUploader.handleUploadResult(UploadResult.Failure(NO_FILE_SELECTED))
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                DocumentUploader.handleUploadResult(UploadResult.Failure(UPLOAD_CANCELED))
            }
        }

        documentScanner = DocumentScanner(documentScannerLauncher, this)
        documentUploader = DocumentUploader(documentUploaderLauncher, this)

        // REMOVE FOR PROD: For testing, don't finish() the activity
        val isTestMode = false // Set to false for production

        // Set up callbacks to return results to the calling app
        SDKCallbackManager.setCallbacks(
            SDKCallbackManager.SDKCallbacks(
                onSuccess = { documentPath ->
                    // Return success result to calling app
                    val resultIntent = Intent().apply {
                        putExtra(EXTRA_DOCUMENT_PATH, documentPath)
                        putExtra(
                            EXTRA_DOCUMENT_TYPE,
                            if (documentPath.endsWith(".pdf")) "scanned" else "uploaded"
                        )
                    }
                    setResult(RESULT_OK, resultIntent)
                    if (!isTestMode) {
                        finish()
                    }
                    // REMOVE FOR PROD: In test mode, show a success message instead
                    if (isTestMode) {
                        android.widget.Toast.makeText(
                            this,
                            "Document saved: $documentPath",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onFailure = { errorMessage ->
                    // Return error result to calling app
                    val resultIntent = Intent().apply {
                        putExtra(EXTRA_ERROR_MESSAGE, errorMessage)
                    }
                    setResult(RESULT_CANCELED, resultIntent)
                    if (!isTestMode) {
                        finish()
                    }
                    // REMOVE FOR PROD: In test mode, show an error message instead
                    if (isTestMode) {
                        android.widget.Toast.makeText(
                            this,
                            "Error: $errorMessage",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onExit = {
                    // User cancelled - return empty result
                    setResult(RESULT_CANCELED)
                    // Always finish on exit
                    finish()
                }
            )
        )

        setContent {
            App(
                onExit = {
                    // This will trigger the onExit callback above
                    SDKCallbackManager.handleExit()
                },
                documentScanner = documentScanner,
                documentUploader = documentUploader
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clear callbacks when activity is destroyed to prevent memory leaks
        SDKCallbackManager.clearCallbacks()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(
        onExit = {},
        documentScanner = DocumentScanner(),
        documentUploader = DocumentUploader()
    )
}
