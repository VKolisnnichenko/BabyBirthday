package com.vkolisnichenko.babybirthday.domain.usecase

import android.net.Uri
import com.vkolisnichenko.babybirthday.domain.repository.FileManager
import com.vkolisnichenko.babybirthday.domain.repository.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectImageUseCase @Inject constructor(
    private val fileManager: FileManager,
    private val imageProcessor: ImageProcessor
) {

    sealed class Result {
        data class Success(val savedImagePath: String) : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(
        sourceUri: Uri,
        previousImagePath: String? = null
    ): Result = withContext(Dispatchers.IO) {

        try {
            if (!imageProcessor.isImageSizeAcceptable(sourceUri)) {
                return@withContext Result.Error("Image file is too large. Please select a smaller image.")
            }

            val targetFile = fileManager.createImageFile()

            val processingSuccess = imageProcessor.processAndSaveImage(sourceUri, targetFile)

            if (!processingSuccess) {
                fileManager.deleteFile(targetFile.absolutePath)
                return@withContext Result.Error("Failed to process image. Please try another image.")
            }

            if (!fileManager.isFileValid(targetFile.absolutePath)) {
                fileManager.deleteFile(targetFile.absolutePath)
                return@withContext Result.Error("Processed image is invalid. Please try again.")
            }

            previousImagePath?.let { oldPath ->
                if (oldPath != targetFile.absolutePath) {
                    fileManager.deleteFile(oldPath)
                }
            }

            fileManager.cleanupTempFiles()

            Result.Success(targetFile.absolutePath)

        } catch (e: SecurityException) {
            Result.Error("Permission denied. Please grant necessary permissions.")
        } catch (e: OutOfMemoryError) {
            Result.Error("Not enough memory to process this image. Please try a smaller image.")
        } catch (e: Exception) {
            Result.Error("Unexpected error occurred: ${e.message ?: "Unknown error"}")
        }
    }
}