package com.dicoding.syarifh.storyapp.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.syarifh.storyapp.data.database.StoryDatabase
import com.dicoding.syarifh.storyapp.data.pref.UserPreference
import com.dicoding.syarifh.storyapp.data.pref.dataStore
import com.dicoding.syarifh.storyapp.data.response.ListStoryItem
import com.dicoding.syarifh.storyapp.retrofit.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.awaitResponse

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val context: Context
) : RemoteMediator<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private val token = runBlocking {
        UserPreference.getInstance(context.dataStore).getSession().first().token
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = INITIAL_PAGE_INDEX
        try {
            val responseData = apiService.getStories(token, page, state.config.pageSize).awaitResponse().body()?.listStory
            val endOfPaginationReached = responseData?.isEmpty() ?:true

            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.storyDao().deleteAll()
                }
                database.storyDao().insertStory(responseData)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }
}