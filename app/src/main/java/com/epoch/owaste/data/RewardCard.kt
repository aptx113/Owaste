package com.epoch.owaste.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RewardCard (

    val cardId: Long,
    val points: Long
) : Parcelable {
    constructor() : this(0, 0)
}