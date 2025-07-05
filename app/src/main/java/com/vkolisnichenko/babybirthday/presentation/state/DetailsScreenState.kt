package com.vkolisnichenko.babybirthday.presentation.state

import java.time.LocalDate

data class DetailsScreenState(
    val name: String = "",
    val birthday: LocalDate? = null,
    val photoPath: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() && birthday != null

    val hasPhoto: Boolean
        get() = photoPath != null
}