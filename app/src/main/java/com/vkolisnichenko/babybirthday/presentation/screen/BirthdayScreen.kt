package com.vkolisnichenko.babybirthday.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vkolisnichenko.babybirthday.presentation.theme.BabyBirthdayAppTheme

@Composable
fun BirthdayScreen(
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Birthday Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }

}

@Preview(showBackground = true)
@Composable
fun BirthdayScreenPreview() {
    BabyBirthdayAppTheme {
        BirthdayScreen()
    }
}