package com.vkolisnichenko.babybirthday.presentation.state

data class ShareState(
    val isSharing: Boolean = false,
    val hideUIForScreenshot: Boolean = false,
    val errorMessage: String? = null
) {
    val hasError: Boolean
        get() = errorMessage != null
}