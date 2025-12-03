# SmallDY 短视频应用技术设计文档（优化版）

## 📋 文档说明

本文档基于原技术设计文档进行优化，修复了版本错误、过时API、架构问题等，确保与当前项目状态和最新技术栈保持一致。

---

## 1. 项目目标

### 1.1 核心目标
- 实现一个类似抖音的短视频应用，包含双列外流、视频内流、评论系统和AI问答功能
- 整体运行流畅，无崩溃卡死，界面UI和交互风格统一，整体呈现效果美观
- 支持滑动1~2屏仍有元素展示，提供良好的用户体验

### 1.2 功能目标
- **双列外流**：实现瀑布流布局，展示视频封面、标题、作者信息、点赞数等
- **视频内流**：全屏视频播放，支持上下滑动切换、播放控制、互动功能
- **评论系统**：支持查看和发布评论，实时更新评论列表
- **AI问答**：提供悬浮球入口，支持与AI进行对话交互

---

## 2. 技术选型

### 2.1 架构模式
**选择：MVVM（Model-View-ViewModel）**

**原因：**
- 清晰的职责分离：View负责UI展示，ViewModel处理业务逻辑，Model管理数据
- 数据驱动UI：通过StateFlow实现响应式编程
- 易于测试：ViewModel可独立测试，不依赖UI
- 生命周期感知：与Android生命周期良好集成
- 符合Jetpack Compose的最佳实践

### 2.2 核心技术栈（已修正版本）

| 技术 | 版本/说明 | 用途 | 状态 |
|------|----------|------|------|
| **Kotlin** | 2.0.21 | 主要开发语言 | ✅ 已配置 |
| **Jetpack Compose BOM** | 2024.09.00 | UI框架版本管理 | ✅ 已配置 |
| **Navigation Compose** | 2.9.6 | 页面导航 | ✅ 已配置 |
| **ExoPlayer** | 2.19.1+ | 视频播放引擎 | ⚠️ 需添加 |
| **Coil** | 2.6.0 | 图片加载库 | ✅ 已配置 |
| **ViewModel** | lifecycle-viewmodel-compose:2.9.4 | 状态管理 | ⚠️ 需添加 |
| **StateFlow/Flow** | kotlinx-coroutines | 数据流 | ✅ 已包含 |
| **Retrofit** | 2.9.0+ | 网络请求（如需要） | ⚠️ 可选 |
| **Room** | 2.6.1+ | 本地数据库（如需要） | ⚠️ 可选 |

### 2.3 重要变更说明

#### ⚠️ Accompanist 库状态更新
**重要：** Accompanist 的许多功能已被 Compose 官方库吸收，不再需要单独依赖：

1. **accompanist-pager** → **androidx.compose.foundation:foundation**
   - `HorizontalPager` 和 `VerticalPager` 现在在 Compose Foundation 中
   - 无需额外依赖

2. **accompanist-swiperefresh** → **androidx.compose.material3:material3**
   - `SwipeRefresh` 现在在 Material3 中
   - 无需额外依赖

3. **accompanist-navigation-animation** → **Navigation Compose 内置**
   - Navigation Compose 2.7.0+ 已内置转场动画支持
   - 使用 `composeOptions` 配置动画

4. **accompanist-permissions** → **仍可使用或使用官方 API**
   - 可以使用 Accompanist 0.32.0+（如果仍需要）
   - 或使用 `ActivityResultContracts.RequestPermission()`

5. **accompanist-systemuicontroller** → **仍可使用**
   - 如果仍需要，可以使用最新版本

#### SharedElement 转场动画
- Compose 1.5.0+ 引入了 `SharedTransitionLayout` 和 `Modifier.sharedElement()`
- 需要确保 Compose BOM 版本支持（2024.09.00 已支持）

#### ViewModel 依赖注入
- **方案一（推荐）**：使用 `viewModel()` 函数（无需 Hilt）
- **方案二**：使用 Hilt + `hiltViewModel()`（需要额外配置）

---

## 3. 架构设计

### 3.1 MVVM架构图

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │FeedScreen│  │VideoScreen│ │CommentScreen│ │ChatScreen│   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘   │
│       │             │              │              │          │
└───────┼─────────────┼──────────────┼──────────────┼──────────┘
        │             │              │              │
        ▼             ▼              ▼              ▼
┌─────────────────────────────────────────────────────────────┐
│                      ViewModel Layer                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │FeedViewModel│ │VideoViewModel│ │CommentViewModel│ │ChatViewModel│   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘   │
│       │             │              │              │          │
└───────┼─────────────┼──────────────┼──────────────┼──────────┘
        │             │              │              │
        ▼             ▼              ▼              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Repository Layer                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │VideoRepository│ │UserRepository│ │CommentRepository│ │AIRepository│   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘   │
│       │             │              │              │          │
└───────┼─────────────┼──────────────┼──────────────┼──────────┘
        │             │              │              │
        ▼             ▼              ▼              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │RemoteDataSource│ │LocalDataSource│ │CacheManager│ │AIService│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 包结构设计

```
com.example.smalldy/
├── data/
│   ├── model/              # 数据模型
│   │   ├── VideoData.kt
│   │   ├── UserData.kt
│   │   ├── CommentData.kt
│   │   └── ChatMessage.kt
│   ├── repository/         # 数据仓库
│   │   ├── VideoRepository.kt
│   │   ├── UserRepository.kt
│   │   └── CommentRepository.kt
│   └── local/              # 本地数据源
│       └── MockDataSource.kt
├── domain/                 # 业务逻辑层（可选）
│   └── usecase/
├── ui/
│   ├── feed/               # 双列外流
│   │   ├── FeedScreen.kt
│   │   ├── FeedViewModel.kt
│   │   └── FeedCard.kt
│   ├── video/              # 视频内流
│   │   ├── VideoScreen.kt
│   │   ├── VideoViewModel.kt
│   │   ├── VideoPlayer.kt
│   │   └── VideoControls.kt
│   ├── comment/            # 评论页面
│   │   ├── CommentScreen.kt
│   │   ├── CommentViewModel.kt
│   │   └── CommentItem.kt
│   ├── chat/               # AI问答
│   │   ├── ChatScreen.kt
│   │   ├── ChatViewModel.kt
│   │   └── FloatingBall.kt
│   └── common/             # 公共组件
│       ├── TopNav.kt
│       ├── BottomNav.kt
│       └── RefreshIndicator.kt
└── util/                   # 工具类
    ├── ImagePicker.kt
    └── VideoUtils.kt
```

---

## 4. 依赖配置修正

### 4.1 需要添加的依赖

在 `app/build.gradle.kts` 中添加：

```kotlin
dependencies {
    // ... 现有依赖 ...
    
    // ViewModel (必需)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    
    // ExoPlayer (视频播放)
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-common:1.4.1")
    
    // Compose Foundation (包含 Pager)
    implementation("androidx.compose.foundation:foundation")
    
    // Coroutines (如果还没有)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    
    // 可选：Hilt (如果需要依赖注入)
    // implementation("com.google.dagger:hilt-android:2.52")
    // kapt("com.google.dagger:hilt-compiler:2.52")
    // implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}
```

### 4.2 版本统一

**修正后的版本配置：**

```kotlin
// gradle/libs.versions.toml
[versions]
composeBom = "2024.09.00"  // ✅ 正确版本（不是 2025.08.00）
navigationCompose = "2.9.6"  // ✅ 统一版本
lifecycleRuntimeKtx = "2.9.4"
```

---

## 5. 功能实现思路和难点（修正版）

### 5.1 双列外流（Feed流）

#### 5.1.1 UI布局实现

**技术方案：**
- 使用 `LazyVerticalGrid` 实现双列布局
- 每个卡片包含：封面图、标题、作者信息、点赞数

**实现代码结构（修正版）：**
```kotlin
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel() // ✅ 使用 viewModel() 而不是 hiltViewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videos) { video ->
            FeedCard(
                video = video,
                onClick = { viewModel.navigateToVideo(video.id) }
            )
        }
    }
}
```

#### 5.1.2 点击封面进入视频内流

**转场动画实现（修正版）：**

Compose 1.5.0+ 使用 `SharedTransitionLayout`：

```kotlin
// 在外流中使用
@Composable
fun FeedCard(video: VideoData, onClick: () -> Unit) {
    SharedTransitionLayout {
        AsyncImage(
            model = video.coverUrl,
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = "video_${video.id}"),
                    this@SharedTransitionLayout
                )
                .clickable { onClick() }
        )
    }
}

// 在视频内流中使用相同的key
@Composable
fun VideoScreen(video: VideoData) {
    SharedTransitionLayout {
        VideoPlayer(
            video = video,
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = "video_${video.id}"),
                    this@SharedTransitionLayout
                )
        )
    }
}
```

#### 5.1.4 下拉刷新和上拉加载（修正版）

**使用 Material3 的 SwipeRefresh：**

```kotlin
@Composable
fun FeedScreen(viewModel: FeedViewModel = viewModel()) {
    val videos by viewModel.videos.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val listState = rememberLazyListState()
    
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.refreshVideos() }
    ) {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(2),
            // ...
        ) {
            items(videos) { video ->
                FeedCard(video = video)
            }
        }
        
        // 监听滚动到底部
        LaunchedEffect(listState) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            }.collect { lastIndex ->
                if (lastIndex == videos.size - 1) {
                    viewModel.loadMoreVideos()
                }
            }
        }
    }
}
```

#### 5.1.6 左右滑动切换顶部Bar（修正版）

**使用 Compose Foundation 的 HorizontalPager：**

```kotlin
@Composable
fun TopTabScreen() {
    val tabs = listOf("司城", "团购", "直播", "商城", "推荐")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    
    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { 
                        coroutineScope.launch { 
                            pagerState.animateScrollToPage(index) 
                        } 
                    }
                ) {
                    Text(tab)
                }
            }
        }
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> FeedScreen()
                1 -> GroupBuyScreen()
                // ...
            }
        }
    }
}
```

### 5.2 视频内流（Video Stream）

#### 5.2.2 点击暂停、播放（修正版）

**使用 Media3 ExoPlayer：**

```kotlin
@Composable
fun VideoPlayer(
    video: VideoData,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(video.videoUrl))
            prepare()
            playWhenReady = true
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
    
    val isPlaying by remember {
        derivedStateOf { player.isPlaying }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { 
                if (isPlaying) player.pause() else player.play()
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false // 隐藏默认控制器
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        if (!isPlaying) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "播放",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
            )
        }
    }
}
```

#### 5.2.3 手指上下移动、切换视频（修正版）

**使用 Compose Foundation 的 VerticalPager：**

```kotlin
@Composable
fun VideoScreen(
    initialVideoId: String,
    viewModel: VideoViewModel = viewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = videos.indexOfFirst { it.id == initialVideoId }
            .takeIf { it >= 0 } ?: 0,
        pageCount = { videos.size }
    )
    
    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        VideoPlayerItem(
            video = videos[page],
            isActive = pagerState.currentPage == page
        )
    }
    
    // 预加载逻辑
    LaunchedEffect(pagerState.currentPage) {
        val current = pagerState.currentPage
        if (current < videos.size - 1) {
            viewModel.preloadVideo(videos[current + 1].id)
        }
    }
}
```

---

## 6. 数据层设计

### 6.1 数据模型

```kotlin
// 视频数据
data class VideoData(
    val id: String,
    val coverUrl: String,
    val videoUrl: String,
    val title: String,
    val description: String?,
    val author: UserData,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val isLiked: Boolean,
    val timestamp: Long
)

// 用户数据
data class UserData(
    val id: String,
    val name: String,
    val avatar: String,
    val bio: String?,
    val followerCount: Int
)

// 评论数据
data class CommentData(
    val id: String,
    val author: UserData,
    val content: String,
    val timestamp: Long,
    val likeCount: Int,
    val replies: List<CommentData> = emptyList()
)

// 聊天消息
data class ChatMessage(
    val id: String,
    val content: String,
    val type: MessageType, // USER, AI
    val timestamp: Long
)

enum class MessageType {
    USER, AI
}
```

### 6.2 ViewModel 实现（修正版）

```kotlin
class FeedViewModel : ViewModel() {
    private val repository = VideoRepository()
    
    private val _videos = MutableStateFlow<List<VideoData>>(emptyList())
    val videos: StateFlow<List<VideoData>> = _videos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    init {
        loadVideos()
    }
    
    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _videos.value = repository.getVideos()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshVideos() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                _videos.value = repository.refreshVideos()
            } finally {
                _isRefreshing.value = false
            }
        }
    }
    
    fun loadMoreVideos() {
        viewModelScope.launch {
            val moreVideos = repository.loadMoreVideos()
            _videos.value = _videos.value + moreVideos
        }
    }
}
```

---

## 7. 关键技术难点总结（修正版）

### 7.1 转场动画
- **难点**：从封面到全屏视频的无缝转场
- **解决方案**：使用 Compose 1.5.0+ 的 `SharedTransitionLayout` 和 `Modifier.sharedElement()`
- **注意**：确保 Compose BOM 版本 >= 2024.09.00

### 7.2 视频预加载
- **难点**：上下滑动切换视频时的流畅性
- **解决方案**：使用 ExoPlayer 的预加载机制，提前准备相邻视频
- **优化**：使用 `ExoPlayer.Builder` 配置缓存策略

### 7.3 手势冲突
- **难点**：双击点赞、上下滑动、左右滑动的冲突处理
- **解决方案**：使用 `Modifier.pointerInput` 精确控制手势优先级
- **技巧**：使用 `detectTapGestures` 和 `detectDragGestures` 区分不同手势

### 7.4 性能优化
- **难点**：大量视频列表的流畅滚动
- **解决方案**：
  - 使用 `LazyColumn`/`LazyVerticalGrid` 进行虚拟化
  - 使用 Coil 的图片缓存
  - 视频预加载和播放器复用
  - 使用 `remember` 和 `derivedStateOf` 减少重组

### 7.5 状态管理
- **难点**：多个页面间的状态同步
- **解决方案**：
  - 使用 ViewModel + StateFlow 管理状态
  - 使用 Navigation Compose 传递参数
  - 使用 `rememberSaveable` 保存状态

---

## 8. 开发计划（优化版）

### 阶段一：基础架构搭建（1-2天）
- [x] 搭建MVVM架构
- [x] 配置依赖（Compose、Navigation）
- [ ] 添加 ExoPlayer 依赖
- [ ] 添加 ViewModel 依赖
- [ ] 实现基础导航结构
- [ ] 创建数据模型和 Mock 数据源

### 阶段二：双列外流实现（2-3天）
- [ ] 实现双列布局（LazyVerticalGrid）
- [ ] 实现下拉刷新（Material3 SwipeRefresh）
- [ ] 实现上拉加载
- [ ] 实现转场动画（SharedTransitionLayout）
- [ ] 实现Tab切换（HorizontalPager）

### 阶段三：视频内流实现（3-4天）
- [ ] 集成 ExoPlayer（Media3）
- [ ] 实现上下滑动切换（VerticalPager）
- [ ] 实现播放控制
- [ ] 实现双击点赞动画
- [ ] 实现音乐转盘
- [ ] 实现视频预加载

### 阶段四：评论系统（1-2天）
- [ ] 实现评论列表
- [ ] 实现评论发布
- [ ] 实现自适应布局

### 阶段五：AI问答（2-3天）
- [ ] 实现悬浮球（可拖动）
- [ ] 实现聊天界面
- [ ] 集成AI服务（API或本地模型）

### 阶段六：优化和测试（1-2天）
- [ ] 性能优化
- [ ] UI/UX优化
- [ ] 测试和Bug修复

---

## 9. 常见问题与解决方案

### Q1: 为什么不再使用 Accompanist？
**A:** Accompanist 的许多功能已被 Compose 官方库吸收。使用官方库可以获得更好的支持和维护。

### Q2: 如何选择 ViewModel 的创建方式？
**A:** 
- 简单项目：使用 `viewModel()` 函数
- 复杂项目：使用 Hilt + `hiltViewModel()`

### Q3: SharedTransitionLayout 不工作？
**A:** 确保：
1. Compose BOM >= 2024.09.00
2. 使用相同的 `SharedContentState` key
3. 两个组件都在 `SharedTransitionLayout` 作用域内

### Q4: ExoPlayer 版本选择？
**A:** 使用 Media3（最新版本），而不是旧的 ExoPlayer 2.x。Media3 是 ExoPlayer 的下一代版本。

---

## 10. 参考资料

- [Jetpack Compose 官方文档](https://developer.android.com/jetpack/compose)
- [Compose Foundation (Pager)](https://developer.android.com/jetpack/compose/layouts/pager)
- [Media3 ExoPlayer 官方文档](https://developer.android.com/guide/topics/media/media3)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Coil 图片加载库](https://coil-kt.github.io/coil/)
- [ViewModel in Compose](https://developer.android.com/jetpack/compose/state#viewmodel)

---

## 11. 版本历史

- **v2.0 (优化版)**：修正版本错误、更新过时API、优化架构设计
- **v1.0 (原版)**：初始技术设计文档

---

**文档版本**：v2.0（优化版）  
**最后更新**：2024年12月  
**优化说明**：修正版本号、更新API使用、优化架构建议

