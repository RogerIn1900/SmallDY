package com.example.smalldy.domain.repository

import com.example.smalldy.domain.model.FriendRecommendation

interface FriendsRepository {
    fun getRecommendations(): List<FriendRecommendation>
}
