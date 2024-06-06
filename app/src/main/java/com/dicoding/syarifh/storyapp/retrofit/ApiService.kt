package com.dicoding.syarifh.storyapp.retrofit

import com.dicoding.syarifh.storyapp.data.response.CreateResponse
import com.dicoding.syarifh.storyapp.data.response.LoginResponse
import com.dicoding.syarifh.storyapp.data.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<CreateResponse>

    @Multipart
    @POST("stories")
    fun upload(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<CreateResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): Call<StoriesResponse>

    @GET("stories?location=1")
    fun getLocation(
        @Header("Authorization") token: String
    ): Call<StoriesResponse>

}


