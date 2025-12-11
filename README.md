# SmallDY 项目说明

## 1. 项目概览
- **技术栈**：Jetpack Compose、Media3 ExoPlayer、Navigation Compose、Room
- **核心功能**：
  - 短视频双列瀑布流
  - 全屏播放器（双击点赞、自定义控制层）
  - AI 悬浮球与聊天页面

### 图片展示：
| ![Screenshot 1](https://github.com/user-attachments/assets/7e29074a-c10d-4872-a30b-88ee1ae09240) | ![Screenshot 2](https://github.com/user-attachments/assets/32b186b8-cfd5-4d30-b7d9-5ba5e7553bf1) | ![Screenshot 3](https://github.com/user-attachments/assets/7bd8db4c-5c9c-4068-8257-5e5b4b336176) |
|:--------------------------------------------:|:--------------------------------------------:|:--------------------------------------------:|
| 视频播放展示1                               | 视频播放展示2                               | AI 聊天展示                                 |


## 2. 主要模块

### `ui/Pages/HomePage/HomePage.kt`
- 顶部 Tab + HorizontalPager
- “视频”页为双列瀑布流 `VideoList`，支持下拉刷新（pullRefresh）
- 点击卡片跳转至全屏播放页（带预加载占位）

### `ui/common/VideoList.kt`
- 使用 `LazyVerticalStaggeredGrid` 组件实现双列瀑布流
- `VideoCard` 为卡片组件

### `ui/video/VideoPlayerScreen.kt`
- 使用 Media3 `PlayerView` 播放器，禁用原生控制栏，叠加自定义控制层
  - **单击**：显示/隐藏控制层
  - **双击**：点赞动画 + 点赞状态更新
  - **控制层功能**：
    - 播放/暂停（中间）
    - 前进/后退 10 秒（左右）
    - 细进度条可拖动
  - 点赞/评论/收藏/分享按钮即时更新数值（本地状态 + 回调）

### `ui/video/FullScreenVideoPlayerUI.kt`
- 右侧交互栏（关注、点赞、评论、收藏、分享）
- 左侧文案区域
- 双击手势点赞动画（心形爆炸）

### `ui/video/BurstAnimationButton.kt`
- 迸发动画按钮，点赞等交互使用

### `ui/ai/AIFloatingService.kt` + `AIChatActivity.kt`
- 悬浮球前台服务（`SYSTEM_ALERT_WINDOW` + `FGS dataSync`），点击进入 AI 聊天
- 进入聊天页面时隐藏悬浮球，退出后恢复
- 聊天页提供左右对齐气泡、输入框、发送按钮（占位回复，可接入后端）

### `SmallDYApplication.kt` / `data/database/*`
- Room 数据库与 `VideoRepository`，提供视频、互动状态、评论数据访问与刷新接口

## 3. 关键交互与手势
- **瀑布流下拉刷新**：`HomePage` “视频”Tab 使用 `pullRefresh` + `PullRefreshIndicator`
- **播放器手势**：
  - **单击**：控制层显示/隐藏
  - **双击**：点赞并动画
- **控制层**：
  - 播放/暂停
  - 前进/后退
  - 可拖动进度条；3 秒后自动隐藏（单击可再次显示）
- **AI 悬浮球**：进入聊天页自动隐藏，返回后自动显示

## 4. 权限与清单

### `AndroidManifest.xml`
- 权限：
  - `INTERNET`
  - `SYSTEM_ALERT_WINDOW`
  - `FOREGROUND_SERVICE`
  - `FOREGROUND_SERVICE_DATA_SYNC`
- Service：`.ui.ai.AIFloatingService`（`foregroundServiceType="dataSync"`）
- Activity：`.ui.ai.AIChatActivity`

## 5. 运行与注意事项
- 首次运行时需授予悬浮窗权限，前台服务会显示通知
- 下拉刷新目前为占位逻辑（读取本地 Flow），如需真实接口，可在 `onRefresh` 中调用网络刷新并更新 Room
- AI 聊天回复为占位，可接入后端或本地模型

## 6. 后续可拓展
- 替换刷新占位逻辑为真实网络请求与数据落库
- AI 聊天接入真实对话接口，支持上下文与流式回复
- 视频预加载与缓存策略优化；互动状态与服务端同步
