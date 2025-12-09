package com.example.smalldy.ui.video

import android.R.id.message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import android.content.Context
import com.example.smalldy.Utils.extractRawResourceName
import com.example.smalldy.Utils.getRawResourceUri
import com.example.smalldy.Utils.isRawResourcePath
import com.example.smalldy.data.ErrorMessage
import com.example.smalldy.data.VideoPlayerUIState
import com.example.smalldy.data.VideoPlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID



@UnstableApi
@OptIn(UnstableApi::class)
class VideoPlayerViewModel(
    private val player: Player
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoPlayerUIState())
    val uiState = _uiState.asStateFlow()
    
    // 新增：VideoPlayerState 用于更清晰的状态管理
    private val _playerState = MutableStateFlow(VideoPlayerState())
    val playerState = _playerState.asStateFlow()

    private var videoUrl: String? = null
    private var isPlayerPausedForResume = false

    init {
        player.prepare()
        player.addListener(object : Player.Listener{
            override fun onVideoSizeChanged(videoSize: VideoSize){
                _uiState.update{
                    it.copy(
                        videoWidth = videoSize.width,
                        videoHeight = videoSize.height,
                    )
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean){
                _uiState.update { it.copy(isPlaying = isPlaying) }
                // 同步更新 VideoPlayerState
                _playerState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int){
                when(playbackState){
                    Player.STATE_READY -> {
                        val duration = if (player.duration > 0) player.duration / 1000 else 0L // 转换为秒
                        _uiState.update {
                            it.copy(
                                hasVideoLoaded = true,
                                duration = player.duration
                            )
                        }
                        // 同步更新 VideoPlayerState
                        _playerState.update {
                            it.copy(
                                duration = duration,
                                isBuffering = false
                            )
                        }
                    }
                    Player.STATE_BUFFERING -> {
                        _playerState.update { it.copy(isBuffering = true) }
                    }
                }
            }
            
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updatePlayerPosition()
            }

            override fun onPlayerError(error: PlaybackException){
                val errorMessage = error.message ?: "播放错误"
                updateUIForError(errorMessage)
            }
        })
        
        // 定时更新播放进度
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(500) // 每500ms更新一次
                if (player.isPlaying) {
                    updatePlayerPosition()
                }
            }
        }
    }

    fun getPlayer() = player

    /**
     * 播放视频
     * 支持以下格式：
     * - HTTP/HTTPS URL：直接播放网络视频
     * - Android resource URI（android.resource://...）：播放本地资源
     * 
     * @param videoUrl 视频 URL 或资源路径
     */
    fun playVideo(videoUrl: String) {
        if (!_uiState.value.hasVideoLoaded || videoUrl != this.videoUrl) {
            this.videoUrl = videoUrl
            
            // 直接播放 URL 或 Android resource URI
            // ExoPlayer 支持 android.resource:// 格式的 URI
                player.apply {
                    setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
                    play()
                }
                _uiState.update { it.copy(hasVideoLoaded = true) }
        }
    }

    /**
     * 播放 raw 文件夹中的视频
     * 
     * @param context Context 对象，用于获取资源
     * @param rawResourceName raw 资源名称（不含扩展名），例如 "video" 对应 "res/raw/video.mp4"
     */
    fun playRawVideo(context: Context, rawResourceName: String) {
            try {
            val resourceUri = getRawResourceUri(context, rawResourceName)
            playVideo(resourceUri.toString())
        } catch (e: IllegalArgumentException) {
            updateUIForError("找不到 raw 资源: $rawResourceName")
        } catch (e: Exception) {
            updateUIForError("播放 raw 资源失败: ${e.message}")
        }
    }

    /**
     * 播放视频（支持 raw 资源路径格式）
     * 
     * 支持的格式：
     * - "raw://video" 或 "raw://video.mp4" -> 自动转换为 Android resource URI
     * - "raw/video" 或 "raw/video.mp4" -> 自动转换为 Android resource URI
     * - 其他格式 -> 直接使用
     * 
     * @param context Context 对象，仅在需要转换 raw 资源路径时使用
     * @param videoPath 视频路径或 URL
     */
    fun playVideo(context: Context, videoPath: String) {
        if (isRawResourcePath(videoPath)) {
            // 提取资源名称并播放 raw 资源
            val resourceName = extractRawResourceName(videoPath)
            playRawVideo(context, resourceName)
                    } else {
            // 直接播放 URL
            playVideo(videoPath)
                }
    }

    /**
     * 操作：播放视频（使用 Video 对象）
     */
    fun playVideo(video: com.example.smalldy.data.Video, context: android.content.Context) {
        playVideo(context, video.url)
                }
    
    /**
     * 操作：暂停视频
     */
    fun pause() {
        player.pause()
        _playerState.update { it.copy(isPlaying = false) }
    }

    /**
     * 操作：恢复播放
     */
    fun resume() {
        player.play()
        _playerState.update { it.copy(isPlaying = true) }
    }
    
    /**
     * 操作：更新播放器状态
     */
    fun updatePlayerState(isPlaying: Boolean, currentPosition: Long): VideoPlayerState {
        val newState = _playerState.value.copy(
            isPlaying = isPlaying,
            currentPosition = currentPosition
        )
        _playerState.value = newState
        return newState
    }
    
    /**
     * 更新播放进度（内部方法）
     */
    private fun updatePlayerPosition() {
        val currentPosition = if (player.currentPosition > 0) player.currentPosition / 1000 else 0L // 转换为秒
        _playerState.update { it.copy(currentPosition = currentPosition) }
        _uiState.update { it.copy(currentPosition = player.currentPosition) }
    }

    fun savePlaybackState() {
        isPlayerPausedForResume = player.isPlaying
        player.pause()
    }

    fun restorePlaybackState() {
        if(isPlayerPausedForResume && !player.isPlaying){
            player.play()
        }
        isPlayerPausedForResume = false
    }

    fun setFullScreenMode(isFullScreen: Boolean){
        _uiState.update {it.copy(isFullScreenMode = isFullScreen)}
    }

    fun errorShown(errorId: Long){
        _uiState.update { currentState ->
            val errorMessages = currentState.errorMessages.filterNot { it.id == errorId }
            currentState.copy(errorMessages = errorMessages)
        }
    }

    private fun updateUIForError(message: String) {
        _uiState.update { currentState ->
            val newErrorMessages = if (currentState.errorMessages.any { it.message == message}){
                currentState.errorMessages
            }else {
                currentState.errorMessages + ErrorMessage(
                    id = UUID.randomUUID().mostSignificantBits,
                    message = message
                )
            }
            currentState.copy(errorMessages = newErrorMessages)
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}