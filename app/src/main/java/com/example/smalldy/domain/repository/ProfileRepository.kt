package com.example.smalldy.domain.repository

import com.example.smalldy.domain.model.UserProfile

interface ProfileRepository {
    fun getUserProfile(): UserProfile
}
