package com.vkolisnichenko.babybirthday.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vkolisnichenko.babybirthday.presentation.screen.BirthdayScreen
import com.vkolisnichenko.babybirthday.presentation.screen.DetailsScreen

object Destinations {
    const val DETAILS_ROUTE = "details"
    const val BIRTHDAY_ROUTE = "birthday"
}

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.DETAILS_ROUTE,
        modifier = modifier
    ) {
        composable(Destinations.DETAILS_ROUTE) {
            DetailsScreen(
                onShowBirthdayScreen = {
                    navController.navigate(Destinations.BIRTHDAY_ROUTE)
                }
            )
        }

        composable(Destinations.BIRTHDAY_ROUTE) {
            BirthdayScreen()
        }
    }
}