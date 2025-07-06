package com.vkolisnichenko.babybirthday.presentation.theme

import androidx.compose.ui.graphics.Color
import com.vkolisnichenko.babybirthday.R
import com.vkolisnichenko.babybirthday.domain.model.BirthdayScreenVariant

data class BirthdayVariantConfig(
    val backgroundColor: Color,
    val decorationResource: Int? = null
)

fun BirthdayScreenVariant.getConfig(): BirthdayVariantConfig {
    return when (this) {
        BirthdayScreenVariant.FOX -> BirthdayVariantConfig(
            backgroundColor = FoxBackground,
            decorationResource = R.drawable.bg_fox
        )

        BirthdayScreenVariant.ELEPHANT -> BirthdayVariantConfig(
            backgroundColor = ElephantBackground,
            decorationResource = R.drawable.bg_elephant
        )

        BirthdayScreenVariant.PELICAN -> BirthdayVariantConfig(
            backgroundColor = PelicanBackground,
            decorationResource = R.drawable.bg_pelican
        )
    }
}