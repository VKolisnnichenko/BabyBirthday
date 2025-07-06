package com.vkolisnichenko.babybirthday.domain.usecase

import android.net.Uri
import com.vkolisnichenko.babybirthday.domain.repository.ImageProcessor
import javax.inject.Inject

class ValidateImageUseCase @Inject constructor(
    private val imageProcessor: ImageProcessor
) {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val reason: String) : ValidationResult()
    }

    suspend operator fun invoke(uri: Uri): ValidationResult {
        return try {
            if (!imageProcessor.isImageSizeAcceptable(uri)) {
                return ValidationResult.Invalid("Image file is too large (max 10MB)")
            }

            val dimensions = imageProcessor.getImageDimensions(uri)
            if (dimensions == null) {
                return ValidationResult.Invalid("Invalid image format or corrupted file")
            }

            val (width, height) = dimensions

            if (width < MIN_IMAGE_DIMENSION || height < MIN_IMAGE_DIMENSION) {
                return ValidationResult.Invalid("Image too small (minimum ${MIN_IMAGE_DIMENSION}px)")
            }

            if (width > EXTREME_IMAGE_DIMENSION || height > EXTREME_IMAGE_DIMENSION) {
                return ValidationResult.Invalid("Image too large (maximum ${EXTREME_IMAGE_DIMENSION}px)")
            }

            ValidationResult.Valid

        } catch (e: Exception) {
            ValidationResult.Invalid("Error reading image: ${e.message}")
        }
    }

    companion object {
        private const val MIN_IMAGE_DIMENSION = 100
        private const val EXTREME_IMAGE_DIMENSION = 8192
    }
}