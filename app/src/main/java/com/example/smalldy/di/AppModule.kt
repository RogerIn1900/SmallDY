package com.example.smalldy.di

import com.example.smalldy.data.repository.FeedRepositoryImpl
import com.example.smalldy.data.repository.FriendsRepositoryImpl
import com.example.smalldy.data.repository.MessagesRepositoryImpl
import com.example.smalldy.data.repository.ProfileRepositoryImpl
import com.example.smalldy.domain.repository.FeedRepository
import com.example.smalldy.domain.repository.FriendsRepository
import com.example.smalldy.domain.repository.MessagesRepository
import com.example.smalldy.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository

    @Binds
    abstract fun bindFriendsRepository(impl: FriendsRepositoryImpl): FriendsRepository

    @Binds
    abstract fun bindMessagesRepository(impl: MessagesRepositoryImpl): MessagesRepository

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
