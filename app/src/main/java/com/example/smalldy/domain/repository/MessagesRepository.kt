package com.example.smalldy.domain.repository

import com.example.smalldy.domain.model.ChatMessage
import com.example.smalldy.domain.model.SystemMessage

interface MessagesRepository {
    fun getSystemMessages(): List<SystemMessage>
    fun getChatMessages(): List<ChatMessage>
}
