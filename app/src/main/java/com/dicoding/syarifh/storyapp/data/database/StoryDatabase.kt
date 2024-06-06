package com.dicoding.syarifh.storyapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.syarifh.storyapp.data.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao

    companion object {

    }
}