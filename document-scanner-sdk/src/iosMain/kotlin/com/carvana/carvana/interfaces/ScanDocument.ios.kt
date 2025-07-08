package com.carvana.carvana.interfaces

import com.carvana.carvana.utils.ViewControllerHelper
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIDevice
import platform.UIKit.UIGraphicsBeginPDFContextToFile
import platform.UIKit.UIGraphicsBeginPDFPageWithInfo
import platform.UIKit.UIGraphicsEndPDFContext
import platform.UIKit.UIImage
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate
import platform.VisionKit.VNDocumentCameraScan
import platform.VisionKit.VNDocumentCameraViewController
import platform.VisionKit.VNDocumentCameraViewControllerDelegateProtocol
import platform.darwin.NSObject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DocumentScanner {

    companion object {
        private var currentCallback: ((ScanResult) -> Unit)? = null

        fun handleScanResult(result: ScanResult) {
            currentCallback?.invoke(result)
            currentCallback = null
        }
    }

    actual fun scanDocument(onResult: (ScanResult) -> Unit) {
        currentCallback = onResult

        try {
            // Check if running on simulator
            val isSimulator = UIDevice.currentDevice.model.contains("Simulator")
            
            if (isSimulator) {
                onResult(ScanResult.Failure("Camera not available on iOS Simulator. Please test on a physical device."))
                return
            }
            
            if (VNDocumentCameraViewController.isSupported()) {
                val documentCamera = VNDocumentCameraViewController()
                val delegate = DocumentCameraDelegate()
                documentCamera.delegate = delegate

                val rootVC = ViewControllerHelper.getPresentingViewController()
                if (rootVC != null) {
                    rootVC.presentViewController(
                        viewControllerToPresent = documentCamera,
                        animated = true,
                        completion = null
                    )
                } else {
                    onResult(ScanResult.Failure("Unable to present document scanner: No root view controller found"))
                }
            } else {
                onResult(ScanResult.Failure("Document scanning not supported on this device (requires iOS 13+)"))
            }
        } catch (e: Exception) {
            onResult(ScanResult.Failure("Failed to open document scanner: ${e.message}"))
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private class DocumentCameraDelegate : NSObject(), VNDocumentCameraViewControllerDelegateProtocol {

        override fun documentCameraViewController(
            controller: VNDocumentCameraViewController,
            didFinishWithScan: VNDocumentCameraScan
        ) {
            if (didFinishWithScan.pageCount > 0u) {
                // Process all scanned pages
                val images = mutableListOf<UIImage>()
                for (i in 0u until didFinishWithScan.pageCount.toUInt()) {
                    images.add(didFinishWithScan.imageOfPageAtIndex(i.toULong()))
                }
                processImagesWithVisionOCR(images)
            } else {
                handleScanResult(ScanResult.Failure("No pages were scanned"))
            }
            controller.dismissViewControllerAnimated(true, completion = null)
        }

        override fun documentCameraViewController(
            controller: VNDocumentCameraViewController,
            didFailWithError: NSError
        ) {
            handleScanResult(ScanResult.Failure("Scan failed: ${didFailWithError.localizedDescription}"))
            controller.dismissViewControllerAnimated(true, completion = null)
        }

        override fun documentCameraViewControllerDidCancel(controller: VNDocumentCameraViewController) {
            handleScanResult(ScanResult.Failure("Scan was cancelled"))
            controller.dismissViewControllerAnimated(true, completion = null)
        }

        private fun processImagesWithVisionOCR(images: List<UIImage>) {
            try {
                val allRecognizedText = StringBuilder()
                var processedCount = 0
                
                // First, create PDF from all images
                val pdfPath = createPDFFromImages(images)
                
                // Process each image for text recognition
                images.forEachIndexed { index, image ->
                    val cgImage = image.CGImage
                    if (cgImage != null) {
                        // Create text recognition request
                        val textRequest = VNRecognizeTextRequest { request, error ->
                            if (error != null) {
                                // Continue processing other pages even if one fails
                                processedCount++
                                if (processedCount == images.size) {
                                    finishProcessing(allRecognizedText.toString().trim(), pdfPath)
                                }
                                return@VNRecognizeTextRequest
                            }

                            val results = request?.results
                            
                            if (index > 0) {
                                allRecognizedText.append("\n\n--- Page ${index + 1} ---\n\n")
                            }

                            results?.forEach { result ->
                                if (result is VNRecognizedTextObservation) {
                                    val topCandidate = result.topCandidates(1u).firstOrNull()
                                    if (topCandidate != null) {
                                        val text = (topCandidate as? VNRecognizedText)?.string ?: ""
                                        allRecognizedText.append(text).append("\n")
                                    }
                                }
                            }
                            
                            processedCount++
                            if (processedCount == images.size) {
                                finishProcessing(allRecognizedText.toString().trim(), pdfPath)
                            }
                        }
                        
                        textRequest.usesLanguageCorrection = true
                        textRequest.recognitionLevel = VNRequestTextRecognitionLevelAccurate

                        val requestHandler = VNImageRequestHandler(cgImage, options = emptyMap<Any?, Any?>())
                        
                        // Perform request asynchronously
                        try {
                            requestHandler.performRequests(listOf(textRequest), null)
                        } catch (e: Exception) {
                            processedCount++
                            if (processedCount == images.size) {
                                finishProcessing(allRecognizedText.toString().trim(), pdfPath)
                            }
                        }
                    } else {
                        processedCount++
                        if (processedCount == images.size) {
                            finishProcessing(allRecognizedText.toString().trim(), pdfPath)
                        }
                    }
                }
            } catch (e: Exception) {
                handleScanResult(ScanResult.Failure("Image processing error: ${e.message}"))
            }
        }
        
        private fun finishProcessing(recognizedText: String, pdfPath: String?) {
            if (recognizedText.isNotEmpty()) {
                handleScanResult(ScanResult.Success(
                    recognizedText,
                    pdfPath ?: ""
                ))
            } else {
                handleScanResult(ScanResult.Success(
                    "Document scanned successfully but no text was recognized",
                    pdfPath ?: ""
                ))
            }
        }
        
        @OptIn(ExperimentalForeignApi::class)
        private fun createPDFFromImages(images: List<UIImage>): String? {
            try {
                val documentsPath = NSSearchPathForDirectoriesInDomains(
                    NSDocumentDirectory,
                    NSUserDomainMask,
                    true
                ).firstOrNull() as? String ?: return null
                
                val fileName = "scan_${NSDate().timeIntervalSince1970.toLong()}.pdf"
                val pdfPath = "$documentsPath/$fileName"
                
                UIGraphicsBeginPDFContextToFile(pdfPath, CGRectZero.readValue(), null)
                
                images.forEach { image ->
                    val imageRect = CGRectMake(0.0, 0.0, image.size.useContents { width }, image.size.useContents { height })
                    UIGraphicsBeginPDFPageWithInfo(imageRect, null)
                    image.drawInRect(imageRect)
                }
                
                UIGraphicsEndPDFContext()
                
                return pdfPath
            } catch (e: Exception) {
                return null
            }
        }

    }
}
