package com.utsman.kemana.backend.model

import com.utsman.kemana.backend.model.place.Places
import org.springframework.data.annotation.Id

data class Order(
        @Id
        var id: String? = null,
        var driver: User? = null,
        var passenger: User? = null,
        var from: Places? = null,
        var to: Places? = null,
        var price: String? = null,
        var distance: String? = null
)