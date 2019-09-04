package com.epoch.owaste

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Restaurants (

    val id: Long,
    val cardId: Long,
    val level: Int,
    val lat: Double,
    val lng: Double,
    val placeId: String
) : Parcelable {
    constructor(): this(0,0,0, 0.0,0.0,"")
}