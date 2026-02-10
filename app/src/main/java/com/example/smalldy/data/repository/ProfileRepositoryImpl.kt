package com.example.smalldy.data.repository

import com.example.smalldy.data.local.ProfileLocalDataSource
import com.example.smalldy.domain.model.UserProfile
import com.example.smalldy.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val localDataSource: ProfileLocalDataSource
) : ProfileRepository {
    override fun getUserProfile(): UserProfile = localDataSource.getUserProfile()
}
