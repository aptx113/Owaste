package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (

    val exp: Long,
    val uid: String
) : Parcelable {
    constructor() : this(0, "")
}