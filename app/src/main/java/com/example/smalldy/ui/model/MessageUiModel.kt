package com.example.smalldy.ui.model

data class SystemMessageUiModel(
    val title: String,
    val subtitle: String
)

data class ChatMessageUiModel(
    val name: String,
    val subtitle: String,
    val isOnline: Boolean
)
