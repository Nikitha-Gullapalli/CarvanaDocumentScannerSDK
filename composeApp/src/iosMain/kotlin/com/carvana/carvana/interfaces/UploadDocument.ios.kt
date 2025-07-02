package com.carvana.carvana.interfaces

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DocumentUploader {

    companion object {
        private var currentCallback: ((UploadResult) -> Unit)? = null
        private var documentPickerDelegate: DocumentPickerDelegate? = null

        fun handleUploadResult(result: UploadResult) {
            currentCallback?.invoke(result)
            currentCallback = null
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun uploadDocument(onResult: (UploadResult) -> Unit) {
        currentCallback = onResult

        try {
            val documentTypes = listOf(
                "public.image",
                "com.adobe.pdf",
                "public.text",
                "public.data"
            )

            val nsDocumentTypes = NSArray.arrayWithObjects(documentTypes.map { NSString.create(string = it) }.toTypedArray())


            val documentPicker = UIDocumentPickerViewController(
                documentTypes = nsDocumentTypes,
                inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
            )

            val delegate = DocumentPickerDelegate()
            documentPicker.delegate = delegate
            documentPickerDelegate = delegate // Keep strong reference to avoid deallocation

            //val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
            val rootVC = UIApplication.sharedApplication.delegate?.window?.rootViewController
            rootVC?.presentViewController(documentPicker, animated = true, completion = null)

        } catch (e: Exception) {
            onResult(UploadResult.Failure("Failed to open document picker: ${e.message}"))
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private class DocumentPickerDelegate : NSObject(), UIDocumentPickerDelegateProtocol {

        override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentAtURL: NSURL) {
            processSelectedDocument(didPickDocumentAtURL)
            controller.dismissViewControllerAnimated(true, null)
        }

        override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
            handleUploadResult(UploadResult.Failure("Upload was cancelled"))
            controller.dismissViewControllerAnimated(true, null)
        }

        private fun processSelectedDocument(url: NSURL) {
            try {
                val fileName = url.lastPathComponent ?: "Unknown file"
                val fileExtension = url.pathExtension?.lowercase() ?: ""

                if (url.startAccessingSecurityScopedResource()) {
                    val recognizedText = when (fileExtension) {
                        "txt" -> {
                            try {
                                val content = NSString.stringWithContentsOfURL(url, encoding = NSUTF8StringEncoding, error = null)
                                "Text Document: $fileName\nContent: ${content ?: "Could not read file content"}"
                            } catch (e: Exception) {
                                "Text Document: $fileName - Error reading file: ${e.message}"
                            }
                        }
                        "pdf" -> {
                            val data = NSData.dataWithContentsOfURL(url)
                            val fileSize = data?.length ?: 0u
                            "PDF Document: $fileName (${fileSize} bytes) - PDF content extraction can be added here"
                        }
                        "jpg", "jpeg", "png", "gif", "heic" -> {
                            val data = NSData.dataWithContentsOfURL(url)
                            val fileSize = data?.length ?: 0u
                            "Image Document: $fileName (${fileSize} bytes) - OCR text extraction can be added here"
                        }
                        else -> {
                            val data = NSData.dataWithContentsOfURL(url)
                            val fileSize = data?.length ?: 0u
                            "Document: $fileName (${fileSize} bytes) - File uploaded successfully"
                        }
                    }

                    url.stopAccessingSecurityScopedResource()
                    handleUploadResult(UploadResult.Success(recognizedText))
                } else {
                    handleUploadResult(UploadResult.Failure("Could not access selected file"))
                }
            } catch (e: Exception) {
                handleUploadResult(UploadResult.Failure("Error processing file: ${e.message}"))
            }
        }
    }
}
