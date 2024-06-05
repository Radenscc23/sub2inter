package com.dicoding.storyapp.data.paging
import com.dicoding.storyapp.data.entity.StoryEntity
import com.dicoding.storyapp.data.source.local.room.StoryDao
import com.dicoding.storyapp.data.source.remote.retrofit.ApiService
import androidx.paging.PagingSource
import androidx.paging.PagingState


class StoryPagingSource(
    private val service: ApiService,
    private val storyDao: StoryDao,
    private val userToken: String
    ): PagingSource<Int, StoryEntity>() {

    private companion object {
        const val PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryEntity> {
        return try {
            val position = params.key ?: PAGE_INDEX
            val responseData = service.getAllStories(
                "Bearer $userToken",
                position,
                params.loadSize
            )
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


}