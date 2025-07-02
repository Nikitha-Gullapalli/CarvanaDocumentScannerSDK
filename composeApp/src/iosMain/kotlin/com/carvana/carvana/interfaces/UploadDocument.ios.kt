package com.carvana.carvana.interfaces

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject
import com.carvana.carvana.utils.ViewControllerHelper
import kotlinx.datetime.Clock

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DocumentUploader {

    companion object {
        private var currentCallback: ((UploadResult) -> Unit)? = null
        private var documentPickerDelegate: DocumentPickerDelegate? = null
        private var photoPickerDelegate: PhotoPickerDelegate? = null

        fun handleUploadResult(result: UploadResult) {
            currentCallback?.invoke(result)
            currentCallback = null
        }
    }

    actual fun uploadDocument(onResult: (UploadResult) -> Unit) {
        currentCallback = onResult

        try {
            // Get the proper view controller for presentation
            val rootVC = ViewControllerHelper.getPresentingViewController()
            
            if (rootVC == null) {
                onResult(UploadResult.Failure("Unable to present picker: No root view controller found"))
                return
            }

            // Create action sheet to let user choose between Photos and Files
            val alertController = UIAlertController.alertControllerWithTitle(
                title = "Select Source",
                message = "Choose where to upload from",
                preferredStyle = 0 // UIAlertControllerStyleActionSheet
            )

            // Add Photos option
            alertController.addAction(
                UIAlertAction.actionWithTitle("Photos", style = 0) { _ -> // UIAlertActionStyleDefault
                    showPhotoPicker(rootVC)
                }
            )

            // Add Files option
            alertController.addAction(
                UIAlertAction.actionWithTitle("Files", style = 0) { _ -> // UIAlertActionStyleDefault
                    showDocumentPicker(rootVC)
                }
            )

            // Add Cancel option
            alertController.addAction(
                UIAlertAction.actionWithTitle("Cancel", style = 1) { _ -> // UIAlertActionStyleCancel
                    handleUploadResult(UploadResult.Failure("Upload cancelled"))
                }
            )

            // Present the action sheet
            rootVC.presentViewController(alertController, animated = true, completion = null)

        } catch (e: Exception) {
            onResult(UploadResult.Failure("Failed to show upload options: ${e.message}"))
        }
    }

    private fun showPhotoPicker(rootVC: UIViewController) {
        val imagePicker = UIImagePickerController()
        imagePicker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        imagePicker.allowsEditing = false
        
        val delegate = PhotoPickerDelegate()
        imagePicker.setDelegate(delegate)
        photoPickerDelegate = delegate // Keep strong reference
        
        rootVC.presentViewController(imagePicker, animated = true, completion = null)
    }

    private fun showDocumentPicker(rootVC: UIViewController) {
        val documentTypes = listOf(
            "public.image",
            "com.adobe.pdf",
            "public.text",
            "public.data"
        )

        // Create document picker with proper initialization
        @Suppress("UNCHECKED_CAST")
        val documentPicker = UIDocumentPickerViewController(
            documentTypes = documentTypes as List<Any?>,
            inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
        )

        val delegate = DocumentPickerDelegate()
        documentPicker.setDelegate(delegate)
        documentPickerDelegate = delegate // Keep strong reference

        rootVC.presentViewController(documentPicker, animated = true, completion = null)
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
                                @OptIn(BetaInteropApi::class)
                                val content = NSString.create(contentsOfURL = url, encoding = NSUTF8StringEncoding, error = null)
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

    @OptIn(ExperimentalForeignApi::class)
    private class PhotoPickerDelegate : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        
        override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
            picker.dismissViewControllerAnimated(true, null)
            
            val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            if (image != null) {
                // Convert UIImage to data and save temporarily
                val imageData = UIImageJPEGRepresentation(image, 0.8)
                if (imageData != null) {
                    val fileName = "picked_image_${Clock.System.now().toEpochMilliseconds()}.jpg"
                    val tempDir = NSTemporaryDirectory()
                    val filePath = "$tempDir$fileName"
                    
                    if (imageData.writeToFile(filePath, atomically = true)) {
                        handleUploadResult(UploadResult.Success(
                            "Image uploaded: $fileName\nPath: $filePath\nSize: ${imageData.length} bytes"
                        ))
                    } else {
                        handleUploadResult(UploadResult.Failure("Failed to save image"))
                    }
                } else {
                    handleUploadResult(UploadResult.Failure("Failed to convert image to data"))
                }
            } else {
                handleUploadResult(UploadResult.Failure("No image selected"))
            }
        }
        
        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
            handleUploadResult(UploadResult.Failure("Photo selection cancelled"))
        }
    }
}
