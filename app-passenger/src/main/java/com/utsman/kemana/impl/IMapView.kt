package com.utsman.kemana.impl

import com.mapbox.mapboxsdk.geometry.LatLng

interface IMapView {
    fun onLocationReady(latLng: LatLng)
}