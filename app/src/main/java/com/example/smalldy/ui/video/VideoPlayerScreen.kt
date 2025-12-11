package com.example.smalldy.ui.video

import android.app.PictureInPictureParams
import android.graphics.Rect
import android.util.Rational
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt
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
import kotlin.math.max
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
import com.example.smalldy.data.Video
import com.example.smalldy.ui.common.VideoCardData
import kotlinx.coroutines.coroutineScope


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
    // 新增参数：视频数据和UI交互回调
    videoData: FullScreenVideoPlayerData? = null,
    onFollowClick: (() -> Unit)? = null,
    onLikeClick: ((Boolean) -> Unit)? = null, // 接收新的点赞状态
    onCommentClick: (() -> Unit)? = null,
    onFavoriteClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onExpandDescriptionClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null, // 返回按钮回调
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

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

/**
 * 自定义控制层：播放/暂停、进度条可拖动、前进后退
 */
@Composable
 fun ControlOverlay(
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    bufferedPositionMs: Long,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onForward: () -> Unit,
    onRewind: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // 底部进度控制条 + 前进/播放/后退（播放键居中）
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val clampedDuration = if (durationMs > 0) durationMs else 1L
            val positionFraction = (positionMs.toFloat() / clampedDuration.toFloat()).coerceIn(0f, 1f)
            val bufferFraction = (bufferedPositionMs.toFloat() / clampedDuration.toFloat()).coerceIn(0f, 1f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onRewind) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "后退10秒",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onPlayPauseToggle) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "暂停" else "播放",
                        tint = Color.White,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), shape = androidx.compose.foundation.shape.CircleShape)
                            .padding(4.dp)
                    )
                }
                IconButton(onClick = onForward) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "前进10秒",
                        tint = Color.White
                    )
                }
            }

            // 缓冲背景条（更细）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
                    .height(6.dp)
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    // 缓冲进度
                    drawRect(
                        color = Color.White.copy(alpha = 0.25f),
                        size = androidx.compose.ui.geometry.Size(width * bufferFraction, 3.dp.toPx()),
                        topLeft = Offset(0f, height / 2 - 1.5.dp.toPx())
                    )
                    // 已播放进度
                    drawRect(
                        color = Color.White,
                        size = androidx.compose.ui.geometry.Size(width * positionFraction, 3.dp.toPx()),
                        topLeft = Offset(0f, height / 2 - 1.5.dp.toPx())
                    )
                }
            }

            Slider(
                value = positionFraction,
                onValueChange = { fraction ->
                    val target = (fraction * clampedDuration).toLong()
                    onSeek(target)
                },
                valueRange = 0f..1f,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
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

    // 播放状态和进度
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var bufferedPosition by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    LaunchedEffect(player) {
        while (true) {
            currentPosition = player.currentPosition
            duration = max(player.duration, duration)
            bufferedPosition = player.bufferedPosition
            isPlaying = player.isPlaying
            delay(500)
        }
    }

    // 控制自定义 UI
    var controlsVisible by remember { mutableStateOf(true) } // 初始显示一次播放控制
    var hideControlsJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val toggleControls: () -> Unit = {
        controlsVisible = !controlsVisible
        hideControlsJob?.cancel()
        if (controlsVisible) {
            hideControlsJob = coroutineScope.launch {
                delay(3_000)
                controlsVisible = false
            }
        }
    }

    // 首次进入时自动隐藏控制层
    LaunchedEffect(Unit) {
        hideControlsJob?.cancel()
        hideControlsJob = coroutineScope.launch {
            delay(3_000)
            controlsVisible = false
        }
    }

    // 用于处理双击点赞和单点击显示控制栏
    val density = LocalDensity.current
    var boxSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    var lastTapTime by remember { mutableStateOf(0L) }
    var lastTapOffset by remember { mutableStateOf(Offset.Zero) }
    var tapJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var showDoubleTapAnimation by remember { mutableStateOf(false) }
    var animationOffset by remember { mutableStateOf(Offset.Zero) }
    
    // 控制栏区域高度（底部约150dp）
    val controllerBarHeight = with(density) { 150.dp.toPx() }
    // 悬浮组件区域（返回按钮、点赞按钮等）的边距
    val topButtonArea = with(density) { 80.dp.toPx() } // 顶部按钮区域
    val rightButtonArea = with(density) { 100.dp.toPx() } // 右侧按钮区域
    val leftInfoArea = with(density) { 300.dp.toPx() } // 左侧信息区域宽度
    val bottomInfoArea = with(density) { 200.dp.toPx() } // 底部信息区域高度
    // 双击判定：时间窗口和距离阈值
    val doubleTapTimeWindowMs = 400L
    val doubleTapDistanceThresholdPx = 50f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                boxSize = androidx.compose.ui.geometry.Size(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                )
            }
            .pointerInput(boxSize, controllerBarHeight, topButtonArea, rightButtonArea, leftInfoArea, bottomInfoArea) {
                detectTapGestures(
                    onTap = { offset ->
                        // 检查是否在控制栏区域（底部150dp）
                        val isInControllerArea = boxSize.height > 0 && 
                            offset.y > (boxSize.height - controllerBarHeight)
                        
                        // 检查是否在悬浮组件区域
                        // 左上角返回按钮区域（约 80x80dp）
                        val isInTopButtonArea = offset.y < topButtonArea && offset.x < topButtonArea
                        // 右侧互动按钮区域（右下角，宽度约100dp，高度约200dp）
                        val isInRightButtonArea = offset.x > (boxSize.width - rightButtonArea) && 
                            offset.y > (boxSize.height - bottomInfoArea - controllerBarHeight)
                        // 左侧信息区域（左下角，宽度约300dp，高度约200dp）
                        val isInLeftInfoArea = offset.x < leftInfoArea && 
                            offset.y > (boxSize.height - bottomInfoArea - controllerBarHeight)
                        
                        // 如果在控制栏区域或悬浮组件区域，不处理点击，让事件穿透
                        if (isInControllerArea || isInTopButtonArea || isInRightButtonArea || isInLeftInfoArea) {
                            return@detectTapGestures
                        }
                        
                        // 在视频区域处理点击
                        val currentTime = System.currentTimeMillis()
                        val timeSinceLastTap = currentTime - lastTapTime
                        val distance = sqrt(
                            (offset.x - lastTapOffset.x) * (offset.x - lastTapOffset.x) +
                            (offset.y - lastTapOffset.y) * (offset.y - lastTapOffset.y)
                        )
                        
                        // 如果两次点击间隔小于400ms且距离小于50px，认为是双击
                        if (timeSinceLastTap < doubleTapTimeWindowMs && distance < doubleTapDistanceThresholdPx) {
                            // 取消之前的单点击任务
                            tapJob?.cancel()
                            
                            // 这是双击，触发点赞（如果有 videoData）
                            videoData?.let { data ->
                                if (!data.videoData.isLiked) {
                                    animationOffset = offset
                                    showDoubleTapAnimation = true
                                    onLikeClick?.invoke(true)
                                    coroutineScope.launch {
                                        delay(1000)
                                        showDoubleTapAnimation = false
                                    }
                                }
                            }
                        } else {
                            // 可能是单点击，延迟执行以等待可能的第二次点击
                            tapJob?.cancel()
                            tapJob = coroutineScope.launch {
                                delay(400) // 等待400ms，如果没有第二次点击则执行单点击
                                toggleControls()
                            }
                        }
                        
                        lastTapTime = currentTime
                        lastTapOffset = offset
                    }
                )
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    // 禁用原生控制栏，改用自定义 UI
                    controllerAutoShow = false
                    useController = false
                    // 避免切换/prepare 时闪黑屏：保留上一帧，设置黑色背景
                    setKeepContentOnPlayerReset(true)
                    setShutterBackgroundColor(android.graphics.Color.BLACK)

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
                // 根据全屏状态更新控制栏行为
                playerView.controllerHideOnTouch = !uiState.isFullScreenMode
                
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

        // 返回按钮（左上角）
        if (onBackClick != null) {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White
                )
            }
        }

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

        // 全屏视频播放器UI覆盖层（如果提供了视频数据）
        // 如果提供了 videoData，说明是流式播放（类似抖音），应该始终允许单点击显示控制栏
        videoData?.let { data ->
            // 本地可变状态，点击右侧按钮时即时更新数值
            var likeCount by remember(data.videoData.id) { mutableStateOf(data.videoData.likeCount) }
            var isLiked by remember(data.videoData.id) { mutableStateOf(data.videoData.isLiked) }
            var commentCount by remember(data.videoData.id) { mutableStateOf(data.commentCount) }
            var favoriteCount by remember(data.videoData.id) { mutableStateOf(data.favoriteCount) }
            var shareCount by remember(data.videoData.id) { mutableStateOf(data.shareCount) }
            var isFavorited by remember(data.videoData.id) { mutableStateOf(data.isFavorited) }

            val uiData = data.copy(
                commentCount = commentCount,
                favoriteCount = favoriteCount,
                shareCount = shareCount,
                isFavorited = isFavorited,
                videoData = data.videoData.copy(
                    likeCount = likeCount,
                    isLiked = isLiked
                )
            )

            FullScreenVideoPlayerUI(
                data = uiData,
                modifier = Modifier.fillMaxSize(),
                onFollowClick = onFollowClick ?: {},
                onLikeClick = { newLiked ->
                    isLiked = newLiked
                    likeCount = (likeCount + if (newLiked) 1 else -1).coerceAtLeast(0)
                    onLikeClick?.invoke(newLiked)
                },
                onCommentClick = {
                    commentCount += 1
                    onCommentClick?.invoke()
                },
                onFavoriteClick = {
                    val newFav = !isFavorited
                    isFavorited = newFav
                    favoriteCount = (favoriteCount + if (newFav) 1 else -1).coerceAtLeast(0)
                    onFavoriteClick?.invoke()
                },
                onShareClick = {
                    shareCount += 1
                    onShareClick?.invoke()
                },
                onExpandDescriptionClick = onExpandDescriptionClick ?: {},
                onSingleTap = toggleControls // 在流式播放中显示自定义控制栏
            )
        }

        // 双击点赞动画（如果有 videoData）
        videoData?.let { data ->
            if (showDoubleTapAnimation) {
                com.example.smalldy.ui.video.DoubleTapLikeAnimation(
                    offset = animationOffset,
                    modifier = Modifier.align(Alignment.Center)
            )
            }
        }

        // 自定义控制层
        if (controlsVisible) {
            ControlOverlay(
                isPlaying = isPlaying,
                positionMs = currentPosition,
                durationMs = duration,
                bufferedPositionMs = bufferedPosition,
                onPlayPauseToggle = {
                    if (player.isPlaying) {
                        player.pause()
                    } else {
                        player.playWhenReady = true
                        player.play()
                    }
                    controlsVisible = true
                },
                onSeek = { targetMs ->
                    player.seekTo(targetMs.coerceIn(0L, max(0L, duration)))
                    controlsVisible = true
                },
                onForward = {
                    val target = player.currentPosition + 10_000
                    player.seekTo(target.coerceAtMost(duration))
                },
                onRewind = {
                    val target = player.currentPosition - 10_000
                    player.seekTo(target.coerceAtLeast(0L))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
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