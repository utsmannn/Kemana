package com.utsman.kemana.impl

import com.utsman.feature.remote.model.Direction
import com.utsman.feature.remote.model.Order

interface NormalControlImpl {
    fun onSelectMapsPicker()
    fun onFindingOrder(order: Order)
    fun toReadyMaps(direction: Direction)
}