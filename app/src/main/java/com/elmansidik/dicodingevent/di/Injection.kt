package com.elmansidik.dicodingevent.di

import android.content.Context
import com.elmansidik.dicodingevent.data.model_repository.SettingPreferences
import com.elmansidik.dicodingevent.data.model_repository.dataStore
import com.elmansidik.dicodingevent.data.local.database.FavoriteEventDatabase
import com.elmansidik.dicodingevent.data.model_repository.EventRepository
import com.elmansidik.dicodingevent.data.response_retrofit.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteEventDatabase.getInstance(context)
        val dao = database.getFavoriteEventDao()
        return EventRepository.getInstance(apiService, dao)
    }


    fun providePreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}