package com.elmansidik.dicodingevent.data.model_repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.elmansidik.dicodingevent.data.local.database.FavoriteEventDao
import com.elmansidik.dicodingevent.data.local.database.FavoriteEvent
import com.elmansidik.dicodingevent.data.response_retrofit.response.Event
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.data.response_retrofit.retrofit.ApiService

class EventRepository(
    private val eventDao: FavoriteEventDao,
    private val api: ApiService
) {
    suspend fun fetchUpcomingEvents(): Result<List<ListEventsItem>> {
        return try {
            val response = api.getAllActiveEvent()
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Unable to fetch data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchFinishedEvents(): Result<List<ListEventsItem>> {
        return try {
            val response = api.getAllFinishedEvent()
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Unable to fetch data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchEventDetail(id: Int): Result<Event> {
        return try {
            val response = api.getDetailEvent(id)
            if (response.isSuccessful) {
                val event = response.body()?.event ?: throw Exception("Event not found in response")
                Result.success(event)
            } else {
                Result.failure(Exception("Unable to fetch data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavoriteEvent(event: FavoriteEvent): Boolean {
        return try {
            val result = eventDao.insertFavoriteEvent(event)
            Log.d("EventRepository", "Favorite event added: $result")
            result != -1L
        } catch (e: Exception) {
            Log.e("EventRepository", "Error adding favorite event: ${e.message}")
            false
        }
    }

    suspend fun removeFavoriteEvent(event: FavoriteEvent): Boolean {
        return try {
            eventDao.deleteFavoriteEvent(event)
            Log.d("EventRepository", "Favorite event removed: $event")
            true
        } catch (e: Exception) {
            Log.e("EventRepository", "Error removing favorite event: ${e.message}")
            false
        }
    }

    fun retrieveAllFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return eventDao.getAllFavoriteEvent()
    }

    fun retrieveFavoriteEventById(eventId: Int): LiveData<FavoriteEvent> {
        return eventDao.getFavoriteEventById(eventId)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            api: ApiService,
            eventDao: FavoriteEventDao
        ): EventRepository {
            return instance ?: synchronized(this) {
                instance ?: EventRepository(eventDao, api)
            }.also { instance = it }
        }
    }
}
