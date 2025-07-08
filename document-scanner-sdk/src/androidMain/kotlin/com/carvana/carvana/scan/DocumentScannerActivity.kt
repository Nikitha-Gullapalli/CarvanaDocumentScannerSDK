package com.carvana.carvana.scan

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.carvana.carvana.interfaces.DocumentScanner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DocumentScannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MLKitDocumentScannerScreen(
                onScanComplete = { bitmaps, pdfPath ->
                    // Extract text from all pages using ML Kit
                    lifecycleScope.launch {
                        val allText = extractTextFromBitmaps(bitmaps)
                        returnScanSuccessResult(allText, pdfPath)
                    }
                },
                onError = { exception ->
                    Log.e("DocumentScanner", "Scan error", exception)
                    val resultIntent = Intent().apply {
                        putExtra(ERROR_MESSAGE, exception.message ?: "Unknown error")
                    }
                    setResult(RESULT_CANCELED, resultIntent)
                    finish()
                },
                onCancel = {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            )
        }
    }
    
    private suspend fun extractTextFromBitmaps(bitmaps: List<Bitmap>): String {
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val allText = StringBuilder()
        
        for ((index, bitmap) in bitmaps.withIndex()) {
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                val result = textRecognizer.process(inputImage).await()
                
                if (index > 0) {
                    allText.append("\n\n--- Page ${index + 1} ---\n\n")
                }
                allText.append(result.text)
            } catch (e: Exception) {
                Log.e("DocumentScanner", "Failed to extract text from page ${index + 1}", e)
            }
        }
        
        return allText.toString()
    }
    
    private fun returnScanSuccessResult(recognizedText: String?, pdfPath: String?) {
        val resultIntent = Intent().apply {
            putExtra(DocumentScanner.SCAN_SUCCESS_RESULT, DocumentScanner.REQUEST_SCAN_SUCCESS)
            putExtra(RECOGNIZED_TEXT, recognizedText.orEmpty())
            putExtra(PDF_PATH, pdfPath.orEmpty())
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        const val RECOGNIZED_TEXT = "RECOGNIZED_TEXT"
        const val PDF_PATH = "PDF_PATH"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"
        const val SCAN_FAILED = "Scan failed"
        const val SCAN_CANCELED = "Scan was cancelled"
        const val UPLOAD_CANCELED = "Upload was cancelled"
        const val NO_FILE_SELECTED = "No file selected"
    }
}
