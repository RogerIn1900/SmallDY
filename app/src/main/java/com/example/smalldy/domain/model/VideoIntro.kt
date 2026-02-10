package com.example.smalldy.domain.model

data class VideoIntro(
    val image: String,
    val title: String,
    val userPic: String,
    val userName: String,
    val isLiked: Boolean,
    val likeCount: Int
)
