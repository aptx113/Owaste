package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceReviews (

    val author_name: String? = "",
    val author_url: String? = "",
    val language: String? = "",
    val profile_photo_url: String? = "",
    val rating: Int = 0,
    val relative_time_description: String? = "",
    val text: String? = "",
    val time: Long? = 0L
) : Parcelable
