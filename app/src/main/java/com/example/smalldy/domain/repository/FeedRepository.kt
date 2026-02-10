package com.example.smalldy.domain.repository

import com.example.smalldy.domain.model.FeedItem

interface FeedRepository {
    fun getFeedItems(): List<FeedItem>
}
