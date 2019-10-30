package com.utsman.kemana.fragment.callback

import com.mapbox.mapboxsdk.geometry.LatLng

interface CallbackFragmentStart {
    fun fromLatLng(latLng: LatLng)
    fun toLatLng(latLng: LatLng)
}