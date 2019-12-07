package com.utsman.kemana.remote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Driver(
    var id: String? = null,
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    var position: Position? = null,
    var attribute: Attribute? = null
) : Parcelable

@Parcelize
data class Passenger(
    var id: String? = null,
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    var position: Position? = null
) : Parcelable

@Parcelize
data class Position(
    var lat: Double? = 0.0,
    var lon: Double? = 0.0,
    var angle: Double? = 0.0
) : Parcelable

@Parcelize
data class Attribute(
    var vehiclesType: String? = "passenger",
    var vehiclesPlat: String? = "passenger",
    var onOrder: Boolean = false
) : Parcelable

data class Responses(
    val message: String,
    val data: List<Driver>? = null
)

object RemoteState {
    const val INSERT_DRIVER = 10
    const val ALL_DRIVER = 11
    const val DRIVER = 12
    const val EDIT_DRIVER = 13
    const val DELETE_DRIVER = 14
    const val STOP_EDIT_DRIVER = 15
}