package com.vkolisnichenko.babybirthday.domain.usecase

import com.vkolisnichenko.babybirthday.domain.model.AgeInfo
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class CalculateAgeUseCase @Inject constructor() {

    operator fun invoke(birthday: LocalDate): AgeInfo {
        val today = LocalDate.now()
        val period = Period.between(birthday, today)

        val totalMonths = period.years * 12 + period.months

        return if (totalMonths < 12) {
            AgeInfo(value = totalMonths, isYears = false)
        } else {
            AgeInfo(value = period.years, isYears = true)
        }
    }
}