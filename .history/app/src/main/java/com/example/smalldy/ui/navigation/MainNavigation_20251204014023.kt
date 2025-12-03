package com.example.smalldy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smalldy.ui.common.BottomNav

@Composable
fun MainNavigation(
    navHostController: NavHostController,
    windowSizeClass: WindowSizeClass,
    isInPictureInPictureMode: Boolean,

) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navigationLayoutType = windowSizeClass.calculateNavigationLayout(
        currentRoute = currentRoute,
        isInPictureInPictureMode = isInPictureInPictureMode,
    )
    
    // 将底部导航栏的 tab ID 映射到路由
    fun getRouteFromTabId(tabId: String): String {
        return when (tabId) {
            "home" -> Page.Home.route
            "friends" -> Page.Friends.route
            "add" -> Page.Add.route
            "messages" -> Page.Msg.route
            "profile" -> Page.Mine.route
            else -> Page.Home.route
        }
    }
    
    // 将路由映射到底部导航栏的 tab ID
    fun getTabIdFromRoute(route: String?): String {
        return when {
            route == Page.Home.route -> "home"
            route == Page.Friends.route -> "friends"
            route == Page.Add.route -> "add"
            route == Page.Msg.route -> "messages"
            route == Page.Mine.route -> "profile"
            else -> "home"
        }
    }

    Row {
        AnimatedVisibility(
            visible = (navigationLayoutType == NavigationLayoutType.NAVIGATION_RAIL),
            enter = slideInHorizontally (initialOffsetX = { -it }),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            Row {
                Text("NAVIGATION_RAIL")
            }
        }
        Scaffold (
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                AnimatedVisibility(
                    visible = (navigationLayoutType == NavigationLayoutType.BOTTOM_NAVIGATION),
                    enter = slideInVertically (initialOffsetY = {it}),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    BottomNav(
                        activeTab = getTabIdFromPageIndex(pagerState.currentPage),
                        onTabChange = { tabId ->
                            val targetPage = getPageIndexFromTabId(tabId)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(targetPage)
                            }
                        }
                    )
                }
            }
        ){paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                pageSpacing = 0.dp,
                contentPadding = PaddingValues(0.dp),
                verticalAlignment = Alignment.Top,
                userScrollEnabled = true,
                reverseLayout = false,
            ) { page ->
                // 根据页面索引显示对应的页面内容
                val currentPage = mainPages[page]
                when (currentPage) {
                    Page.Home -> Home()
                    Page.Friends -> FriendsPage()
                    Page.Add -> AddPage()
                    Page.Msg -> MsgPage()
                    Page.Mine -> MinePage()
                    else -> Home()
                }
            }
        }
    }
}


private enum class NavigationLayoutType {
    BOTTOM_NAVIGATION,
    NAVIGATION_RAIL,
    FULL_SCREEN,
}

private fun WindowSizeClass.calculateNavigationLayout(currentRoute: String?, isInPictureInPictureMode: Boolean): NavigationLayoutType {
    if (currentRoute?.startsWith(Page.Exoplayer.route) == true || isInPictureInPictureMode) {
        return NavigationLayoutType.FULL_SCREEN
    }
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            NavigationLayoutType.BOTTOM_NAVIGATION
        }
        else -> {
            // WindowWidthSizeClass.Medium, -- tablet portrait
            // WindowWidthSizeClass.Expanded, -- phone landscape mode
            NavigationLayoutType.NAVIGATION_RAIL
        }
    }
}