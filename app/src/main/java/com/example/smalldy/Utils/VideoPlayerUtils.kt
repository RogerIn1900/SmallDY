package com.example.smalldy.Utils


import android.app.PictureInPictureParams
import android.util.Rational
import android.view.View
import androidx.activity.compose.LocalActivity
import android.content.Context
import android.net.Uri

import android.app.Activity
import android.os.Build
import android.view.WindowInsetsController
import android.view.WindowInsets

@Suppress("DEPRECATION")
internal fun Activity.enterFullScreenMode(){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        window.insetsController?.hide(
            (WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()).toInt()
        )
        window.insetsController?.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }else{
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }
}

fun Activity.exitFullScreenMode() {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        window.insetsController?.show(
            WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
        )
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

fun Activity.enterPIPMode(aspactRatio: Rational) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            // 检查设备是否支持画中画
            if (packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(aspactRatio)
                    .build()
                enterPictureInPictureMode(params)
            }
        } catch (e: IllegalStateException) {
            // Activity 不支持画中画，忽略错误
            android.util.Log.w("VideoPlayerUtils", "Picture-in-Picture not supported: ${e.message}")
        }
    }
}

/**
 * 将 raw 资源名称转换为 Android resource URI
 * 
 * @param context Context 对象
 * @param rawResourceName raw 文件夹中的资源名称（不含扩展名），例如 "video" 对应 "video.mp4"
 * @return Android resource URI，例如 "android.resource://com.example.smalldy/raw/video"
 * 
 * 使用示例：
 * val videoUri = getRawResourceUri(context, "video")  // 对应 res/raw/video.mp4
 * viewModel.playVideo(videoUri.toString())
 */
fun getRawResourceUri(context: Context, rawResourceName: String): Uri {
    val packageName = context.packageName
    val resourceId = context.resources.getIdentifier(
        rawResourceName,
        "raw",
        packageName
    )
    
    if (resourceId == 0) {
        throw IllegalArgumentException("Raw resource '$rawResourceName' not found")
    }
    
    return Uri.parse("android.resource://$packageName/$resourceId")
}

/**
 * 检查字符串是否是 raw 资源路径格式
 * 支持的格式：
 * - "raw://video" 或 "raw://video.mp4" -> 转换为 Android resource URI
 * - "raw/video" 或 "raw/video.mp4" -> 转换为 Android resource URI
 * 
 * @param path 要检查的路径
 * @return 如果是 raw 资源路径格式，返回 true
 */
fun isRawResourcePath(path: String): Boolean {
    return path.startsWith("raw://", ignoreCase = true) ||
           path.startsWith("raw/", ignoreCase = true)
}

/**
 * 从 raw 资源路径中提取资源名称（去除扩展名）
 * 
 * @param path raw 资源路径，例如 "raw://video.mp4" 或 "raw/video"
 * @return 资源名称（不含扩展名），例如 "video"
 */
fun extractRawResourceName(path: String): String {
    val name = when {
        path.startsWith("raw://", ignoreCase = true) -> path.substring(6)
        path.startsWith("raw/", ignoreCase = true) -> path.substring(4)
        else -> path
    }
    // 去除扩展名
    return name.substringBeforeLast(".")
}