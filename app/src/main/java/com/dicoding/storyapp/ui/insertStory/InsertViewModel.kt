package com.dicoding.storyapp.ui.insertStory
import com.dicoding.storyapp.data.entity.UserEntity
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.data.source.remote.request.NewStoryRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class   InsertViewModel(
    private val preferences: UserPreferences,
    private val repository: StoryRepository
) : ViewModel() {

    fun addNewStory(newStoryRequest: NewStoryRequest) = repository.addNewStory(newStoryRequest)

    fun getLogin() : LiveData<UserEntity> = preferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { preferences.deleteLogin() } }


}