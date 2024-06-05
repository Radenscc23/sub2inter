package com.dicoding.storyapp.ui.maps
import com.dicoding.storyapp.data.entity.UserEntity
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MapsViewModel(
    private val preferences: UserPreferences,
    private val repository: StoryRepository
    ): ViewModel() {

    fun getLogin(): LiveData<UserEntity> = preferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { preferences.deleteLogin() } }

    fun getAllStoriesWithLocation(token: String) =
        repository.getAllStoriesWithLocation(token)
}