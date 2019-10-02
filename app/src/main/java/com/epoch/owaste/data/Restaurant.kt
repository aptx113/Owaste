package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Restaurant(
    val id: Long = 0,
    val cardId: Long = 0,
    val level: Int = 0,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val placeId: String = "",
    val name: String = ""
) : Parcelable