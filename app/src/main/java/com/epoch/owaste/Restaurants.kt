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
) : Parcelable

val restaurantsList = listOf(
    Restaurants(
        id = 1,
        cardId = 1,
        level = 3,
        lat = 25.042044,
        lng = 121.564699,
        placeId = "AAA"
    ),
    Restaurants(
        id = 2,
        cardId = 2,
        level = 4,
        lat = 25.042098,
        lng = 121.564179,
        placeId = "BBB"
    ),
    Restaurants(
        id = 3,
        cardId = 3,
        level = 3,
        lat = 25.042336,
        lng = 121.564289,
        placeId = "CCC"
    ),
    Restaurants(
        id = 4,
        cardId = 4,
        level = 2,
        lat = 25.042178,
        lng = 121.564455,
        placeId = "DDD"
    ),
    Restaurants(
        id = 5,
        cardId = 5,
        level = 2,
        lat = 25.042068,
        lng = 121.563881,
        placeId = "EEE"
    ),
    Restaurants(
        id = 6,
        cardId = 6,
        level = 1,
        lat = 25.042302,
        lng = 121.564160,
        placeId = "FFF"
    )
)