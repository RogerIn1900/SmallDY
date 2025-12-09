package com.example.smalldy.ui.Pages.HomePage

import android.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.smalldy.data.database.DatabaseInitializer
import kotlinx.coroutines.flow.first
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
import com.example.smalldy.ui.navigation.Page
import com.example.smalldy.SmallDYApplication
import com.example.smalldy.data.database.VideoRepository
import com.example.smalldy.data.toVideoCardData
import com.example.smalldy.ui.video.VideoInteractionViewModel
import com.example.smalldy.ui.video.VideoOperations


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
    
    // 转换为 VideoCardData - 使用稳定的 key 确保列表项不会重新创建
    val videoCardData = remember(videosWithInteractions.map { it.first.id }) {
        val cardData = videosWithInteractions.map { (video, interaction) ->
            video.toVideoCardData().copy(
                isLiked = interaction.isLiked
            )
        }
        Log.d("HomePage", "VideoCardData count: ${cardData.size}")
        cardData
    }
    
    // 保持列表状态，确保滚动位置和布局稳定
    val listState = androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState()
    
    var showCommentScreen by remember { mutableStateOf(false) }
    var selectedVideo by remember { mutableStateOf<com.example.smalldy.ui.common.VideoCardData?>(null) }

    // 显示加载状态或视频列表
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
            // 导航到视频播放页面，传递当前视频在列表中的索引
            navController?.let { nav ->
                val videoIndex = videoCardData.indexOfFirst { it.id == video.id }
                if (videoIndex >= 0) {
                    val route = Page.Exoplayer.createRoute(videoIndex)
                    nav.navigate(route)
                }
            }
        },
        onLikeClick = { videoId, liked ->
            // 处理点赞 - 使用数据库
            coroutineScope.launch {
                repository.updateLikeStatus(videoId, liked)
            }
        },
        onCommentClick = { video ->
            // 打开评论页面
            selectedVideo = video
            showCommentScreen = true
        },
        onShareClick = { video ->
            // 处理转发 - 使用数据库
            coroutineScope.launch {
                repository.updateShareStatus(video.id, true)
            }
        }
        )
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
@Composable
fun Home2() {
    // 状态管理
    var activeTab by remember { mutableStateOf("home") }
    var activeNavItem by remember { mutableStateOf(2) } // 默认选中"直播"

    // 缺省数据 - 示例 FeedCard 数据
    val sampleFeedData = listOf(
        FeedCardData(
            image = R.drawable.btn_plus.toString(),
            title = "探索城市美食之旅",
            description = "发现隐藏在城市角落的美味佳肴，每一口都是惊喜",
            author = "美食探索家",
            avatar = "https://i.pravatar.cc/150?img=1",
            likes = 12345
        ),
        FeedCardData(
            image = "https://picsum.photos/400/533?random=2",
            title = "周末户外运动指南",
            description = "享受阳光，拥抱自然，让身体和心灵都得到放松",
            author = "运动达人",
            avatar = "https://i.pravatar.cc/150?img=2",
            likes = 8567
        ),
        FeedCardData(
            image = "https://picsum.photos/400/533?random=3",
            title = "摄影技巧分享",
            description = "用镜头记录生活中的美好瞬间，捕捉每一个精彩时刻",
            author = "摄影师小王",
            avatar = "https://i.pravatar.cc/150?img=3",
            likes = 23456
        ),
        FeedCardData(
            image = "https://picsum.photos/400/533?random=4",
            title = "旅行日记：云南之行",
            description = "彩云之南，风景如画，感受不一样的民族风情",
            author = "旅行者",
            avatar = "https://i.pravatar.cc/150?img=4",
            likes = 18900
        ),
        FeedCardData(
            image = "https://picsum.photos/400/533?random=5",
            title = "科技产品评测",
            description = "最新科技产品深度体验，为你提供最真实的购买建议",
            author = "科技评测",
            avatar = "https://i.pravatar.cc/150?img=5",
            likes = 5678
        )
    )

    // 顶部导航项数据
    val navItems = remember(activeNavItem) {
        listOf(
            NavItem("司城", active = activeNavItem == 0),
            NavItem("团购", active = activeNavItem == 1),
            NavItem("直播", active = activeNavItem == 2, badge = true),
            NavItem("商城", active = activeNavItem == 3),
            NavItem("推荐", active = activeNavItem == 4, badge = true)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopNav(
                navItems = navItems,
                onMenuClick = {
                    // 处理菜单点击
                },
                onSearchClick = {
                    // 处理搜索点击
                }
            )
        },
//        bottomBar = {
//            BottomNav(
//                activeTab = activeTab,
//                onTabChange = { tab ->
//                    activeTab = tab
//                }
//            )
//        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
        ) {
            // FeedCard 列表
            sampleFeedData.forEachIndexed { index, feedData ->
                FeedCard(
                    data = feedData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        // 处理卡片点击
                    }
                )
            }
            
            // 底部留白，避免被 BottomNav 遮挡
            Spacer(
                modifier = Modifier.padding(bottom = 80.dp)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavGraph(
//    navController: NavHostController = rememberAnimatedNavController()
) {

    @Composable
    fun Cat(modifier: Modifier = Modifier) {
        Image(
            painterResource(id = R.drawable.arrow_up_float),
            contentDescription = "cute cat",
            contentScale = ContentScale.FillHeight,
            modifier = modifier.clip(shape = RoundedCornerShape(10)),
        )
    }

    val sharedElementKey = R.drawable.arrow_up_float
    var showLargeImage by remember { mutableStateOf(true) }

    SharedTransitionLayout(
        Modifier.clickable { showLargeImage = !showLargeImage }.fillMaxSize().padding(10.dp)
    ) {
        // In the SharedTransitionLayout, we will be able to access the receiver scope (i.e.
        // SharedTransitionScope) in order to create shared element transition.
        AnimatedContent(targetState = showLargeImage) { showLargeImageMode ->
            if (showLargeImageMode) {
                Cat(
                    Modifier.fillMaxSize()
                        .aspectRatio(1f)
                        // Creating a shared element. Note that this modifier is *after*
                        // the size modifier and aspectRatio modifier, because those size specs
                        // are not shared between the two shared elements.
                        .sharedElement(
                            rememberSharedContentState(sharedElementKey),
                            // Using the AnimatedVisibilityScope from the AnimatedContent
                            // defined above.
                            this@AnimatedContent,
                        )
                )
                Text(
                    "Cute Cat YT",
                    fontSize = 40.sp,
                    color = Color.Blue,
                    // Prefer Modifier.sharedBounds for text, unless the texts in both initial
                    // content and target content are exactly the same (i.e. same
                    // size/font/color)
                    modifier =
                        Modifier.fillMaxWidth()
                            // IMPORTANT: Prefer using wrapContentWidth/wrapContentSize over
                            // textAlign
                            // for shared text transition. This allows the layout system sees actual
                            // position and size of the text to facilitate bounds animation.
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .sharedBounds(
                                rememberSharedContentState(key = "text"),
                                this@AnimatedContent,
                            ),
                )
            } else {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Cat(
                            Modifier.size(100.dp)
                                // Creating another shared element with the same key.
                                // Note that this modifier is *after* the size modifier,
                                // The size changes between these two shared elements, i.e. the size
                                // is not shared between the two shared elements.
                                .sharedElement(
                                    rememberSharedContentState(sharedElementKey),
                                    this@AnimatedContent,
                                )
                        )
                        Text(
                            "Cute Cat YT",
                            // Change text color & size
                            fontSize = 20.sp,
                            color = Color.DarkGray,
                            // Prefer Modifier.sharedBounds for text, unless the texts in both
                            // initial content and target content are exactly the same (i.e. same
                            // size/font/color)
                            modifier =
                                Modifier
                                    // The modifier that is not a part of the shared content, but
                                    // rather
                                    // for positioning and sizes should be on the *left* side of
                                    // sharedBounds/sharedElement.
                                    .padding(start = 20.dp)
                                    .sharedBounds(
                                        // Here we use a string-based key, in contrast to the key
                                        // above.
                                        rememberSharedContentState(key = "text"),
                                        this@AnimatedContent,
                                    ),
                        )
                    }
                    Box(
                        Modifier.fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xffffcc5c), RoundedCornerShape(5.dp))
                    )
                    Box(
                        Modifier.fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xff2a9d84), RoundedCornerShape(5.dp))
                    )
                }
            }
        }
    }
}
