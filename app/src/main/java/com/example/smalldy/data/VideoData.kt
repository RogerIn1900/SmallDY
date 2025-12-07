package com.example.smalldy.data


data class VideoData (
    val image: String,
    val title:String,
    val userPic:String,
    val userName:String,
    val isLiked: Boolean,
    val likeCount: Int
)

data class VideoPlayerUIState(
    val videoWidth: Int = 0,
    val videoHeight: Int = 0,
    val hasVideoLoaded: Boolean = false,
    val isFullScreenMode: Boolean = false,

    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val errorMessages: List<ErrorMessage> = emptyList(),
)

data class VideoPlayerUIEvent(
    val onPlayVideo: ()->Unit,
    val onPause: ()->Unit,
    val onResume: () -> Unit,

    val onEnterPictureInPictureMode: () -> Unit,
    val onSavePlaybackState: () -> Unit,
    val onRestorePlaybackState: () -> Unit,
    val onToggleFullScreenMode: (Boolean) -> Unit,
    val onErrorShown: (Long) -> Unit,
    val onShowSnackbar: suspend (String) -> Unit,
)




data class ErrorMessage(
    val id: Long,
    val message: String,
)

