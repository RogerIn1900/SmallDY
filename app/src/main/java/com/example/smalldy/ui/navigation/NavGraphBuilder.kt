package com.example.smalldy.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.smalldy.ui.Pages.AddPage.AddPage
import com.example.smalldy.ui.Pages.FriendsPage.FriendsPage
import com.example.smalldy.ui.Pages.HomePage.Home
import com.example.smalldy.ui.Pages.MinePage.MinePage
import com.example.smalldy.ui.Pages.MsgPage.MsgPage

fun NavGraphBuilder.navMap(navController: NavController){
    composable(Page.Home.route) { Home() }
    composable(Page.Friends.route) { FriendsPage() }
    composable(Page.Add.route) { AddPage() }
    composable(Page.Msg.route) { MsgPage() }
    composable(Page.Mine.route) { MinePage() }
}