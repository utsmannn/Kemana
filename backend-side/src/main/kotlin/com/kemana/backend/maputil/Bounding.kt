package com.kemana.backend.maputil

import kotlin.math.asin
import kotlin.math.cos

class Bounding(private val radius: Double) {
    private val R = 6378000.0
    private var latMax = 0.0
    private var latMin = 0.0

    private var lonMax = 0.0
    private var lonMin = 0.0


    fun calculate(lat: Double, lon: Double) : Bounding {
        val lonD = (asin(radius / (R * cos(Math.PI*lat/180))))*180/Math.PI
        val latD = (asin(radius / R))*180/Math.PI

        latMax = lat+latD
        latMin = lat-latD

        lonMax = lon+lonD
        lonMin = lon-lonD

        return this
    }

    fun getLatMax(): Double = latMax
    fun getLatMin(): Double = latMin
    fun getLonMax(): Double = lonMax
    fun getLonMin(): Double = lonMin
}