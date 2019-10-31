package com.utsman.kemana.message

data class OrderData(val userId: String,
                     val username: String,
                     val userImg: String,
                     val fromLat: Double,
                     val fromLng: Double,
                     val toLat: Double,
                     val toLng: Double,
                     val distance: Double)