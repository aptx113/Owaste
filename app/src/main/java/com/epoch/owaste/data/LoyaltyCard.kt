package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyCard (

    val cardId: Long,
    val points: Long
) : Parcelable