package com.kemana.backend.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "order_data")
data class OrderData(
        val orderID: String?,
        val from: Places?,
        val to: Places?,
        val attribute: OrderDataAttr?,
        val active: Boolean?
)

data class OrderDataAttr(
        val driver: Driver?,
        val passenger: Passenger?
)

data class Passenger(
        var id: String? = null,
        val name: String?,
        val email: String?,
        val photoUrl: String?,
        var position: Position? = null
)