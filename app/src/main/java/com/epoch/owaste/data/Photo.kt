package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo (

    val photo_reference: String,
    val height: Long?,
    val width: Long?
) : Parcelable