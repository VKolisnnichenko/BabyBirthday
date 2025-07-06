package com.vkolisnichenko.babybirthday.domain.usecase

import android.graphics.Bitmap
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.graphics.createBitmap

class CaptureScreenshotUseCase @Inject constructor() {

    sealed class Result {
        data class Success(val bitmap: Bitmap) : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(view: View): Result = withContext(Dispatchers.Main) {
        try {
            if (view.width == 0 || view.height == 0) {
                return@withContext Result.Error("View has no dimensions")
            }

            val bitmap = withContext(Dispatchers.Default) {
                try {
                    val bitmap = createBitmap(view.width, view.height)

                    val canvas = android.graphics.Canvas(bitmap)
                    view.draw(canvas)
                    bitmap
                } catch (e: Exception) {
                    null
                }
            }

            if (bitmap != null) {
                Result.Success(bitmap)
            } else {
                Result.Error("Failed to create bitmap from view")
            }
        } catch (e: Exception) {
            Result.Error("Failed to capture screenshot: ${e.message}")
        }
    }
}