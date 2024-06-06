package com.dicoding.syarifh.storyapp.data

import com.dicoding.syarifh.storyapp.data.response.ListStoryItem

object DataDummy {
    fun getFakeListStory(): List<ListStoryItem> {
        val dummyList = ArrayList<ListStoryItem>()

        dummyList.add(
            ListStoryItem(
                photoUrl = "https://example.com/photo1.jpg",
                createdAt = "2024-06-05T06:41:10.912Z",
                name = "Story 1",
                description = "This is story 1",
                lon = 123.456,
                id = "story-123456",
                lat = 78.901
            )
        )
        dummyList.add(
            ListStoryItem(
                photoUrl = "https://example.com/photo2.jpg",
                createdAt = "2024-06-05T06:42:20.123Z",
                name = "Story 2",
                description = "This is story 2",
                lon = 12.345,
                id = "story-789012",
                lat = 45.678
            )
        )

        return dummyList
    }



}
