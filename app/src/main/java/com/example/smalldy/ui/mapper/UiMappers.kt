package com.example.smalldy.ui.mapper

import com.example.smalldy.domain.model.ChatMessage
import com.example.smalldy.domain.model.FeedItem
import com.example.smalldy.domain.model.FriendRecommendation
import com.example.smalldy.domain.model.SystemMessage
import com.example.smalldy.domain.model.UserProfile
import com.example.smalldy.ui.model.ChatMessageUiModel
import com.example.smalldy.ui.model.FeedCardUiModel
import com.example.smalldy.ui.model.FriendRecommendUiModel
import com.example.smalldy.ui.model.ProfileUiModel
import com.example.smalldy.ui.model.SystemMessageUiModel

fun FeedItem.toUiModel() = FeedCardUiModel(
    image = image,
    title = title,
    description = description,
    author = author,
    avatar = avatar,
    formattedLikes = formatLikes(likes)
)

fun FriendRecommendation.toUiModel() = FriendRecommendUiModel(
    name = name,
    description = description,
    buttonText = buttonText
)

fun SystemMessage.toUiModel() = SystemMessageUiModel(
    title = title,
    subtitle = subtitle
)

fun ChatMessage.toUiModel() = ChatMessageUiModel(
    name = name,
    subtitle = subtitle,
    isOnline = isOnline
)

fun UserProfile.toUiModel() = ProfileUiModel(
    displayName = displayName,
    douyinId = douyinId,
    likesCount = likesCount,
    mutualFollowCount = mutualFollowCount,
    followingCount = followingCount,
    followersCount = followersCount
)

fun formatLikes(num: Int): String {
    return if (num >= 10000) {
        "${String.format("%.1f", num / 10000.0)}万"
    } else {
        num.toString()
    }
}
