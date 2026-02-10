package com.example.smalldy.ui.messages

import androidx.lifecycle.ViewModel
import com.example.smalldy.domain.repository.MessagesRepository
import com.example.smalldy.ui.mapper.toUiModel
import com.example.smalldy.ui.model.ChatMessageUiModel
import com.example.smalldy.ui.model.SystemMessageUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    messagesRepository: MessagesRepository
) : ViewModel() {

    private val _systemMessages = MutableStateFlow<List<SystemMessageUiModel>>(emptyList())
    val systemMessages: StateFlow<List<SystemMessageUiModel>> = _systemMessages.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessageUiModel>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageUiModel>> = _chatMessages.asStateFlow()

    init {
        _systemMessages.value = messagesRepository.getSystemMessages().map { it.toUiModel() }
        _chatMessages.value = messagesRepository.getChatMessages().map { it.toUiModel() }
    }
}
