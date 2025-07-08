package com.carvana.carvana.upload

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.resources.Strings.FAILED_TO_COPY_FILE_TO_CACHE

/**
 * Android-specific implementation of DocumentUploadHandler
 */
class AndroidDocumentUploadHandler(
    private val context: Context
) : DocumentUploadHandler {

    companion object {
        private const val TAG = "AndroidUploadHandler"
        private const val CACHE_DIR_NAME = "uploads"
    }

    /**
     * Processes the uploaded document and returns the result.
     * This method handles different file types and performs necessary validations.
     */
    override suspend fun processUploadedDocument(fileData: Any): UploadResult =
        withContext(Dispatchers.IO) {
            try {
                if (fileData !is Uri) {
                    return@withContext UploadResultProcessor.createErrorResult("Invalid file data type")
                }

                val uri = fileData as Uri

                // Get file information
                val fileInfo = getFileInfo(uri)

                // Validate file using common validator
                val validationError = FileValidator.validate(fileInfo)
                if (validationError != null) {
                    return@withContext UploadResultProcessor.createErrorResult(validationError)
                }

                // Copy file to cache
                val cachedFile = copyFileToCache(uri, fileInfo.fileName)
                if (cachedFile == null) {
                    return@withContext UploadResultProcessor.createErrorResult("Failed to process file")
                }

                // Process based on file type
                return@withContext when (fileInfo.mimeType) {
                    "application/pdf" -> processPdfFile(cachedFile, fileInfo)
                    in listOf(
                        "image/jpeg",
                        "image/png",
                        "image/jpg"
                    ) -> processImageFile(cachedFile, fileInfo)

                    "text/plain" -> processTextFile(cachedFile, fileInfo)
                    else -> processGenericFile(cachedFile, fileInfo)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing uploaded document", e)
                return@withContext UploadResultProcessor.createErrorResult("Error processing document: ${e.message}")
            }
        }

    override fun validateFile(fileName: String, fileSize: Long, mimeType: String): String? {
        return FileValidator.validate(FileInfo(fileName, fileSize, mimeType))
    }

    override fun cleanupOldFiles(daysToKeep: Int) {
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
        if (!cacheDir.exists()) return

        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)

        cacheDir.listFiles()?.forEach { file ->
            if (file.lastModified() < cutoffTime) {
                file.delete()
            }
        }
    }

    /*
    - getFileInfo(uri) extracts filename, file size, MIME type from URI
    - Uses ContentResolver to query file metadata
    */
    private fun getFileInfo(uri: Uri): FileInfo {
        var fileName = "unknown_file"
        var fileSize = 0L
        var mimeType = "application/octet-stream"

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex) ?: fileName
                }
                if (sizeIndex >= 0) {
                    fileSize = cursor.getLong(sizeIndex)
                }
            }
        }

        context.contentResolver.getType(uri)?.let {
            mimeType = it
        }

        return FileInfo(fileName, fileSize, mimeType)
    }

    private fun copyFileToCache(uri: Uri, fileName: String): File? {
        return try {
            val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val timestamp = System.currentTimeMillis()
            val cachedFile = File(cacheDir, "${timestamp}_$fileName")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(cachedFile).use { output ->
                    input.copyTo(output)
                }
            }

            cachedFile
        } catch (e: IOException) {
            Log.e(TAG, FAILED_TO_COPY_FILE_TO_CACHE, e)
            null
        }
    }

    private fun processPdfFile(file: File, fileInfo: FileInfo): UploadResult {
        return UploadResultProcessor.createSuccessResult(
            fileName = fileInfo.fileName,
            filePath = file.absolutePath,
            fileType = "pdf",
            fileSize = file.length()
        )
    }

    private fun processImageFile(file: File, fileInfo: FileInfo): UploadResult {
        return UploadResultProcessor.createSuccessResult(
            fileName = fileInfo.fileName,
            filePath = file.absolutePath,
            fileType = "image",
            fileSize = file.length()
        )
    }

    private fun processTextFile(file: File, fileInfo: FileInfo): UploadResult {
        return try {
            val content = file.readText()
            UploadResultProcessor.createSuccessResult(
                fileName = fileInfo.fileName,
                filePath = file.absolutePath,
                fileType = "text",
                fileSize = file.length(),
                content = content
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read text file", e)
            UploadResultProcessor.createErrorResult("Failed to read text file: ${e.message}")
        }
    }

    private fun processGenericFile(file: File, fileInfo: FileInfo): UploadResult {
        return UploadResultProcessor.createSuccessResult(
            fileName = fileInfo.fileName,
            filePath = file.absolutePath,
            fileType = fileInfo.mimeType,
            fileSize = file.length()
        )
    }
}