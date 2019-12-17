/*
 * Copyright (c) 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.kemana.driver.presenter

import com.utsman.kemana.driver.impl.presenter.OrderInterface
import com.utsman.kemana.driver.impl.view.IOrderView
import com.utsman.kemana.remote.place.Places

class OrderPresenter(private val iOrderView: IOrderView) : OrderInterface {
    override fun onPickup(places: Places?) {
        iOrderView.onPickup(places)
    }

    override fun onTake(places: Places?) {
        iOrderView.onTake(places)
    }

    override fun onArrive(places: Places?) {
        iOrderView.onArrive(places)
    }
}