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

package com.utsman.kemana.remote.place

import com.utsman.kemana.base.logi
import com.utsman.kemana.remote.printThrow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PlacePresenter(private val disposable: CompositeDisposable) : PlaceListener {
    private val placeInterface = PlaceInterface.create()

    override fun search(query: String, from: String, places: (List<Places?>?) -> Unit) {
        val action = placeInterface.search(query, from)
            .subscribeOn(Schedulers.io())
            .map { it.places }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("places result success")
                places.invoke(it)
            }, {
                it.printThrow("search failed")
            })

        disposable.add(action)
    }

    override fun getAddress(from: String, places: (Places?) -> Unit) {
        val action = placeInterface.searchAddress(from)
            .subscribeOn(Schedulers.io())
            .map { it.places?.get(0) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("get places result success")
                places.invoke(it)
            }, {
                places.invoke(null)
                it.printThrow("get places failed")
            })

        disposable.add(action)

    }

    override fun getPolyline(from: String, to: String, result: (PolylineResponses?) -> Unit) {
        val action = placeInterface.getPolyline(from, to)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("get direction success")
                result.invoke(it)
            }, {
                result.invoke(null)
                it.printThrow("get direction failed")
            })

        disposable.add(action)
    }
}