package com.example.smalldy.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.smalldy.ui.add.AddScreen
import com.example.smalldy.ui.friends.FriendsScreen
import com.example.smalldy.ui.home.HomeScreen
import com.example.smalldy.ui.messages.MessagesScreen
import com.example.smalldy.ui.profile.ProfileScreen

fun NavGraphBuilder.navMap(navController: NavController) {
    composable(Page.Home.route) { HomeScreen() }
    composable(Page.Friends.route) { FriendsScreen() }
    composable(Page.Add.route) { AddScreen() }
    composable(Page.Msg.route) { MessagesScreen() }
    composable(Page.Mine.route) { ProfileScreen() }
}
