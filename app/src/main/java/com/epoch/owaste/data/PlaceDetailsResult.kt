package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceDetailsResult (

    val result: List<PlaceDetails>? = null
) : Parcelable