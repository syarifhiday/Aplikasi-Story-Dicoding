package com.dicoding.syarifh.storyapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.syarifh.storyapp.data.database.StoryDatabase
import com.dicoding.syarifh.storyapp.data.response.ListStoryItem
import com.dicoding.syarifh.storyapp.retrofit.ApiService

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    fun fetchStories(token: String, context: Context): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, context),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}