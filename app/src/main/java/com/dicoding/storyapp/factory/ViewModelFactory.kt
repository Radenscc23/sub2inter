package com.dicoding.storyapp.factory
import com.dicoding.storyapp.ui.detail.DetailViewModel
import com.dicoding.storyapp.ui.home.HomeViewModel
import com.dicoding.storyapp.ui.insertStory.InsertViewModel
import com.dicoding.storyapp.ui.main.MainViewModel
import com.dicoding.storyapp.ui.maps.MapsViewModel
import com.dicoding.storyapp.ui.setting.SettingViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.source.local.datastore.SettingPreferences
import com.dicoding.storyapp.data.source.local.datastore.UserPreferences
import com.dicoding.storyapp.di.Injection


class ViewModelFactory(
    private val userpref: UserPreferences,
    private val settingpref: SettingPreferences,
    private val userRepo: UserRepository,
    private val storyRepo: StoryRepository
    ) : NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userpref, settingpref, userRepo) as T
            }
            modelClass.isAssignableFrom(InsertViewModel::class.java) -> {
                InsertViewModel(userpref, storyRepo) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(userpref, storyRepo) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userpref, storyRepo) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(userpref, settingpref) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(userpref, storyRepo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var appInstance: ViewModelFactory? = null

        fun getInstance(context: Context) : ViewModelFactory =
            appInstance ?: synchronized(this) {
                appInstance ?: ViewModelFactory(
                    Injection.provideUserPreferences(context),
                    Injection.provideSettingPreferences(context),
                    Injection.provideUserRepository(),
                    Injection.provideStoryRepository(context)
                )
            }.also { appInstance = it }
    }

}