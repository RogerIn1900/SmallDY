package com.example.smalldy.ui.friends

import androidx.lifecycle.ViewModel
import com.example.smalldy.domain.repository.FriendsRepository
import com.example.smalldy.ui.mapper.toUiModel
import com.example.smalldy.ui.model.FriendRecommendUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    friendsRepository: FriendsRepository
) : ViewModel() {

    private val _recommendations = MutableStateFlow<List<FriendRecommendUiModel>>(emptyList())
    val recommendations: StateFlow<List<FriendRecommendUiModel>> = _recommendations.asStateFlow()

    init {
        _recommendations.value = friendsRepository.getRecommendations().map { it.toUiModel() }
    }
}
