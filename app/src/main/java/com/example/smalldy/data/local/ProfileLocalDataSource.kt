package com.example.smalldy.data.local

import com.example.smalldy.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileLocalDataSource @Inject constructor() {

    fun getUserProfile(): UserProfile = UserProfile(
        displayName = "点击填写名字",
        douyinId = "25954785652",
        likesCount = "0",
        mutualFollowCount = "1",
        followingCount = "22",
        followersCount = "1"
    )
}
