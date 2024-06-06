package com.dicoding.syarifh.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.syarifh.storyapp.data.response.ListStoryItem
import com.dicoding.syarifh.storyapp.retrofit.ApiService
import retrofit2.awaitResponse

class StoriesPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStories("Bearer $token").awaitResponse()
            if (response.isSuccessful) {
                val storiesResponse = response.body()
                val listStory = storiesResponse?.listStory?.filterNotNull() ?: emptyList()

                LoadResult.Page(
                    data = listStory,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (listStory.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("Failed to load data: ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}