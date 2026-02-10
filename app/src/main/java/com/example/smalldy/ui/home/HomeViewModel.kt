package com.example.smalldy.ui.home

import androidx.lifecycle.ViewModel
import com.example.smalldy.domain.repository.FeedRepository
import com.example.smalldy.ui.mapper.toUiModel
import com.example.smalldy.ui.model.FeedCardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    feedRepository: FeedRepository
) : ViewModel() {

    private val _feedItems = MutableStateFlow<List<FeedCardUiModel>>(emptyList())
    val feedItems: StateFlow<List<FeedCardUiModel>> = _feedItems.asStateFlow()

    init {
        _feedItems.value = feedRepository.getFeedItems().map { it.toUiModel() }
    }
}
