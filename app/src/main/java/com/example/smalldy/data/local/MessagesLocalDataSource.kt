package com.example.smalldy.data.local

import com.example.smalldy.domain.model.ChatMessage
import com.example.smalldy.domain.model.SystemMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesLocalDataSource @Inject constructor() {

    fun getSystemMessages(): List<SystemMessage> = listOf(
        SystemMessage("新关注我的", "没有新通知"),
        SystemMessage("互动消息", "没有新通知")
    )

    fun getChatMessages(): List<ChatMessage> = listOf(
        ChatMessage(
            name = "（彭绮雯） conflict \uD83D\uDC2C\uD83D\uDC2C\uD83D\uDC2C",
            subtitle = "昨天在线",
            isOnline = false
        )
    )
}
