package com.epoch.owaste

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

/**
 * This stores the details of a place that used to draw a marker
 */
class PlaceDetails (
    val position: LatLng,
    val title: String = "Marker",
    val snippet: String? = null,
    val icon: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(),
    val infoWindowAnchorX: Float = 0.5F,
    val infoWindowAnchorY: Float = 0F,
    val draggable: Boolean = false,
    val zIndex: Float = 0F
)
