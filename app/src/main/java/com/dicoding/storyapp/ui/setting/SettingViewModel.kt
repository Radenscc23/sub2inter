package com.dicoding.storyapp.ui.setting
import com.dicoding.storyapp.data.entity.UserEntity
import com.dicoding.storyapp.data.source.local.datastore.SettingPreferences
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(
    private val preferences: UserPreferences,
    private val settingPref: SettingPreferences
    ) : ViewModel() {

    fun saveThemeSetting(isDarkModeActive: Boolean) { viewModelScope.launch {
            settingPref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getThemeSetting() : LiveData<Boolean> = settingPref.getThemeSetting().asLiveData()

    fun getLogin() : LiveData<UserEntity> = preferences.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { preferences.deleteLogin() } }
}