package com.elmansidik.dicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.elmansidik.dicodingevent.data.model_repository.SettingPreferences
import com.elmansidik.dicodingevent.data.local.database.FavoriteEvent
import com.elmansidik.dicodingevent.data.model_repository.EventRepository
import com.elmansidik.dicodingevent.data.response_retrofit.response.Event
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import kotlinx.coroutines.launch

class MainViewModel(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _upcomingEvent = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvent: LiveData<List<ListEventsItem>> = _upcomingEvent
    private val _finishedEvent = MutableLiveData<List<ListEventsItem>>()
    val finishedEvent: LiveData<List<ListEventsItem>> = _finishedEvent
    private val _detailEvent = MutableLiveData<Event>()
    val detailEvent: LiveData<Event> = _detailEvent
    private val _searchEvent = MutableLiveData<List<ListEventsItem>>()
    val searchEvent: LiveData<List<ListEventsItem>> = _searchEvent
    private val _allFavoriteEvents = MutableLiveData<List<FavoriteEvent>>()
    val allFavoriteEvents: LiveData<List<FavoriteEvent>> get() = _allFavoriteEvents

    init {
        getUpcomingEvent()
        getFinishedEvent()
        getAllFavoriteEvent()
    }

    fun getUpcomingEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.fetchUpcomingEvents()
            _isLoading.value = false
            result.onSuccess {
                _upcomingEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun getFinishedEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.fetchFinishedEvents()
            _isLoading.value = false
            result.onSuccess {
                _finishedEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun getDetailEvent(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.fetchEventDetail(id)
            _isLoading.value = false
            result.onSuccess {
                _detailEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun searchEvent(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {

        }
    }

    fun insertFavoriteEvent(event: FavoriteEvent) {
        viewModelScope.launch {
            val success = eventRepository.addFavoriteEvent(event)
            if (!success) {
                _errorMessage.value = "Failed to insert favorite event"
            }
        }
    }

    fun deleteFavoriteEvent(event: FavoriteEvent) {
        viewModelScope.launch {
            val success = eventRepository.removeFavoriteEvent(event)
            if (!success) {
                _errorMessage.value = "Failed to delete favorite event"
            }
        }
    }

    fun getAllFavoriteEvent() {
        _isLoading.value = true
        eventRepository.retrieveAllFavoriteEvents().observeForever { favoriteEvents ->
            Log.d("MainViewModel", "Favorite Events: $favoriteEvents")
            _isLoading.value = false
            _allFavoriteEvents.value = favoriteEvents
            clearErrorMessage()
        }
    }

    fun getFavoriteEventById(eventId: Int): LiveData<FavoriteEvent> {
        return eventRepository.retrieveFavoriteEventById(eventId)
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getReminderSettings(): LiveData<Boolean> {
        return pref.getReminderSetting().asLiveData()
    }

    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isReminderActive)
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}