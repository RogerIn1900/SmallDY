package com.example.smalldy.ui.model

data class FeedCardUiModel(
    val image: String,
    val title: String,
    val description: String?,
    val author: String,
    val avatar: String,
    val formattedLikes: String
)
