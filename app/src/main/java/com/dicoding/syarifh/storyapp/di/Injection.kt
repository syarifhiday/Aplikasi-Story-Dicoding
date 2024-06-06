package com.dicoding.syarifh.storyapp.di

import android.content.Context
import com.dicoding.syarifh.storyapp.data.UserRepository
import com.dicoding.syarifh.storyapp.data.pref.UserPreference
import com.dicoding.syarifh.storyapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

}