package com.example.smalldy.data.repository

import com.example.smalldy.data.local.MessagesLocalDataSource
import com.example.smalldy.domain.model.ChatMessage
import com.example.smalldy.domain.model.SystemMessage
import com.example.smalldy.domain.repository.MessagesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val localDataSource: MessagesLocalDataSource
) : MessagesRepository {
    override fun getSystemMessages(): List<SystemMessage> = localDataSource.getSystemMessages()
    override fun getChatMessages(): List<ChatMessage> = localDataSource.getChatMessages()
}
