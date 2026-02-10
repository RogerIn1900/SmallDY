# SmallDY 项目分析与优化建议

## 一、对标开源项目

| 项目 | Stars | 架构 | UI | 模块化 | 视频播放 |
|------|-------|------|-----|--------|----------|
| **SmallDY（本项目）** | - | Clean + MVVM | Compose + M3 | 单模块 | 未集成 |
| [puskal-khadka/TikTok-Compose](https://github.com/puskal-khadka/TikTok-Compose) | ~475 | Clean + MVVM + UDF | Compose + M3 | 16 模块 | Media3 |
| [android/nowinandroid](https://github.com/android/nowinandroid) | ~20,600 | Clean + UDF | Compose + M3 | 多模块 | 无 |
| [iambaljeet/TikTok](https://github.com/iambaljeet/TikTok) | ~265 | MVVM | XML Views | 单模块 | ExoPlayer 2 |
| [oguzhaneksi/TikTokCompose](https://github.com/oguzhaneksi/TikTokCompose) | ~25 | MVVM | Compose | 单模块 | Media3 |

### 关键对标：TikTok-Compose

该项目是 Compose 生态中最完整的抖音仿品，采用 16 模块架构：

```
:app
:domain                       # 业务逻辑 / Use Cases
:data                         # Repository 实现
:core                         # 基础框架
:common:theme                 # 设计系统
:common:composable            # 共享 UI 组件
:feature:home                 # 视频流
:feature:commentlisting       # 评论
:feature:creatorprofile       # 创作者主页
:feature:inbox                # 通知
:feature:authentication       # 登录
:feature:friends              # 社交
:feature:myprofile            # 个人主页
:feature:setting              # 设置
:feature:cameramedia          # 拍摄
```

### 架构参考标杆：NowInAndroid

Google 官方架构示范项目，代表业界最佳实践：
- **Convention Plugins**（`:build-logic` 模块）复用构建配置
- **Version Catalogs**（`libs.versions.toml`）管理依赖版本
- **无 Mock 库的测试策略** — 用 test doubles 实现相同接口
- **Screenshot 测试** — Roborazzi 回归测试
- **Product Flavors** — `demo`（静态数据） vs `prod`（真实后端）

---

## 二、SmallDY 现状评估

### 做得好的地方

| 方面 | 评价 |
|------|------|
| 分层架构 | Domain / Data / UI 三层清晰分离 |
| 依赖注入 | Hilt + KSP，`@Binds` 抽象绑定 |
| 导航系统 | 类型安全的 sealed class 路由，响应式布局适配 |
| UI 模型映射 | 独立 mapper 函数，domain → UI 单向转换 |
| 构建配置 | Version Catalog 统一版本，无硬编码重复依赖 |
| 状态管理 | StateFlow + collectAsStateWithLifecycle |

### 存在的问题

#### P0 — 关键问题

**1. ViewModel 同步初始化阻塞主线程**

```kotlin
// 当前写法 — 阻塞 UI 线程
init {
    _feedItems.value = feedRepository.getFeedItems().map { it.toUiModel() }
}
```

```kotlin
// 建议写法 — 异步加载
init {
    viewModelScope.launch {
        _loading.value = true
        try {
            _feedItems.value = feedRepository.getFeedItems().map { it.toUiModel() }
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _loading.value = false
        }
    }
}
```

**影响范围**：HomeViewModel、FriendsViewModel、MessagesViewModel、ProfileViewModel 全部存在此问题。

**2. 无加载/错误状态**

当前 ViewModel 只暴露数据 StateFlow，缺少 `loading` 和 `error` 状态。UI 始终假设数据可用，无法处理加载中、加载失败、空数据等场景。

**3. 测试覆盖率为 0%**

仅有 Android Studio 自动生成的占位测试。对比 TikTok-Compose 有基础测试依赖声明，NowInAndroid 有完整的测试体系。

| 组件 | 当前覆盖率 |
|------|-----------|
| ViewModel | 0% |
| Repository | 0% |
| Mapper | 0% |
| UI 组件 | 0% |
| Navigation | 0% |

#### P1 — 重要问题

**4. AddScreen 为空占位符**

```kotlin
@Composable
fun AddScreen() {
    Text("Add")
}
```

作为核心功能入口（底部导航中间按钮），完全未实现。

**5. 视频播放未集成**

`VideoPlayer.kt` 是空类，`Page.Exoplayer` 路由已定义但无对应页面。对比 TikTok-Compose 使用 Media3 实现了完整的垂直滑动视频流。

**6. 硬编码颜色值散落各处**

```kotlin
// 相同颜色在多个文件重复
Color(0xFF05060A)   // FriendsScreen
Color(0xFFFF2850)   // FriendsScreen, others
Color(0xFF4F8DFF)   // MessagesScreen, FriendsScreen
```

NowInAndroid 的做法：在 Theme 中定义语义化颜色，组件通过 `MaterialTheme.colorScheme` 引用。

**7. 硬编码字符串无国际化支持**

所有中文文本直接写在 Composable 中，没有使用 `strings.xml` 资源文件。

#### P2 — 改进项

**8. 单模块结构**

当前项目 ~48 个源文件，单模块暂可接受。但随功能增长应考虑模块化拆分：

```
当前:  :app (everything)
目标:  :app + :core + :feature:home + :feature:friends + ...
```

**9. Repository 接口返回同步 List**

```kotlin
// 当前
interface FeedRepository {
    fun getFeedItems(): List<FeedItem>
}

// 建议：为未来网络请求做准备
interface FeedRepository {
    suspend fun getFeedItems(): List<FeedItem>
    // 或 Flow
    fun getFeedItems(): Flow<List<FeedItem>>
}
```

**10. 未使用的代码**

| 文件 | 问题 |
|------|------|
| `data/VideoIntroData.kt` | 数据类已定义，但无使用方引用 |
| `ui/video/VideoPlayer.kt` | 空类 |
| `MainActivity.onResume()` | 注释掉的 PiP 代码 |

---

## 三、与 TikTok-Compose 的差距分析

| 维度 | SmallDY | TikTok-Compose | 差距 |
|------|---------|----------------|------|
| 视频播放 | 未实现 | Media3 + VerticalPager | 核心功能缺失 |
| 视频拍摄 | 未实现 | CameraX 集成 | 核心功能缺失 |
| 评论系统 | 未实现 | 完整评论列表 | 功能缺失 |
| UDF 状态管理 | 基础 StateFlow | 完整 UDF + Action/State | 架构差距 |
| 模块化 | 单模块 | 16 模块 | 构建架构差距 |
| Use Cases | 无 | 有 domain usecase 层 | 可后续添加 |
| 主题系统 | 基础 Material 主题 | 完整设计系统模块 | 体验差距 |
| 错误处理 | 无 | 基础错误处理 | 稳定性差距 |

### SmallDY 的优势

| 方面 | 说明 |
|------|------|
| KSP 替代 kapt | TikTok-Compose 仍用 kapt，SmallDY 已用 KSP，编译更快 |
| Version Catalog | TikTok-Compose 用旧式 buildSrc，SmallDY 已用 libs.versions.toml |
| 响应式布局 | WindowSizeClass 适配平板/折叠屏，TikTok-Compose 无此设计 |
| Compose BOM | 版本管理更规范 |

---

## 四、优化路线图

### 阶段 1：修复基础问题（建议优先）

```
1. [ ] ViewModel 改为异步初始化（viewModelScope.launch）
2. [ ] 添加 loading / error StateFlow 到每个 ViewModel
3. [ ] Repository 接口方法改为 suspend 函数
4. [ ] 为 Mapper 函数编写单元测试（formatLikes、toUiModel）
5. [ ] 为 ViewModel 编写单元测试
6. [ ] 清理未使用代码（空 VideoPlayer 类、注释掉的 PiP 代码）
```

### 阶段 2：核心功能补全

```
1. [ ] 集成 Media3 ExoPlayer 视频播放
2. [ ] 实现 VerticalPager 垂直滑动视频流
3. [ ] 实现视频预加载机制
4. [ ] 补全 AddScreen（至少实现选择/预览视频）
5. [ ] 实现评论列表页面
```

### 阶段 3：代码质量提升

```
1. [ ] 提取硬编码颜色到 Theme / Color.kt
2. [ ] 提取硬编码字符串到 strings.xml
3. [ ] 添加 UiState sealed class 统一管理加载/成功/错误状态
4. [ ] 添加 Compose UI 测试
5. [ ] 启用 R8/ProGuard 代码混淆
```

### 阶段 4：架构演进（可选）

```
1. [ ] 多模块拆分（:core, :feature:home, :feature:friends ...）
2. [ ] 添加 Convention Plugins（参考 NowInAndroid :build-logic）
3. [ ] 添加网络层（Retrofit + kotlinx.serialization）
4. [ ] 添加本地数据库（Room）
5. [ ] 实现离线优先架构
```

---

## 五、参考资源

| 资源 | 用途 |
|------|------|
| [TikTok-Compose](https://github.com/puskal-khadka/TikTok-Compose) | 功能参考：视频流、评论、拍摄 |
| [NowInAndroid](https://github.com/android/nowinandroid) | 架构参考：构建系统、测试、模块化 |
| [Android 官方架构指南](https://developer.android.com/topic/architecture) | 架构原则 |
| [Media3 ExoPlayer 文档](https://developer.android.com/guide/topics/media/media3) | 视频播放集成 |
| [Hilt 测试指南](https://developer.android.com/training/dependency-injection/hilt-testing) | ViewModel 测试 |
