package com.vkolisnichenko.babybirthday.domain.model

import java.time.LocalDate

data class BabyInfo(
    val name: String,
    val birthday: LocalDate,
    val photoPath: String?
) {
    companion object {
        fun empty() = BabyInfo(
            name = "",
            birthday = LocalDate.now(),
            photoPath = null
        )
    }
}