package com.epoch.owaste

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (

    val totalPoints: Long,
    val uid: String
) : Parcelable