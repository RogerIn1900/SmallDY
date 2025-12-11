package com.example.smalldy.ui.Pages.HomePage

import android.R
import android.R.attr.divider
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.ui.graphics.TransformOrigin
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.example.smalldy.data.database.DatabaseInitializer
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smalldy.ui.common.BottomNav
import com.example.smalldy.ui.common.FeedCard
import com.example.smalldy.ui.common.FeedCardData
import com.example.smalldy.ui.common.NavItem
import com.example.smalldy.ui.common.TopNav
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smalldy.data.Comment
import com.example.smalldy.data.toVideo
import com.example.smalldy.ui.comment.CommentScreen
import com.example.smalldy.ui.common.VideoList
import com.example.smalldy.ui.common.generateDefaultVideoCardData
import com.example.smalldy.ui.common.VideoThumbnailImage
import com.example.smalldy.ui.navigation.Page
import com.example.smalldy.SmallDYApplication
import com.example.smalldy.data.database.VideoRepository
import com.example.smalldy.data.toVideoCardData
import com.example.smalldy.ui.video.VideoInteractionViewModel
import com.example.smalldy.ui.video.VideoOperations
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.derivedStateOf


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Home(navController: NavController? = null) {
    val context = LocalContext.current
    val application = context.applicationContext as SmallDYApplication
    val repository = application.repository
    val interactionViewModel: VideoInteractionViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    // 从数据库获取视频数据
    val videosWithInteractions by repository.getAllVideosWithInteractions().collectAsStateWithLifecycle(initialValue = emptyList())
    
    // 添加日志调试
    Log.d("HomePage", "Videos count: ${videosWithInteractions.size}")
    if (videosWithInteractions.isNotEmpty()) {
        Log.d("HomePage", "First video: ${videosWithInteractions.first().first.title}")
        Log.d("HomePage", "First video URL: ${videosWithInteractions.first().first.url}")
    } else {
        Log.w("HomePage", "No videos found in database!")
    }
    
    // 尝试手动触发数据库初始化（如果数据库为空或视频数量不足）
    LaunchedEffect(Unit) {
        if (videosWithInteractions.isEmpty()) {
            try {
                val allVideos = repository.getAllVideos().first()
                Log.d("HomePage", "Direct query: ${allVideos.size} videos")
                if (allVideos.isEmpty() || allVideos.size < 18) {
                    Log.w("HomePage", "Database has ${allVideos.size} videos (expected 18), re-initializing...")
                    DatabaseInitializer.initialize(context, repository, forceRefresh = true)
                }
            } catch (e: Exception) {
                Log.e("HomePage", "Error checking database", e)
            }
        }
    }
    
    // 直接根据当前数据构建 UI 列表，确保图片与文本一一对应
    val videoCardData = videosWithInteractions.map { (video, interaction) ->
        video.toVideoCardData().copy(isLiked = interaction.isLiked)
    }
    Log.d("HomePage", "VideoCardData count: ${videoCardData.size}")
    
    // 保持列表状态，确保滚动位置和布局稳定
    // 使用 rememberSaveable + LazyStaggeredGridState.Saver 保持瀑布流滚动位置，返回时不重置
    val listState = rememberSaveable(
        saver = LazyStaggeredGridState.Saver
    ) {
        androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState()
    }
    
    var showCommentScreen by remember { mutableStateOf(false) }
    var selectedVideo by remember { mutableStateOf<com.example.smalldy.ui.common.VideoCardData?>(null) }
    var expandingVideo by remember { mutableStateOf<com.example.smalldy.ui.common.VideoCardData?>(null) }
    var pendingNavigateIndex by remember { mutableStateOf<Int?>(null) }

    // 预加载标志，用于提前准备播放器数据
    var preloadingRoute by remember { mutableStateOf<String?>(null) }
    
    // 10个tabs：第一个是"视频"，其他显示简易信息
    val tabs = remember {
        listOf(
            "视频",
            "推荐",
            "关注",
            "直播",
            "游戏",
            "美食",
            "旅行",
            "音乐",
            "搞笑",
            "科技"
        )
    }
    
    // PagerState用于HorizontalPager
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    
    // 当前选中的tab索引
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    
    // 当Tab点击时，同步Pager
    val onTabClick: (Int) -> Unit = { index ->
        coroutineScope.launch {
            pagerState.animateScrollToPage(index)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // ScrollableTabRow - 固定在顶部，只应用状态栏的顶部padding
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(top = paddingValues.calculateTopPadding()
                        ,
                containerColor = Color.Black,
                contentColor = Color.Gray,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier,
                        color = Color.Transparent // 隐藏指示器
                    )
                },
                divider = {
                    Divider(
                        color = Color(0xFF333333) // 深灰色分隔线
                    )
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = { onTabClick(index) },
                        text = {
                            Text(
                                text = tab,
                                fontSize = 14.sp,
                                color = if (selectedTabIndex.value == index) {
                                    Color.White // 选中时白色
                                } else {
                                    Color.Gray // 未选中时灰色
                                }
                            )
                        }
                    )
                }
            }
            
            // HorizontalPager - 占据剩余空间，应用底部padding
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) { page ->
                when (page) {
                    0 -> {
                        // "视频" tab - 双列瀑布流
    AnimatedContent(
        targetState = expandingVideo,
        transitionSpec = {
                                // 延长动画时间，使其与播放器加载时间同步
                                fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.92f,
                                            animationSpec = tween(600, easing = FastOutSlowInEasing),
                        transformOrigin = TransformOrigin.Center
                    ) with
                                        fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                    scaleOut(
                        targetScale = 1.02f,
                                            animationSpec = tween(300, easing = FastOutSlowInEasing),
                        transformOrigin = TransformOrigin.Center
                    )
        },
        label = "CardSharedTransition"
    ) { animatedVideo ->
        if (animatedVideo == null) {
            if (videoCardData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else {
                VideoList(
                    videos = videoCardData,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5)),
                    listState = listState,
                    onVideoClick = { video ->
                        val videoIndex = videoCardData.indexOfFirst { it.id == video.id }
                        if (videoIndex >= 0) {
                                                // 立即开始并行准备：设置动画状态和预加载数据
                            expandingVideo = video
                            pendingNavigateIndex = videoIndex
                                                
                                                // 并行执行：提前准备路由和预加载数据
                                                val route = Page.Exoplayer.createRoute(videoIndex)
                                                preloadingRoute = route
                                                
                                                // 在后台协程中提前准备播放器数据（不阻塞UI）
                                                coroutineScope.launch {
                                                    // 预加载：提前获取视频数据，让播放器页面初始化更快
                                                    // 这里可以提前做一些准备工作，比如预加载视频信息
                                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                                        // 可以在这里预加载一些数据，比如视频元信息
                                                        // repository.getVideoById(video.id) // 如果需要的话
                                                    }
                                                }
                        }
                    },
                    onLikeClick = { videoId, liked ->
                        coroutineScope.launch { repository.updateLikeStatus(videoId, liked) }
                    },
                    onCommentClick = { video ->
                        selectedVideo = video
                        showCommentScreen = true
                    },
                    onShareClick = { video ->
                        coroutineScope.launch { repository.updateShareStatus(video.id, true) }
                    }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .aspectRatio(9f / 16f)
                ) {
                    VideoThumbnailImage(
                        videoUrl = animatedVideo.videoUrl ?: animatedVideo.coverImage,
                        coverImage = animatedVideo.coverImage,
                        contentDescription = animatedVideo.title,
                        videoId = animatedVideo.id,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                                }
                            }
                        }
                    }
                    else -> {
                        // 其他tab - 显示对应的简易信息
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabs[page],
                                fontSize = 32.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 动画和导航并行执行：动画进行时，导航和播放器初始化也在后台准备
    // 动画时间延长到约1000ms，与播放器加载时间同步
    LaunchedEffect(expandingVideo, preloadingRoute) {
        val targetIndex = pendingNavigateIndex
        val route = preloadingRoute
        
        if (expandingVideo != null && targetIndex != null && route != null && navController != null) {
            // 并行执行策略：
            // 1. 立即导航，让播放器开始加载（不等待动画）
            // 2. 动画时间：fadeIn 400ms + scaleIn 600ms = 1000ms
            // 3. 播放器加载约800-1000ms，与动画时间匹配
            // 4. 当播放器准备好时，动画刚好结束并淡出
            
            // 立即导航，让播放器开始加载（不等待动画）
            navController.navigate(route)
            
            // 等待动画和播放器加载完成
            // fadeIn 400ms + scaleIn 600ms = 1000ms（最长动画时间）
            // 播放器加载通常需要800-1000ms，所以等待1000ms让它们同步
            kotlinx.coroutines.delay(1000) // 等待动画和播放器加载完成
            
            // 动画结束，触发淡出效果（fadeOut 300ms + scaleOut 300ms）
            expandingVideo = null
            pendingNavigateIndex = null
            preloadingRoute = null
        }
    }
    
    // 评论页面
    selectedVideo?.let { video ->
        if (showCommentScreen) {
            CommentScreen(
                video = video.toVideo(),
                interactionViewModel = interactionViewModel,
                onBack = {
                    showCommentScreen = false
                    selectedVideo = null
                }
            )
        }
    }
}


//@Composable
//fun Home2(navController: NavController? = null) {
//    val context = LocalContext.current
//    val application = context.applicationContext as SmallDYApplication
//    val repository = application.repository
//    val coroutineScope = rememberCoroutineScope()
//
//    // 从数据库获取视频数据
//    val videosWithInteractions by repository.getAllVideosWithInteractions().collectAsStateWithLifecycle(initialValue = emptyList())
//
//    // 直接根据当前数据构建 UI 列表
//    val videoCardData = videosWithInteractions.map { (video, interaction) ->
//        video.toVideoCardData().copy(isLiked = interaction.isLiked)
//    }
//
//    // 10个tabs：第一个是"视频"，其他是数字1-9
//    val tabs = remember {
//        listOf("视频", "1", "2", "3", "4", "5", "6", "7", "8", "9")
//    }
//
//    // PagerState用于HorizontalPager
//    val pagerState = rememberPagerState(pageCount = { tabs.size })
//
//    // 当前选中的tab索引
//    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
//
//    // 同步Pager和Tab的选中状态
//    LaunchedEffect(pagerState.currentPage) {
//        // Pager页面变化时，Tab会自动更新（通过derivedStateOf）
//    }
//
//    // 当Tab点击时，同步Pager
//    val onTabClick: (Int) -> Unit = { index ->
//        coroutineScope.launch {
//            pagerState.animateScrollToPage(index)
//        }
//    }
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        containerColor = Color(0xFFF5F5F5)
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(Color(0xFFF5F5F5))
//        ) {
//            // ScrollableTabRow - 固定在顶部
//            ScrollableTabRow(
//                selectedTabIndex = selectedTabIndex.value,
//                modifier = Modifier.fillMaxWidth(),
//                containerColor = Color.White,
//                contentColor = Color.Black,
//                indicator = { tabPositions ->
//                    TabRowDefaults.Indicator(
//                        modifier = Modifier,
//                        color = Color(0xFF007AFF)
//                    )
//                },
//                divider = {
//                    Divider(
//                        color = Color(0xFFE0E0E0)
//                    )
//                }
//            ) {
//                tabs.forEachIndexed { index, tab ->
//                    Tab(
//                        selected = selectedTabIndex.value == index,
//                        onClick = { onTabClick(index) },
//                        text = {
//                            Text(
//                                text = tab,
//                                fontSize = 14.sp
//                            )
//                        }
//                    )
//                }
//            }
//
//            // HorizontalPager - 占据剩余空间
//            HorizontalPager(
//                state = pagerState,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//            ) { page ->
//                when (page) {
//                    0 -> {
//                        // "视频" tab - 双列瀑布流
//                        VideoList(
//                            videos = videoCardData,
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(Color(0xFFF5F5F5)),
//                            onVideoClick = { video ->
//                                val videoIndex = videoCardData.indexOfFirst { it.id == video.id }
//                                if (videoIndex >= 0) {
//                                    navController?.navigate(Page.Exoplayer.createRoute(videoIndex))
//                                }
//                            },
//                            onLikeClick = { videoId, liked ->
//                                coroutineScope.launch {
//                                    repository.updateLikeStatus(videoId, liked)
//                                }
//                            },
//                            onCommentClick = { video ->
//                                // 处理评论点击
//                            },
//                            onShareClick = { video ->
//                                coroutineScope.launch {
//                                    repository.updateShareStatus(video.id, true)
//                                }
//                            }
//                        )
//                    }
//                    else -> {
//                        // 其他tab - 显示数字
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(Color(0xFFF5F5F5)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = tabs[page],
//                                fontSize = 48.sp,
//                                color = Color.Gray
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

// 示例导航及共享元素 demo 已移除，避免影响主功能编译
