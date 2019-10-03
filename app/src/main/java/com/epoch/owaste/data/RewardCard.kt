package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RewardCard(
    val cardId: Long = 0,
    val points: Long = 0,
    val restaurantLevel: Int = 0,
    val restaurantName: String = ""
) : Parcelable