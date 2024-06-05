package com.dicoding.storyapp.ui.home
import com.dicoding.storyapp.data.entity.StoryEntity
import com.dicoding.storyapp.data.entity.UserEntity
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn

class HomeViewModel(
    private val preferences: UserPreferences,
    private val repository: StoryRepository
    ) : ViewModel() {

    fun getLogin() : LiveData<UserEntity> = preferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { preferences.deleteLogin() } }

    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> =
        repository.getAllStories(token).cachedIn(viewModelScope)

    fun getBookmarkedStories() = repository.getBookmarkedStories()

    fun saveStory(storyEntity: StoryEntity) {
        viewModelScope.launch {
            repository.setStoryBookmark(storyEntity, true)
        }
    }

    fun deleteStory(story: StoryEntity) {
        viewModelScope.launch {
            repository.setStoryBookmark(story, false)
        }
    }
}