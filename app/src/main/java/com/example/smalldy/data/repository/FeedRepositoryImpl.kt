package com.example.smalldy.data.repository

import com.example.smalldy.data.local.FeedLocalDataSource
import com.example.smalldy.domain.model.FeedItem
import com.example.smalldy.domain.repository.FeedRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val localDataSource: FeedLocalDataSource
) : FeedRepository {
    override fun getFeedItems(): List<FeedItem> = localDataSource.getFeedItems()
}
