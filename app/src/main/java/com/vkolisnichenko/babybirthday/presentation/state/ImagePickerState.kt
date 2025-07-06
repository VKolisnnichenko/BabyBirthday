package com.vkolisnichenko.babybirthday.presentation.state

data class ImagePickerState(
    val isProcessingImage: Boolean = false,
    val errorMessage: String? = null
) {
    val hasError: Boolean
        get() = errorMessage != null
}