package com.utsman.kemana.backend.model

import com.utsman.kemana.backend.model.place.Places
import org.springframework.data.annotation.Id

data class Order(
        @Id
        var id: String? = null,
        var time: Long? = null,
        var driver_id: String? = null,
        var passenger_id: String? = null,
        var from: Places? = null,
        var to: Places? = null,
        var distance: Double? = null
)