package com.vkolisnichenko.babybirthday.presentation.utils

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

fun NavController.popBackStackOrIgnore() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}