package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val exp: Long = 0L,
    val level: Int = 0,
    val uid: String = ""
) : Parcelable