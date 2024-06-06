package com.dicoding.syarifh.storyapp.view.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.syarifh.storyapp.data.StoriesPagingSource
import com.dicoding.syarifh.storyapp.data.UserRepository
import com.dicoding.syarifh.storyapp.data.pref.UserModel
import com.dicoding.syarifh.storyapp.data.response.ListStoryItem
import com.dicoding.syarifh.storyapp.data.response.StoriesResponse
import com.dicoding.syarifh.storyapp.retrofit.ApiConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<PagingData<ListStoryItem>>()
    val stories: LiveData<PagingData<ListStoryItem>> = _stories

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun fetchStories(token: String, context: Context) {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = { StoriesPagingSource(ApiConfig.getApiService(context), token) }
        ).flow

        viewModelScope.launch {
            pager.collectLatest { pagingData ->
                _stories.postValue(pagingData)
            }
        }
    }

    fun fetchLocation(token: String, context: Context): LiveData<List<ListStoryItem>> {
        val storiesLiveData = MutableLiveData<List<ListStoryItem>>()
        val service = ApiConfig.getApiService(context)
        val call = service.getLocation("Bearer $token")
        call.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(call: Call<StoriesResponse>, response: Response<StoriesResponse>) {
                if (response.isSuccessful) {
                    val storiesResponse = response.body()
                    val stories = storiesResponse?.listStory?.filterNotNull() ?: emptyList()
                    storiesLiveData.postValue(stories)
                } else {
                    // Handle unsuccessful response
                    storiesLiveData.postValue(emptyList()) // Or handle differently
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                // Handle failure
                storiesLiveData.postValue(emptyList()) // Or handle differently
            }
        })
        return storiesLiveData
    }
}