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
    object Exoplayer : Page(route = "Exoplayer/{videoUrl}/{title}",describe = "视频播放器") {
        fun createRoute(videoUrl: String, title: String = ""): String {
            // URL编码，避免特殊字符导致导航问题
            val encodedUrl = java.net.URLEncoder.encode(videoUrl, "UTF-8")
            val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
            return "Exoplayer/$encodedUrl/$encodedTitle"
        }
    }
}