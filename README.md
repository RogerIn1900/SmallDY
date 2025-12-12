
# SmallDY

一个仿抖音风格的短视频应用，使用 Jetpack Compose 和 ExoPlayer 实现流畅的视频流体验。

## 📱 项目概述

SmallDY 是一个功能完整的短视频应用，实现了类似抖音的核心交互体验，包括双列瀑布流视频展示、全屏视频播放、AI 悬浮窗助手等功能。项目采用现代化的 Android 开发技术栈，包括 Jetpack Compose、MVVM 架构、Room 数据库等。
### 图片展示：
| ![Screenshot 1](https://github.com/user-attachments/assets/7e29074a-c10d-4872-a30b-88ee1ae09240) | ![Screenshot 2](https://github.com/user-attachments/assets/32b186b8-cfd5-4d30-b7d9-5ba5e7553bf1) | ![Screenshot 3](https://github.com/user-attachments/assets/7bd8db4c-5c9c-4068-8257-5e5b4b336176) |
|:--------------------------------------------:|:--------------------------------------------:|:--------------------------------------------:|
| 视频播放展示1                               | 视频播放展示2                               | AI 聊天展示                                 |


## ✨ 核心功能特性

### 🎬 视频播放功能

#### 1. **双击点赞动画**
- 在视频播放区域双击触发点赞动画
- 使用 `Animatable` 和 `Spring` 动画实现流畅的缩放和淡出效果
- 双击判定时间窗口：400ms，距离阈值：50px
- 动画包含迸发的小心心效果，持续约 1 秒

#### 2. **视频播放控制**
- **播放/暂停**：单击视频区域切换播放状态
- **快进/快退**：通过播放器控制栏实现 ±10 秒跳转
- **进度条拖动**：支持拖动进度条精确跳转到指定位置
- **自动隐藏控制栏**：播放开始后 3 秒自动隐藏，点击视频区域重新显示

#### 3. **内流视频上下滑动切换**
- 使用 `VerticalPager` 实现垂直滑动切换视频
- 支持手势滑动和自动播放下一视频
- 滑动过程中保持流畅的过渡动画

#### 4. **单视频播放机制**
- 仅当前可见页面播放视频，其他页面显示视频首帧或封面
- 使用 `VerticalPager.currentPage` 判断当前页面
- 非当前页面的播放器处于暂停状态，节省资源

#### 5. **播放位置记忆与恢复**
- 使用 `MutableMap<String, Long>` 存储每个视频的播放进度（毫秒）
- 离开页面时自动保存当前播放位置
- 返回已播放的视频时，自动 `seekTo` 到上次位置
- 通过 `Player.Listener` 监听 `STATE_READY` 状态后恢复播放位置

#### 6. **自动播放下一视频**
- 监听 `Player.STATE_ENDED` 事件
- 视频播放结束后自动切换到下一个视频
- 通过 `pagerState.animateScrollToPage()` 实现平滑切换

#### 7. **返回按钮功能**
- 支持系统返回键和 UI 返回按钮
- 返回时保持双列瀑布流的滚动位置和布局顺序
- 使用 `rememberSaveable` 保存 `LazyStaggeredGridState`

### 🎨 UI 交互功能

#### 8. **可拖动 AI 悬浮窗**
- 使用 `WindowManager` 和 `TYPE_APPLICATION_OVERLAY` 实现全局悬浮窗
- 支持拖动手势，实时更新悬浮窗位置
- 悬浮窗位置保存在 `WindowManager.LayoutParams` 中

#### 9. **AI 聊天页面**
- 点击悬浮窗进入全屏聊天页面
- 进入聊天页面时自动隐藏悬浮窗（`ACTION_HIDE_BUBBLE`）
- 退出聊天页面时恢复显示悬浮窗（`ACTION_SHOW_BUBBLE`）

#### 10. **聊天消息响应**
- 发送消息后立即显示用户消息
- 模拟 AI 回复（可接入真实后端）
- 使用 `LazyColumn` 实现消息列表滚动

#### 11. **应用退出后悬浮窗保持**
- 使用前台服务（`ForegroundService`）保持进程存活
- 服务类型：`FOREGROUND_SERVICE_DATA_SYNC`
- 通过通知栏显示持续运行状态
- 应用退到后台后悬浮窗仍然可见

#### 12. **左右滑动切换顶部页面**
- 使用 `HorizontalPager` 和 `ScrollableTabRow` 实现 Tab 切换
- 支持 10 个 Tab 页面（视频、推荐、关注、直播等）
- Tab 点击和 Pager 滑动双向同步
- 使用 `derivedStateOf` 同步选中状态

### 📺 视频展示功能

#### 13. **双列瀑布流展示**
- 使用 `LazyVerticalStaggeredGrid` 实现双列布局
- 每列显示视频简介卡片（`VideoCard`）
- 卡片包含：视频封面、标题、作者信息、点赞数、评论数等

#### 14. **上下滑动保持布局稳定**
- 使用 `rememberSaveable` 保存 `LazyStaggeredGridState`
- 滚动位置在配置变更和导航返回时保持不变
- 使用 `VideoHeightCache` 缓存每个视频卡片的高度
- 避免滚动时重新计算布局导致的位置跳动

#### 15. **根据图片高度确定卡片高度**
- 使用 `MediaMetadataRetriever` 提取视频首帧
- 计算视频宽高比（aspectRatio = width / height）
- 根据卡片宽度和宽高比动态计算卡片高度
- 高度信息缓存到 `VideoHeightCache`，确保布局稳定

#### 16. **视频信息展示**
- **点赞数**：显示格式化的点赞数（超过 10000 显示为 "X.X万"）
- **评论数**：显示评论数量
- **收藏/分享**：显示收藏和分享状态
- **视频名称**：显示视频标题（最多 2 行，超出显示省略号）
- **视频描述**：显示视频描述信息
- **作者信息**：显示作者头像、昵称、认证状态
- **位置信息**：显示视频拍摄位置（如果有）

### 🎯 导航与跳转

#### 17. **底部导航栏**
- 使用 `Scaffold` 的 `bottomBar` 实现底部导航
- 包含：首页、发现、发布、消息、我的
- 支持导航到不同页面（视频播放器、主页、我的等）

#### 18. **视频播放器跳转**
- 点击瀑布流中的视频卡片跳转到全屏播放器
- 使用 `NavController.navigate()` 传递视频索引
- 支持从播放器返回到瀑布流，保持原位置

#### 19. **点赞动画**
- 双击视频区域触发点赞动画
- 动画包含：缩放、淡出、迸发效果
- 点赞状态同步更新到数据库

### 🛠️ 技术实现

#### 20. **FFmpeg 视频合成**
- 使用 FFmpeg 将 B 站缓存视频合成为 MP4 格式
- 支持批量处理视频文件
- 合成的视频存储在 `res/raw` 目录

#### 21. **视频首帧提取**
- 使用 `MediaMetadataRetriever` 提取视频首帧
- 提取的首帧作为视频封面显示
- 首帧 Bitmap 缓存到 `VideoHeightCache`，避免重复提取

#### 22. **视频播放结束自动播放**
- 监听 `Player.STATE_ENDED` 事件
- 自动切换到下一个视频并开始播放
- 通过 `onVideoEnded` 回调触发切换逻辑

## 🏗️ 技术架构

### 架构模式
- **MVVM（Model-View-ViewModel）**：清晰的职责分离
- **Repository 模式**：统一的数据访问接口
- **依赖注入**：使用 Hilt 进行依赖管理

### 核心技术栈

#### UI 框架
- **Jetpack Compose**：现代化的声明式 UI 框架
- **Material 3**：Material Design 3 组件库
- **Navigation Compose**：页面导航管理
- **LazyVerticalStaggeredGrid**：双列瀑布流布局
- **VerticalPager / HorizontalPager**：垂直/水平页面切换

#### 视频播放
- **Media3 ExoPlayer**：强大的视频播放引擎
  - 版本：1.8.0
  - 支持：MP4、HLS、DASH 等格式
  - 自定义 `PlayerView` 控制栏

#### 数据存储
- **Room Database**：本地数据库
  - 视频信息表（Video）
  - 用户交互表（VideoInteraction）
  - 评论表（Comment）
- **DataStore**：轻量级数据存储（如用户偏好设置）

#### 图片加载
- **Coil**：异步图片加载库
  - 支持网络图片和本地资源
  - 自动缓存和内存管理

#### 状态管理
- **StateFlow / Flow**：响应式数据流
- **remember / rememberSaveable**：Compose 状态管理
- **LaunchedEffect / DisposableEffect**：副作用处理

#### 其他技术
- **Coroutines**：异步任务处理
- **Hilt**：依赖注入框架
- **WindowManager**：悬浮窗管理
- **MediaMetadataRetriever**：视频元数据提取

## 📂 项目结构

```
app/src/main/java/com/example/smalldy/
├── data/
│   ├── database/          # Room 数据库相关
│   │   ├── Video.kt       # 视频实体
│   │   ├── VideoDao.kt    # 视频数据访问对象
│   │   └── DatabaseInitializer.kt  # 数据库初始化
│   ├── VideoModels.kt     # 视频数据模型
│   └── VideoRepository.kt # 数据仓库
├── ui/
│   ├── Pages/
│   │   ├── HomePage/      # 主页（双列瀑布流）
│   │   ├── VideoPlayerPage/  # 视频播放页面
│   │   ├── MinePage/      # 我的页面
│   │   └── MsgPage/       # 消息页面
│   ├── video/
│   │   ├── VideoPlayerScreen.kt      # 视频播放器 UI
│   │   ├── VideoPlayerViewModel.kt    # 播放器 ViewModel
│   │   ├── FullScreenVideoPlayerUI.kt # 全屏播放器 UI
│   │   └── VideoPlayerPreloader.kt    # 播放器预加载
│   ├── common/
│   │   ├── VideoCard.kt           # 视频卡片组件
│   │   ├── VideoList.kt           # 视频列表组件
│   │   └── VideoThumbnailImage.kt  # 视频缩略图组件
│   ├── ai/
│   │   ├── AIFloatingService.kt   # AI 悬浮窗服务
│   │   └── AIChatActivity.kt      # AI 聊天页面
│   └── navigation/
│       ├── MainNavigation.kt     # 主导航
│       └── NavGraphBuilder.kt     # 导航图构建
└── Utils/
    └── VideoThumbnailUtils.kt     # 视频缩略图工具类
```

## 🔧 关键技术实现细节

### 1. 视频高度缓存机制

```kotlin
object VideoHeightCache {
    // LRU 缓存 Bitmap（内存的 1/8）
    private val thumbnailCache = LruCache<String, Bitmap>(cacheSizeKb)
    
    // 宽高比缓存（ConcurrentHashMap）
    private val aspectRatioMap = ConcurrentHashMap<String, Float>()
    
    // 高度缓存（ConcurrentHashMap）
    private val heightMap = ConcurrentHashMap<String, Dp>()
}
```

**优势**：
- 避免重复提取视频首帧
- 确保滚动时布局稳定
- 内存占用可控（LRU 策略）

### 2. 播放位置记忆

```kotlin
// 存储播放位置
val playbackPositions = remember { mutableStateMapOf<String, Long>() }

// 离开页面时保存
DisposableEffect(video.id) {
    onDispose {
        playbackPositions[video.id] = player.currentPosition
    }
}

// 返回时恢复
LaunchedEffect(video.id) {
    val lastPosition = playbackPositions[video.id] ?: 0L
    if (lastPosition > 0) {
        player.seekTo(lastPosition)
    }
}
```

### 3. 单视频播放控制

```kotlin
VerticalPager(state = pagerState) { page ->
    val isCurrentPage = page == pagerState.currentPage
    
    if (isCurrentPage) {
        // 当前页：播放视频
        VideoPlayerWithExoPlayer(...)
    } else {
        // 非当前页：显示封面
        VideoThumbnailPlaceholder(...)
    }
}
```

### 4. AI 悬浮窗实现

```kotlin
class AIFloatingService : Service() {
    private fun showFloatingBubble() {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        
        layoutParams = WindowManager.LayoutParams(
            size, size, type,
            FLAG_NOT_FOCUSABLE or FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        
        windowManager?.addView(bubbleView, layoutParams)
    }
}
```

### 5. 双击点赞动画

```kotlin
// 双击判定
val timeSinceLastTap = currentTime - lastTapTime
val distance = sqrt((offset.x - lastTapOffset.x)^2 + (offset.y - lastTapOffset.y)^2)

if (timeSinceLastTap < 400ms && distance < 50px) {
    // 触发点赞动画
    showDoubleTapAnimation = true
    onLikeClick(true)
}

// 动画实现
scale.animateTo(1.5f, tween(250))
scale.animateTo(1.1f, spring(...))
alpha.animateTo(0f, tween(500))
```

## 📋 主要依赖

### Compose BOM
```kotlin
implementation(platform("androidx.compose:compose-bom:2025.08.00"))
```

### 核心依赖
- `androidx.compose.ui`
- `androidx.compose.material3`
- `androidx.compose.animation`
- `androidx.compose.foundation.layout`

### 视频播放
- `androidx.media3:media3-exoplayer:1.8.0`
- `androidx.media3:media3-ui:1.8.0`
- `androidx.media3:media3-common:1.8.0`

### 数据存储
- `androidx.room:room-runtime`
- `androidx.room:room-ktx`

### 图片加载
- `io.coil-kt:coil-compose`

### 依赖注入
- `com.google.dagger:hilt-android:2.57.1`
- `com.google.dagger:hilt-android-compiler`

### 导航
- `androidx.navigation:navigation-compose:2.9.6`

## 🎯 功能演示

### 双列瀑布流
- 上下滑动浏览视频卡片
- 点击卡片进入全屏播放器
- 返回时保持原滚动位置

### 全屏播放器
- 上下滑动切换视频
- 双击点赞，单击显示/隐藏控制栏
- 拖动进度条跳转播放位置
- 视频结束自动播放下一个

### AI 悬浮窗
- 拖动悬浮窗到任意位置
- 点击进入聊天页面
- 发送消息获得回复
- 应用退出后悬浮窗仍然存在

## 🔍 性能优化

1. **视频首帧缓存**：避免重复提取，提升列表滚动性能
2. **播放器预加载**：提前准备下一个视频的播放器实例
3. **高度缓存**：稳定瀑布流布局，避免重新计算
4. **单视频播放**：节省资源，仅当前页播放
5. **LRU 缓存策略**：控制内存占用，自动清理不常用数据

---

**SmallDY** - 让视频流体验更流畅 🎬

## 6. 后续可拓展
- 替换刷新占位逻辑为真实网络请求与数据落库
- AI 聊天接入真实对话接口，支持上下文与流式回复
- 视频预加载与缓存策略优化；互动状态与服务端同步
