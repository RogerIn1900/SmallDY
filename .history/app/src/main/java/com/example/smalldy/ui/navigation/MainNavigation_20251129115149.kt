package com.example.smalldy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.smalldy.ui.Pages.HomePage.Home

@Composable
fun MainNavigation(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "Home"){
        navMap(navHostController)
    }
}


