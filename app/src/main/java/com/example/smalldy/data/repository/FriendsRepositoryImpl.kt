package com.example.smalldy.data.repository

import com.example.smalldy.data.local.FriendsLocalDataSource
import com.example.smalldy.domain.model.FriendRecommendation
import com.example.smalldy.domain.repository.FriendsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsRepositoryImpl @Inject constructor(
    private val localDataSource: FriendsLocalDataSource
) : FriendsRepository {
    override fun getRecommendations(): List<FriendRecommendation> = localDataSource.getRecommendations()
}
