package com.carvana.carvana.interfaces

import com.carvana.carvana.FileExtension
import com.carvana.carvana.IOSDocumentType
import com.carvana.carvana.FileExtension.GIF
import com.carvana.carvana.FileExtension.HEIC
import com.carvana.carvana.FileExtension.JPG
import com.carvana.carvana.FileExtension.JPEG
import com.carvana.carvana.FileExtension.PNG
import com.carvana.carvana.FileExtension.PDF
import com.carvana.carvana.FileExtension.TXT
import com.carvana.carvana.resources.Strings.CANCEL
import com.carvana.carvana.resources.Strings.CHOSE_WHERE_TO_UPLOAD_FROM
import com.carvana.carvana.resources.Strings.ERROR_PROCESSING_FILE
import com.carvana.carvana.resources.Strings.FAILED_TO_CONVERT_IMAGE_TO_DATA
import com.carvana.carvana.resources.Strings.FAILED_TO_SAVE_IMAGE
import com.carvana.carvana.resources.Strings.FILES
import com.carvana.carvana.resources.Strings.NO_ACCESS_TO_FILE
import com.carvana.carvana.resources.Strings.NO_IMAGE_SELECTED
import com.carvana.carvana.resources.Strings.NO_ROOT_VIEW_FOUND
import com.carvana.carvana.resources.Strings.PHOTOS_LIBRARY
import com.carvana.carvana.resources.Strings.PHOTO_SELECTION_CANCELED
import com.carvana.carvana.resources.Strings.PICKED_IMAGE
import com.carvana.carvana.resources.Strings.SELECT_SOURCE
import com.carvana.carvana.resources.Strings.UNCHECKED_CAST
import com.carvana.carvana.resources.Strings.UNKNOWN_FILE
import com.carvana.carvana.resources.Strings.UPLOAD_CANCELED
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
                onResult(UploadResult.Failure(NO_ROOT_VIEW_FOUND))
                return
            }

            // Create action sheet to let user choose between Photos and Files
            val alertController = UIAlertController.alertControllerWithTitle(
                title = SELECT_SOURCE,
                message = CHOSE_WHERE_TO_UPLOAD_FROM,
                // UIAlertControllerStyleActionSheet
                preferredStyle = 0
            )

            // Add Photos option
            alertController.addAction(
                UIAlertAction.actionWithTitle(
                    PHOTOS_LIBRARY,
                    style = 0
                ) { _ ->
                    // UIAlertActionStyleDefault
                    showPhotoPicker(rootVC)
                }
            )

            // Add Files option
            alertController.addAction(
                UIAlertAction.actionWithTitle(
                    FILES,
                    style = 0
                ) { _ ->
                    // UIAlertActionStyleDefault
                    showDocumentPicker(rootVC)
                }
            )

            // Add Cancel option
            alertController.addAction(
                UIAlertAction.actionWithTitle(
                    CANCEL,
                    style = 1
                ) { _ ->
                    // UIAlertActionStyleCancel
                    handleUploadResult(UploadResult.Failure(UPLOAD_CANCELED))
                }
            )

            // Present the action sheet
            rootVC.presentViewController(alertController, animated = true, completion = null)

        } catch (e: Exception) {
            onResult(UploadResult.Failure("FAILED_TO_SHOW_UPLOAD_OPTIONS ${e.message}"))
        }
    }

    private fun showPhotoPicker(rootVC: UIViewController) {
        val imagePicker = UIImagePickerController()
        imagePicker.sourceType =
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        imagePicker.allowsEditing = false

        val delegate = PhotoPickerDelegate()
        imagePicker.setDelegate(delegate)
        photoPickerDelegate = delegate

        rootVC.presentViewController(imagePicker, animated = true, completion = null)
    }


    private fun showDocumentPicker(rootVC: UIViewController) {
        val documentTypes = IOSDocumentType.getAllTypes()

        // Create document picker with proper initialization
        @Suppress(UNCHECKED_CAST)
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

        override fun documentPicker(
            controller: UIDocumentPickerViewController,
            didPickDocumentAtURL: NSURL
        ) {
            processSelectedDocument(didPickDocumentAtURL)
            controller.dismissViewControllerAnimated(true, null)
        }

        override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
            handleUploadResult(UploadResult.Failure(UPLOAD_CANCELED))
            controller.dismissViewControllerAnimated(true, null)
        }

        private fun processSelectedDocument(url: NSURL) {
            try {
                val fileName = url.lastPathComponent ?: UNKNOWN_FILE
                val fileExtension = url.pathExtension?.lowercase().orEmpty()

                if (url.startAccessingSecurityScopedResource()) {
                    val recognizedText = when (FileExtension.fromString(fileExtension)) {
                        TXT -> {
                            try {
                                @OptIn(BetaInteropApi::class)
                                val content = NSString.create(
                                    contentsOfURL = url,
                                    encoding = NSUTF8StringEncoding,
                                    error = null
                                )
                                "$TXT Document $fileName\nContent: ${content ?: "Could not read file content"}"
                            } catch (e: Exception) {
                                "$TXT Document: $fileName - Error reading file: ${e.message}"
                            }
                        }

                        PDF -> {
                            val data = NSData.dataWithContentsOfURL(url)
                            val fileSize = data?.length ?: 0u
                            "$PDF Document: $fileName (${fileSize} bytes) - PDF content extraction can be added here"
                        }

                        JPG, JPEG, PNG, GIF, HEIC -> {
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
                    handleUploadResult(UploadResult.Failure(NO_ACCESS_TO_FILE))
                }
            } catch (e: Exception) {
                handleUploadResult(UploadResult.Failure("$ERROR_PROCESSING_FILE ${e.message}"))
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private class PhotoPickerDelegate : NSObject(), UIImagePickerControllerDelegateProtocol,
        UINavigationControllerDelegateProtocol {

        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            picker.dismissViewControllerAnimated(true, null)

            val image =
                didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            if (image != null) {
                // Convert UIImage to data and save temporarily
                val imageData = UIImageJPEGRepresentation(image, 0.8)
                if (imageData != null) {
                    val fileName =
                        "$PICKED_IMAGE${Clock.System.now().toEpochMilliseconds()}.${JPG.value}"
                    val tempDir = NSTemporaryDirectory()
                    val filePath = "$tempDir$fileName"

                    if (imageData.writeToFile(filePath, atomically = true)) {
                        handleUploadResult(
                            UploadResult.Success(
                                "Image uploaded: $fileName\nPath: $filePath\nSize: ${imageData.length} bytes"
                            )
                        )
                    } else {
                        handleUploadResult(FAILED_TO_SAVE_IMAGE)
                    }
                } else {
                    handleUploadResult(FAILED_TO_CONVERT_IMAGE_TO_DATA)
                }
            } else {
                handleUploadResult(NO_IMAGE_SELECTED)
            }
        }

        fun handleUploadResult(message: String) {
            handleUploadResult(UploadResult.Failure(message))
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
            handleUploadResult(UploadResult.Failure(PHOTO_SELECTION_CANCELED))
        }
    }
}