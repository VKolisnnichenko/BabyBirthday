package com.vkolisnichenko.babybirthday.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vkolisnichenko.babybirthday.domain.model.BirthdayScreenVariant
import com.vkolisnichenko.babybirthday.presentation.state.BirthdayScreenState
import com.vkolisnichenko.babybirthday.presentation.theme.BabyBirthdayAppTheme
import com.vkolisnichenko.babybirthday.presentation.theme.getConfig
import com.vkolisnichenko.babybirthday.presentation.viewmodel.BirthdayScreenViewModel

@Composable
fun BirthdayScreen(
    modifier: Modifier = Modifier,
    viewModel: BirthdayScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BirthdayScreenContent(
        state = state,
        modifier = modifier
    )
}

@Composable
private fun BirthdayScreenContent(
    state: BirthdayScreenState,
    modifier: Modifier = Modifier
) {
    BirthdayBackground(
        variant = state.variant,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Birthday Screen - ${state.variant.name}",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun BirthdayBackground(
    variant: BirthdayScreenVariant,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val config = variant.getConfig()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(config.backgroundColor)
    ) {
        config.decorationResource?.let { decorationRes ->
            Image(
                painter = painterResource(id = decorationRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        content()
    }
}

@Preview(showBackground = true, name = "Fox Variant")
@Composable
fun BirthdayScreenFoxPreview() {
    BabyBirthdayAppTheme {
        BirthdayScreenContent(
            state = BirthdayScreenState(variant = BirthdayScreenVariant.FOX)
        )
    }
}

@Preview(showBackground = true, name = "Elephant Variant")
@Composable
fun BirthdayScreenElephantPreview() {
    BabyBirthdayAppTheme {
        BirthdayScreenContent(
            state = BirthdayScreenState(variant = BirthdayScreenVariant.ELEPHANT)
        )
    }
}

@Preview(showBackground = true, name = "Pelican Variant")
@Composable
fun BirthdayScreenPelicanPreview() {
    BabyBirthdayAppTheme {
        BirthdayScreenContent(
            state = BirthdayScreenState(variant = BirthdayScreenVariant.PELICAN)
        )
    }
}