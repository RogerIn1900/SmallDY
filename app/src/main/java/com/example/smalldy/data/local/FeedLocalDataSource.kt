package com.example.smalldy.data.local

import android.content.Context
import android.net.Uri
import com.example.smalldy.domain.model.FeedItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val rawImages = listOf("cat2", "cat3", "cat4", "no_stress")

    fun getFeedItems(): List<FeedItem> {
        val pkg = context.packageName
        return listOf(
            FeedItem(
                image = Uri.parse("android.resource://$pkg/raw/${rawImages[0]}").toString(),
                title = "探索城市美食之旅",
                description = "发现隐藏在城市角落的美味佳肴，每一口都是惊喜",
                author = "美食探索家",
                avatar = Uri.parse("android.resource://$pkg/raw/${rawImages[1]}").toString(),
                likes = 12345
            ),
            FeedItem(
                image = Uri.parse("android.resource://$pkg/raw/${rawImages[1]}").toString(),
                title = "周末户外运动指南",
                description = "享受阳光，拥抱自然，让身体和心灵都得到放松",
                author = "运动达人",
                avatar = Uri.parse("android.resource://$pkg/raw/${rawImages[2]}").toString(),
                likes = 8567
            ),
            FeedItem(
                image = Uri.parse("android.resource://$pkg/raw/${rawImages[2]}").toString(),
                title = "摄影技巧分享",
                description = "用镜头记录生活中的美好瞬间，捕捉每一个精彩时刻",
                author = "摄影师小王",
                avatar = Uri.parse("android.resource://$pkg/raw/${rawImages[3]}").toString(),
                likes = 23456
            ),
            FeedItem(
                image = Uri.parse("android.resource://$pkg/raw/${rawImages[3]}").toString(),
                title = "旅行日记：云南之行",
                description = "彩云之南，风景如画，感受不一样的民族风情",
                author = "旅行者",
                avatar = Uri.parse("android.resource://$pkg/raw/${rawImages[0]}").toString(),
                likes = 18900
            ),
            FeedItem(
                image = Uri.parse("android.resource://$pkg/raw/${rawImages[0]}").toString(),
                title = "科技产品评测",
                description = "最新科技产品深度体验，为你提供最真实的购买建议",
                author = "科技评测",
                avatar = Uri.parse("android.resource://$pkg/raw/${rawImages[1]}").toString(),
                likes = 5678
            )
        )
    }
}
