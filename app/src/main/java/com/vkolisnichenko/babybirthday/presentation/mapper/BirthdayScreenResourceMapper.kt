package com.vkolisnichenko.babybirthday.presentation.mapper

import androidx.annotation.DrawableRes
import com.vkolisnichenko.babybirthday.R
import com.vkolisnichenko.babybirthday.domain.model.BirthdayScreenVariant

@DrawableRes
fun getAgeImageResource(ageValue: Int): Int {
    return when (ageValue) {
        1 -> R.drawable.one
        2 -> R.drawable.two
        3 -> R.drawable.three
        4 -> R.drawable.four
        5 -> R.drawable.five
        6 -> R.drawable.six
        7 -> R.drawable.seven
        8 -> R.drawable.eight
        9 -> R.drawable.nine
        10 -> R.drawable.ten
        11 -> R.drawable.eleven
        12 -> R.drawable.twelve
        else -> R.drawable.one
    }
}

@DrawableRes
fun getBabyImageResource(variant: BirthdayScreenVariant): Int {
    return when (variant) {
        BirthdayScreenVariant.FOX -> R.drawable.ic_baby_fox
        BirthdayScreenVariant.ELEPHANT -> R.drawable.ic_baby_elephant
        BirthdayScreenVariant.PELICAN -> R.drawable.ic_baby_pelican
    }
}

@DrawableRes
fun getPhotoImageResource(variant: BirthdayScreenVariant): Int {
    return when (variant) {
        BirthdayScreenVariant.FOX -> R.drawable.ic_photo_fox
        BirthdayScreenVariant.ELEPHANT -> R.drawable.ic_photo_elephant
        BirthdayScreenVariant.PELICAN -> R.drawable.ic_photo_pelican
    }
}