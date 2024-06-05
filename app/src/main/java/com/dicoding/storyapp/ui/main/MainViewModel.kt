package com.dicoding.storyapp.ui.main
import com.dicoding.storyapp.data.entity.UserEntity
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.source.local.datastore.SettingPreferences
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.data.source.remote.request.LoginRequest
import com.dicoding.storyapp.data.source.remote.request.RegisterRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPref: UserPreferences,
    private val settingPref: SettingPreferences,
    private val repository: UserRepository
    ) : ViewModel() {

    fun register(registerRequest: RegisterRequest) = repository.register(registerRequest)

    fun login(loginRequest: LoginRequest) = repository.login(loginRequest)

    fun setLogin(user: UserEntity) { viewModelScope.launch { userPref.setLogin(user) } }

    fun getLogin() : LiveData<UserEntity> = userPref.getLogin().asLiveData()

    fun deleteLogin() { viewModelScope.launch { userPref.deleteLogin() } }

    fun getThemeSetting() : LiveData<Boolean> = settingPref.getThemeSetting().asLiveData()
}