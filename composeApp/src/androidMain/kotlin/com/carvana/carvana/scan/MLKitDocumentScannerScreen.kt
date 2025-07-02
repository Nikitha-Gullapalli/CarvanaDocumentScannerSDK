package com.carvana.carvana.scan

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.launch

@Composable
fun MLKitDocumentScannerScreen(
    onScanComplete: (List<Bitmap>, String?) -> Unit = { _, _ -> },
    onError: (Exception) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = context !is Activity
    
    var isScanning by remember { mutableStateOf(false) }
    
    if (isPreview) {
        // Show preview UI
        MLKitDocumentScannerPreviewContent()
        return
    }
    
    val activity = context as Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Configure scanner options
    val scannerOptions = remember {
        GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(false)
            .setPageLimit(10)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
    }
    
    // Create scanner client
    val scanner = remember {
        GmsDocumentScanning.getClient(scannerOptions)
    }
    
    // Activity result launcher for the scanner
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        isScanning = false

        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)

                scanningResult?.let { gmsResult ->
                    lifecycleOwner.lifecycleScope.launch {
                        try {
                            val bitmaps = mutableListOf<Bitmap>()
                            var pdfPath: String? = null

                            // Process pages
                            gmsResult.pages?.forEach { page ->
                                page.imageUri.let { uri ->
                                    val bitmap = loadBitmapFromUri(context, uri)
                                    bitmap?.let { bitmaps.add(it) }
                                }
                            }

                            // Get PDF if available
                            gmsResult.pdf?.let { pdf ->
                                pdf.uri.let { uri ->
                                    // Save PDF to cache
                                    val cacheFile = saveUriToCache(context, uri, "scan_${System.currentTimeMillis()}.pdf")
                                    pdfPath = cacheFile?.absolutePath
                                }
                            }

                            if (bitmaps.isNotEmpty() || pdfPath != null) {
                                onScanComplete(bitmaps, pdfPath)
                            } else {
                                onError(Exception("No scan results found"))
                            }
                        } catch (e: Exception) {
                            Log.e("MLKitScanner", "Error processing scan results", e)
                            onError(e)
                        }
                    }
                } ?: run {
                    onError(Exception("Failed to get scanning result"))
                }
            }
            Activity.RESULT_CANCELED -> {
                onCancel()
            }
            else -> {
                onError(Exception("Scanning failed with result code: ${result.resultCode}"))
            }
        }
    }
    
    // Launch scanner when screen loads
    LaunchedEffect(Unit) {
        if (!isScanning) {
            isScanning = true
            
            scanner.getStartScanIntent(activity)
                .addOnSuccessListener { intentSender ->
                    val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                    scannerLauncher.launch(intentSenderRequest)
                }
                .addOnFailureListener { e ->
                    isScanning = false
                    Log.e("MLKitScanner", "Failed to start scanner", e)
                    onError(e)
                }
        }
    }
    
    // Show loading indicator while scanner is launching
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isScanning) {
            CircularProgressIndicator()
        }
    }
}

private fun loadBitmapFromUri(context: Activity, uri: Uri): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input)
        }
    } catch (e: Exception) {
        Log.e("MLKitScanner", "Failed to load bitmap from URI", e)
        null
    }
}

private fun saveUriToCache(context: Activity, uri: Uri, fileName: String): java.io.File? {
    return try {
        val cacheFile = java.io.File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            java.io.FileOutputStream(cacheFile).use { output ->
                input.copyTo(output)
            }
        }
        cacheFile
    } catch (e: Exception) {
        Log.e("MLKitScanner", "Failed to save file to cache", e)
        null
    }
}

@Composable
private fun MLKitDocumentScannerPreviewContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun MLKitDocumentScannerScreenPreview() {
    MLKitDocumentScannerScreen()
}