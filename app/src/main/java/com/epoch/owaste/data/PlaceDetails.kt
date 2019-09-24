package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceDetails (

    val formatted_address: String? = "",
    val formatted_phone_number: String? = "",
    val name: String? = "",
    val place_id: String? = "",
    val rating: Float? = 0F,
    val user_ratings_total: Long = 0L,
    val reviews: List<PlaceReviews>? = null
) : Parcelable