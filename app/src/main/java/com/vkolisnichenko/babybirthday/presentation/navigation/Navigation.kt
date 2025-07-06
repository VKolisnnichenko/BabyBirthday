package com.vkolisnichenko.babybirthday.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vkolisnichenko.babybirthday.presentation.screen.BirthdayScreen
import com.vkolisnichenko.babybirthday.presentation.screen.DetailsScreen
import com.vkolisnichenko.babybirthday.presentation.utils.popBackStackOrIgnore

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
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 0))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 0))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
        }
    ) {
        composable(
            route = Destinations.DETAILS_ROUTE,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = 50))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            }
        ) {
            DetailsScreen(
                onShowBirthdayScreen = {
                    navController.navigate(Destinations.BIRTHDAY_ROUTE)
                }
            )
        }

        composable(
            route = Destinations.BIRTHDAY_ROUTE,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = 100))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300, delayMillis = 0))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 400, delayMillis = 0))
            }
        ) {
            BirthdayScreen(
                onCloseClick = {
                    navController.popBackStackOrIgnore()
                },
                onCameraClick = {
                    // TODO: Implement camera functionality in Step 4
                }
            )
        }
    }
}