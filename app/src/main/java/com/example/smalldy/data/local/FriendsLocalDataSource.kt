package com.example.smalldy.data.local

import com.example.smalldy.domain.model.FriendRecommendation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsLocalDataSource @Inject constructor() {

    fun getRecommendations(): List<FriendRecommendation> = listOf(
        FriendRecommendation("火种Lewis", "可能认识的人", "关注"),
        FriendRecommendation("飞蟹蚊子", "可能认识的人", "关注"),
        FriendRecommendation("北极圈剩饭", "可能认识的人", "关注")
    )
}
