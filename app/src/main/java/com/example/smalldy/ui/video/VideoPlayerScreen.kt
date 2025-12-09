package com.example.smalldy.ui.video

import android.app.PictureInPictureParams
import android.graphics.Rect
import android.util.Rational
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.smalldy.Utils.enterFullScreenMode
import com.example.smalldy.Utils.enterPIPMode
import com.example.smalldy.Utils.exitFullScreenMode
import com.example.smalldy.data.VideoPlayerUIEvent
import com.example.smalldy.data.VideoPlayerUIState


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    videoUrl: String,
    player: Player,
    uiState: VideoPlayerUIState,
    uiEvent: VideoPlayerUIEvent,
    shouldShowPiPButton: Boolean = false,
    snackbarHostState: SnackbarHostState? = null,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // 监听生命周期
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 处理错误消息
    if (uiState.errorMessages.isNotEmpty()) {
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }
        LaunchedEffect(errorMessage.id) {
            uiEvent.onShowSnackbar(errorMessage.message)
            uiEvent.onErrorShown(errorMessage.id)
        }
    }

    // 全屏模式控制
    DisposableEffect(Unit) {
        onDispose {
            activity?.exitFullScreenMode()
        }
    }

    SideEffect {
        activity?.let {
            if (uiState.isFullScreenMode) {
                it.enterFullScreenMode()
            } else {
                it.exitFullScreenMode()
            }
        }
    }

    // 生命周期管理播放状态
    DisposableEffect(lifecycle) {
        when (lifecycle) {
            Lifecycle.Event.ON_PAUSE -> {
                // 在 Android 7.0+ 中，应该在 onStop 时暂停
            }
            Lifecycle.Event.ON_RESUME -> {
                uiEvent.onRestorePlaybackState()
            }
            Lifecycle.Event.ON_STOP -> {
                uiEvent.onSavePlaybackState()
            }
            else -> {}
        }
        onDispose { }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    // 自动播放下一条时不默认展示控制栏，用户触摸后再显示
                    controllerAutoShow = false
                    useController = true
                    // 保持快速隐藏策略，展示后 2 秒自动隐藏
                    controllerShowTimeoutMs = 2_000
                    controllerHideOnTouch = true
                    // 避免切换/prepare 时闪黑屏：保留上一帧，关闭快门背景
                    setKeepContentOnPlayerReset(true)
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)

                    // 全屏按钮监听
                    setFullscreenButtonClickListener { isFullScreen ->
                        uiEvent.onToggleFullScreenMode(isFullScreen)
                    }

                    // 更新 PIP 的 sourceRectHint（用于平滑过渡）
                    // 只在 Android 8.0+ 且 Activity 支持 PIP 时更新
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                            val sourceRectHint = Rect()
                            getGlobalVisibleRect(sourceRectHint)
                            try {
                                val builder = PictureInPictureParams.Builder()
                                    .setSourceRectHint(sourceRectHint)
                                activity?.setPictureInPictureParams(builder.build())
                            } catch (e: IllegalStateException) {
                                // Activity 不支持 PIP，忽略错误
                            }
                        }
                    }

                    this.player = player
                }
            },
            update = { playerView ->
                // 根据生命周期更新播放器状态
                when (lifecycle) {
                    Lifecycle.Event.ON_PAUSE -> {
                        playerView.hideController()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        playerView.onResume()
                    }
                    Lifecycle.Event.ON_STOP -> {
                        playerView.onPause()
                    }
                    else -> {}
                }
            }
        )

        // PIP 按钮（可选）
        if (shouldShowPiPButton && uiState.videoWidth > 0 && uiState.videoHeight > 0) {
            PiPButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                onClick = {
                    val aspectRatio = Rational(uiState.videoWidth, uiState.videoHeight)
                    activity?.enterPIPMode(aspectRatio)
                    uiEvent.onEnterPictureInPictureMode()
                }
            )
        }

        // Snackbar（可选）
        snackbarHostState?.let {
            SnackbarHost(
                hostState = it,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // 自动播放视频
    LaunchedEffect(videoUrl) {
        if (!uiState.hasVideoLoaded) {
            uiEvent.onPlayVideo()
        }
    }
}

// 简单的 PIP 按钮组件
@Composable
fun PiPButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // 实现你的 PIP 按钮 UI
    // 可以使用 IconButton + Icon
}