package com.vkolisnichenko.babybirthday.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.vkolisnichenko.babybirthday.domain.repository.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class SaveScreenshotUseCase @Inject constructor(
    private val fileManager: FileManager
) {

    sealed class Result {
        data class Success(val uri: Uri) : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(bitmap: Bitmap): Result = withContext(Dispatchers.IO) {
        try {
            val screenshotFile = createScreenshotFile()

            val success = saveBitmapToFile(bitmap, screenshotFile)
            if (!success) {
                return@withContext Result.Error("Failed to save screenshot")
            }

            val uri = fileManager.getFileProviderUri(screenshotFile)
            Result.Success(uri)
        } catch (e: Exception) {
            Result.Error("Failed to save screenshot: ${e.message}")
        }
    }

    private fun createScreenshotFile(): File {
        val tempFile = fileManager.createTempCameraFile()
        val screenshotFile = File(tempFile.parent, "birthday_screenshot_${System.currentTimeMillis()}.jpg")
        return screenshotFile
    }

    private suspend fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                true
            } catch (e: IOException) {
                false
            }
        }
}