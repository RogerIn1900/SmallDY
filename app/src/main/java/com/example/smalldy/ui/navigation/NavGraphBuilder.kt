package com.example.smalldy.ui.navigation

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.smalldy.ui.Pages.AddPage.AddPage
import com.example.smalldy.ui.Pages.FriendsPage.FriendsPage
import com.example.smalldy.ui.Pages.HomePage.Home
import com.example.smalldy.ui.Pages.MinePage.MinePage
import com.example.smalldy.ui.Pages.MsgPage.MsgPage
import com.example.smalldy.ui.Pages.VideoPlayerPage.VideoPlayerFeedPage

@OptIn(UnstableApi::class)
fun NavGraphBuilder.navMap(navController: NavController){
    composable(Page.Home.route) { 
        Home(navController = navController)
    }
    composable(Page.Friends.route) { FriendsPage() }
    composable(Page.Add.route) { AddPage() }
    composable(Page.Msg.route) { MsgPage() }
    composable(Page.Mine.route) { MinePage() }
    
    // 视频播放器路由，支持传递视频索引
    composable(
        route = Page.Exoplayer.route,
        arguments = listOf(
            navArgument("videoIndex") { 
                type = androidx.navigation.NavType.StringType
                defaultValue = "0"
            }
        )
    ) { backStackEntry ->
        VideoPlayerFeedPage(
            navBackStackEntry = backStackEntry
        )
    }
}