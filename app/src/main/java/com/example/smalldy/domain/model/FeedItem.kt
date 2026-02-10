package com.example.smalldy.domain.model

data class FeedItem(
    val image: String,
    val title: String,
    val description: String? = null,
    val author: String,
    val avatar: String,
    val likes: Int
)
