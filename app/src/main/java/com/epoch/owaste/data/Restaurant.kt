package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Restaurants (

    val id: Long,
    val cardId: Long,
    val level: Int,
    val lat: Double,
    val lng: Double,
    val placeId: String,
    val name: String
) : Parcelable

val restaurantsList = listOf(
    Restaurants(
        id = 1,
        cardId = 1,
        level = 3,
        lat = 25.042044,
        lng = 121.564699,
        placeId = "ChIJO_MDTbmrQjQR9kY38FEBbro",
        name = "好好文化創意 We & Me Cafe"
    ),
    Restaurants(
        id = 2,
        cardId = 2,
        level = 4,
        lat = 25.042098,
        lng = 121.564179,
        placeId = "ChIJoeYaSrmrQjQRl7CUbo2Pluc",
        name = "AWESOME BURGER"
    ),
    Restaurants(
        id = 3,
        cardId = 3,
        level = 3,
        lat = 25.042336,
        lng = 121.564289,
        placeId = "ChIJPwvOSrmrQjQRHzecMU-BDAI",
        name = "白暮蛋餅先生2號店松菸"
    ),
    Restaurants(
        id = 4,
        cardId = 4,
        level = 2,
        lat = 25.042178,
        lng = 121.564455,
        placeId = "ChIJjQ-CS7mrQjQRKG0nvvZ2YFk",
        name = "韓明屋"
    ),
    Restaurants(
        id = 5,
        cardId = 5,
        level = 2,
        lat = 25.042068,
        lng = 121.563881,
        placeId = "ChIJT1xXMbmrQjQR7hDPRYjDDPk",
        name = "好咖啡拿鐵專賣店"
    ),
    Restaurants(
        id = 6,
        cardId = 6,
        level = 1,
        lat = 25.042302,
        lng = 121.564160,
        placeId = "ChIJfUEVNbmrQjQRL0PgXppg88A",
        name = "比利時咖啡"
    ),
    Restaurants(
        id = 7,
        cardId = 7,
        level = 2,
        lat = 25.042486,
        lng = 121.564393,
        placeId = "ChIJdQ9UtL6rQjQRld_ih6s9TrI",
        name = "一記水餃牛肉麵店"
    ),
    Restaurants(
        id = 8,
        cardId = 8,
        level = 5,
        lat = 25.042297,
        lng = 121.564098,
        placeId = "ChIJ9wtcNbmrQjQRH1tM-FZwOao",
        name = "吉滿屋食坊"
    ),
    Restaurants(
        id = 9,
        cardId = 9,
        level = 2,
        lat = 25.042402,
        lng = 121.564190,
        placeId = "ChIJcQtDtb6rQjQRT-zRc6G1-D4",
        name = "杜佬大手作弁當"
    ),
    Restaurants(
        id = 10,
        cardId = 10,
        level = 4,
        lat = 25.042451,
        lng = 121.564005,
        placeId = "ChIJO8LByr6rQjQRuxQVsR3LGmU",
        name = "台灣阿誠現炒菜"
    )
)