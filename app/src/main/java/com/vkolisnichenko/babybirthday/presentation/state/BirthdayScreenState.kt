package com.vkolisnichenko.babybirthday.presentation.state

import com.vkolisnichenko.babybirthday.domain.model.AgeInfo
import com.vkolisnichenko.babybirthday.domain.model.BirthdayScreenVariant

data class BirthdayScreenState(
    val babyName: String = "",
    val ageInfo: AgeInfo = AgeInfo(0, false),
    val variant: BirthdayScreenVariant = BirthdayScreenVariant.FOX,
    val photoPath: String = ""
)