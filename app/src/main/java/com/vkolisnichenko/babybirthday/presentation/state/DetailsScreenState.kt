package com.vkolisnichenko.babybirthday.presentation.state

import java.time.LocalDate
import java.time.Period

data class DetailsScreenState(
    val name: String = "",
    val birthday: LocalDate? = null,
    val photoPath: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() && birthday != null && isBirthdayValid

    val hasPhoto: Boolean
        get() = photoPath != null

    val isBirthdayValid: Boolean
        get() = birthday?.let { date ->
            !date.isAfter(LocalDate.now()) && Period.between(date, LocalDate.now()).years < 18
        } ?: true

    val birthdayErrorMessage: String?
        get() = birthday?.let { date ->
            when {
                date.isAfter(LocalDate.now()) -> "Birthday cannot be in the future"
                Period.between(
                    date,
                    LocalDate.now()
                ).years >= 18 -> "Baby must be under 18 years old"

                else -> null
            }
        }
}