package com.michael.mystoryapplication.di

import com.michael.mystoryapplication.api.ApiConfig
import com.michael.mystoryapplication.data.StoryRepository

object Injection {
    fun provideRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}