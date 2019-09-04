package com.epoch.owaste

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Restaurants (

    val id: Long,
    val cardId: Long,
    val level: Int,
    val lat: Double,
    val lng: Double,
    val placeId: String
) : Parcelable