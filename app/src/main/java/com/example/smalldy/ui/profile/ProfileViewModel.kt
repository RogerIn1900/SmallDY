package com.example.smalldy.ui.profile

import androidx.lifecycle.ViewModel
import com.example.smalldy.domain.repository.ProfileRepository
import com.example.smalldy.ui.mapper.toUiModel
import com.example.smalldy.ui.model.ProfileUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<ProfileUiModel?>(null)
    val profile: StateFlow<ProfileUiModel?> = _profile.asStateFlow()

    init {
        _profile.value = profileRepository.getUserProfile().toUiModel()
    }
}
