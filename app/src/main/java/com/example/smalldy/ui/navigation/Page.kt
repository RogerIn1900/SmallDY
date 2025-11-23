package com.example.smalldy.ui.navigation

sealed class Page(
    val route: String,
    val destination: String
) {
    object Home : Page(route = "Home",destination = "主页面导航")
}