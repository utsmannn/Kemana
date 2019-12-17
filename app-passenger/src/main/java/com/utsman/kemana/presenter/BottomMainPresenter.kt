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

import com.utsman.kemana.impl.presenter.BottomMainInterface
import com.utsman.kemana.impl.view.IBottomMainView
import com.utsman.kemana.remote.place.Places
import io.reactivex.disposables.Disposable

class BottomMainPresenter(private val iBottomMainView: IBottomMainView) : BottomMainInterface {


    override fun onSearchStartLocation(list: (List<Places?>?) -> Unit): Disposable {
        return iBottomMainView.onSearchStartLocation(list)
    }

    override fun onSearchDestLocation(list: (List<Places?>?) -> Unit): Disposable {
        return iBottomMainView.onSearchDestLocation(list)
    }

    override fun onClickOrder(startPlaces: Places, destPlaces: Places) {
        iBottomMainView.onClickOrder(startPlaces, destPlaces)
    }
}