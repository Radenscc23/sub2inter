package com.dicoding.storyapp.data.repository
import com.dicoding.storyapp.data.entity.StoryEntity
import com.dicoding.storyapp.data.paging.Mediator
import com.dicoding.storyapp.data.source.local.room.StoryDao
import com.dicoding.storyapp.data.source.local.room.StoryDatabase
import com.dicoding.storyapp.data.source.remote.request.NewStoryRequest
import com.dicoding.storyapp.data.source.remote.response.MessageResponse
import com.dicoding.storyapp.data.source.remote.retrofit.ApiService
import com.dicoding.storyapp.helper.AppExecutors
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import android.util.Log

class StoryRepository private constructor(
    private val service: ApiService,
    private val storyDatabase: StoryDatabase,
    private val dao: StoryDao,
    ) {



    fun addNewStory(newStoryRequest: NewStoryRequest) : LiveData<Result<MessageResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                if (newStoryRequest.token.isNotBlank()) {
                    val responseBody = service.addNewStory(
                        "Bearer ${newStoryRequest.token}",
                        newStoryRequest.description,
                        newStoryRequest.photo
                    )
                    emit(Result.Success(responseBody))
                } else {
                    val responseBody = service.addNewStory(
                        newStoryRequest.description,
                        newStoryRequest.photo
                    )
                    emit(Result.Success(responseBody))
                }
            } catch (e: Exception) {
                Log.d("StoryRepository", "addNewStory: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getAllStories(token: String) : LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = Mediator(storyDatabase, service, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getAllStoriesWithLocation(token: String): LiveData<Result<List<StoryEntity>>> =
        liveData {
            emit(Result.Loading)
            try {
                val responseBody = service.getAllStoriesWithLocation("Bearer $token")
                val stories = responseBody.listStory
                emit(Result.Success(stories))
            } catch (e: Exception) {
                Log.d(
                    "StoryRepository",
                    "getAllStoriesWithLocation: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
    }

    fun getDetailStory(token: String, id: String) : LiveData<Result<StoryEntity>> =
        liveData {
            var error = false
            emit(Result.Loading)
            try {
                val responseBody = service.getDetailStory("Bearer $token", id)
                emit(Result.Success(responseBody.story))
            } catch (e: Exception) {
                Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
                error = true
            } finally {
                if (error) {
                    val localData: LiveData<Result<StoryEntity>> = dao.getDetailStory(id).map {
                        Result.Success(it)
                    }
                    emitSource(localData)
                }
            }
        }

    fun getBookmarkedStories() : LiveData<List<StoryEntity>> = dao.getBookmarkedStories()

    fun setStoryBookmark(story: StoryEntity, bookmarkState: Boolean) {
        story.isBookmarked = bookmarkState
        dao.updateStory(story)
    }

    companion object {
        @Volatile
        private var appInstance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase,
            storyDao: StoryDao,
            appExecutors: AppExecutors
        ) : StoryRepository =
            appInstance ?: synchronized(this) {
                appInstance ?: StoryRepository(apiService, storyDatabase, storyDao)
            }.also { appInstance = it }
    }

}