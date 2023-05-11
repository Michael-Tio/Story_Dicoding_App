package com.michael.mystoryapplication.util

import com.michael.mystoryapplication.responses.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                "user $i",
                "Lorem Ipsum",
                -16.002,
                "story-FvU4u0Vp2S3PMsFg",
                -10.212
            )
            items.add(story)
        }
        return items
    }
}