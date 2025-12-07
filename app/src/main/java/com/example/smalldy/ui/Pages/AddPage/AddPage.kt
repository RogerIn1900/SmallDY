package com.example.smalldy.ui.Pages.AddPage

import android.R.id.message
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.smalldy.data.VideoPlayerUIEvent
import com.example.smalldy.ui.video.VideoPlayerScreen
import com.example.smalldy.ui.video.VideoPlayerViewModel
import kotlin.contracts.contract

@Composable
fun AddPage() {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    val viewModel = remember { VideoPlayerViewModel(player) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 使用 raw 文件夹中的视频（res/raw/video.mp4）
    // 方式1：使用 raw 资源路径格式
    val rawVideoPath = "raw://video"  // 对应 res/raw/video.mp4（不含扩展名）
    // 方式2：直接使用 Android resource URI（需要先转换）
    // import com.example.smalldy.Utils.getRawResourceUri
    // val rawVideoUri = getRawResourceUri(context, "video").toString()

    VideoPlayerScreen(
        modifier = Modifier.fillMaxSize(),
        videoUrl = rawVideoPath,  // 或使用网络 URL，例如 "https://example.com/video.mp4"
        player = player,
        uiState = uiState,
        uiEvent = VideoPlayerUIEvent(
            // 使用支持 raw 资源的方法（自动检测并转换）
            onPlayVideo = { viewModel.playVideo(context, rawVideoPath) },
            // 或者直接使用 raw 资源名称
            // onPlayVideo = { viewModel.playRawVideo(context, "video") },
            // 或者使用网络 URL
            // onPlayVideo = { viewModel.playVideo("https://example.com/video.mp4") },
            onPause = { viewModel.pause() },
            onResume = { viewModel.resume() },
            onEnterPictureInPictureMode = {
                // PIP 逻辑
            },
            onSavePlaybackState = { viewModel.savePlaybackState() },
            onRestorePlaybackState = { viewModel.restorePlaybackState() },
            onToggleFullScreenMode = { viewModel.setFullScreenMode(it) },
            onErrorShown = { viewModel.errorShown(it) },
            onShowSnackbar = { message ->
                snackbarHostState.showSnackbar(message)
            }
        ),
        snackbarHostState = snackbarHostState
    )
}
