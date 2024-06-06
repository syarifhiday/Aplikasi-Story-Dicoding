package com.dicoding.syarifh.storyapp.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String
): Parcelable
