package com.utsman.kemana.maputil

import com.mapbox.mapboxsdk.geometry.LatLng

data class LatLngUpdater(val oldLatLng: LatLng,
                         val newLatLng: LatLng)