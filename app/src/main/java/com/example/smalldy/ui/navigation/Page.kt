package com.example.smalldy.ui.navigation

sealed class Page(
    val route: String,
    val describe: String
) {
    object Home : Page(route = "Home",describe = "主页面导航")
    object Friends : Page(route = "Friends",describe = "朋友")
    object Add : Page(route = "Add",describe = "拍摄、发布视频")
    object Msg : Page(route = "Msg",describe = "消息")
    object Mine : Page(route = "Mine",describe = "我的")
    object Exoplayer : Page(route = "Exoplayer",describe = "视频播放器")
}