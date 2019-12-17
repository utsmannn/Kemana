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

package com.utsman.kemana.presenter

import com.utsman.kemana.impl.view.IMessagingView
import com.utsman.kemana.impl.presenter.MessagingInterface
import com.utsman.kemana.remote.place.Places
import com.utsman.kemana.remote.place.PolylineResponses

class MessagingPresenter(private val iMessagingView: IMessagingView) :
    MessagingInterface {

    override fun findDriver(startPlaces: Places, destPlaces: Places, polyline: PolylineResponses) {
        iMessagingView.findDriver(startPlaces, destPlaces, polyline)
    }

    override fun orderCancel() {
        iMessagingView.orderCancel()
    }
}