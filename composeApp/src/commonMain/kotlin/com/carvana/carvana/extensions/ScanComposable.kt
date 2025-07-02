package com.carvana.carvana.extensions

import com.carvana.carvana.interfaces.SDKCallbackManager
import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.ScanResult
import com.carvana.carvana.interfaces.logger

fun scanDocument(documentScanner: DocumentScanner) {
    documentScanner.scanDocument { result ->
        when (result) {
            is ScanResult.Success -> {
                logger.d("scanDocument", "Success: pdf Path recognized: ${result.pdfPath}")

                SDKCallbackManager.handleSuccess(result.pdfPath)
            }

            is ScanResult.Failure -> {
                logger.e("scanDocument", "Failed: ${result.message}")
                SDKCallbackManager.handleFailure(result.message)
            }
        }
    }
}
