package com.dicoding.syarifh.storyapp.retrofit

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.dicoding.syarifh.storyapp.data.pref.UserPreference
import com.dicoding.syarifh.storyapp.data.pref.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(context: Context): ApiService {
            val token = runBlocking(Dispatchers.IO) {
                UserPreference.getInstance(context.dataStore).getSession().first().token
            }
            Log.e(TAG, "Token di ApiConfig: $token")

            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}