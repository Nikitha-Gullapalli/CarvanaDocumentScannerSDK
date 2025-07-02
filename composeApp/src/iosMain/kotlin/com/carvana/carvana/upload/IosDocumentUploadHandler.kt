package com.carvana.carvana.upload

import androidx.compose.ui.text.toLowerCase
import com.carvana.carvana.interfaces.UploadResult
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.UIDocument
import platform.UniformTypeIdentifiers.*
import platform.darwin.NSObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * iOS-specific implementation of DocumentUploadHandler
 */
class IosDocumentUploadHandler : DocumentUploadHandler {
    
    companion object {
        private const val CACHE_DIR_NAME = "uploads"
    }
    
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun processUploadedDocument(fileData: Any): UploadResult = withContext(Dispatchers.Main) {
        try {
            if (fileData !is NSURL) {
                return@withContext UploadResultProcessor.createErrorResult("Invalid file data type")
            }
            
            val url = fileData as NSURL
            
            // Get file information
            val fileInfo = getFileInfo(url)
            
            // Validate file using common validator
            val validationError = FileValidator.validate(fileInfo)
            if (validationError != null) {
                return@withContext UploadResultProcessor.createErrorResult(validationError)
            }
            
            // Copy file to cache
            val cachedFile = copyFileToCache(url, fileInfo.fileName)
            if (cachedFile == null) {
                return@withContext UploadResultProcessor.createErrorResult("Failed to process file")
            }
            
            // Process based on file type
            return@withContext when (fileInfo.mimeType) {
                "application/pdf" -> processPdfFile(cachedFile, fileInfo)
                in listOf("image/jpeg", "image/png", "image/jpg") -> processImageFile(cachedFile, fileInfo)
                "text/plain" -> processTextFile(cachedFile, fileInfo)
                else -> processGenericFile(cachedFile, fileInfo)
            }
        } catch (e: Exception) {
            return@withContext UploadResultProcessor.createErrorResult("Error processing document: ${e.message}")
        }
    }
    
    override fun validateFile(fileName: String, fileSize: Long, mimeType: String): String? {
        return FileValidator.validate(FileInfo(fileName, fileSize, mimeType))
    }
    
    @OptIn(ExperimentalForeignApi::class)
    override fun cleanupOldFiles(daysToKeep: Int) {
        val fileManager = NSFileManager.defaultManager
        val cacheDir = getCacheDirectory() ?: return
        
        val cutoffDate = NSDate().dateByAddingTimeInterval(-daysToKeep.toDouble() * 24 * 60 * 60)
        
        val enumerator = fileManager.enumeratorAtPath(cacheDir)
        while (true) {
            val fileName = enumerator?.nextObject() as? String ?: break
            val filePath = "$cacheDir/$fileName"
            
            val attributes = fileManager.attributesOfItemAtPath(filePath, null)
            val modificationDate = attributes?.get(NSFileModificationDate) as? NSDate
            
            if (modificationDate != null && modificationDate.compare(cutoffDate) == NSOrderedAscending) {
                fileManager.removeItemAtPath(filePath, null)
            }
        }
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun getFileInfo(url: NSURL): FileInfo {
        val fileName = url.lastPathComponent ?: "unknown_file"
        var fileSize = 0L
        var mimeType = "application/octet-stream"
        
        // Get file size
        val fileManager = NSFileManager.defaultManager
        url.path?.let { path ->
            val attributes = fileManager.attributesOfItemAtPath(path, null)
            fileSize = (attributes?.get(NSFileSize) as? NSNumber)?.longValue ?: 0L
        }
        
        // Get MIME type from file extension
        val fileExtension = url.pathExtension
        mimeType = when (fileExtension?.lowercase()) {
            "pdf" -> "application/pdf"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "txt" -> "text/plain"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            else -> "application/octet-stream"
        }
        
        return FileInfo(fileName, fileSize, mimeType)
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun copyFileToCache(url: NSURL, fileName: String): NSURL? {
        return try {
            val cacheDir = getCacheDirectory() ?: return null
            val fileManager = NSFileManager.defaultManager
            
            // Create cache directory if it doesn't exist
            val uploadDir = "$cacheDir/$CACHE_DIR_NAME"
            if (!fileManager.fileExistsAtPath(uploadDir)) {
                fileManager.createDirectoryAtPath(uploadDir, true, null, null)
            }
            
            val timestamp = NSDate().timeIntervalSince1970.toLong()
            val cachedFilePath = "$uploadDir/${timestamp}_$fileName"
            val cachedFileURL = NSURL.fileURLWithPath(cachedFilePath)
            
            // Copy file to cache
            fileManager.copyItemAtURL(url, cachedFileURL, null)
            
            cachedFileURL
        } catch (e: Exception) {
            null
        }
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun getCacheDirectory(): String? {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSCachesDirectory,
            NSUserDomainMask,
            true
        )
        return paths.firstOrNull() as? String
    }
    
    private fun processPdfFile(file: NSURL, fileInfo: FileInfo): UploadResult {
        return UploadResultProcessor.createSuccessResult(
            fileName = fileInfo.fileName,
            filePath = file.path ?: "",
            fileType = "pdf",
            fileSize = fileInfo.fileSize
        )
    }
    
    private fun processImageFile(file: NSURL, fileInfo: FileInfo): UploadResult {
        return UploadResultProcessor.createSuccessResult(
            fileName = fileInfo.fileName,
            filePath = file.path ?: "",
            fileType = "image",
            fileSize = fileInfo.fileSize
        )
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun processTextFile(file: NSURL, fileInfo: FileInfo): UploadResult {
        return try {
            val content = NSString.stringWithContentsOfURL(file, NSUTF8StringEncoding, null) as? String
            UploadResultProcessor.createSuccessResult(
                fileName = fileInfo.fileName,
                filePath = file.path ?: "",
                fileType = "text",
                fileSize = fileInfo.fileSize,
                content = content
            )
        } catch (e: Exception) {
            UploadResultProcessor.createErrorResult("Failed to read text file: ${e.message}")
        }
    }
    
    private fun processGenericFile(file: NSURL, fileInfo: FileInfo): UploadResult {
        return UploadResultProcessor.createSuccessResult(
            fileName = fileInfo.fileName,
            filePath = file.path ?: "",
            fileType = fileInfo.mimeType,
            fileSize = fileInfo.fileSize
        )
    }
}