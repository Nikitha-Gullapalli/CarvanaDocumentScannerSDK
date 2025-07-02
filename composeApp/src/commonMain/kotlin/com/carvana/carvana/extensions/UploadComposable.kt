package com.carvana.carvana.extensions

import com.carvana.carvana.interfaces.SDKCallbackManager
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.interfaces.logger

fun uploadDocument(documentUploader: DocumentUploader) {
    documentUploader.uploadDocument { result ->
        when (result) {
            is UploadResult.Success -> {
                logger.d("uploadDocument", "Text recognized: ${result.recognizedText}")
                SDKCallbackManager.handleSuccess(result.recognizedText)
            }

            is UploadResult.Failure -> {
                logger.e("uploadDocument", "Failed: ${result.message}")
                SDKCallbackManager.handleFailure(result.message)
            }
        }
    }
}
