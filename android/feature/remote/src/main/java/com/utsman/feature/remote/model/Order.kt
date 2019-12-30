package com.utsman.feature.remote.model

import com.google.gson.annotations.SerializedName

data class Order(
    var id: String? = null,
    var time: String? = null,
    @SerializedName("driver_id")
    var driverId: String? = null,
    @SerializedName("passenger_id")
    var passengerId: String? = null,
    var from: Place? = null,
    var to: Place? = null,
    var distance: Double? = null
)

fun orderData(data: Order.() -> Unit): Order {
    val order = Order()
    order.apply(data)
    return order
}