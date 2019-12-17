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

package com.utsman.kemana.driver.fragment.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ncorti.slidetoact.SlideToActView
import com.utsman.kemana.base.*
import com.utsman.kemana.driver.R
import com.utsman.kemana.driver.impl.view_state.IActiveState
import com.utsman.kemana.driver.presenter.ActivatedStatePresenter
import io.reactivex.functions.Consumer
import isfaaghyth.app.notify.Notify
import isfaaghyth.app.notify.NotifyProvider
import kotlinx.android.synthetic.main.bottom_sheet_frg_main.view.*

class MainBottomSheet(private val activeStatePresenter: ActivatedStatePresenter) : RxFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_frg_main, container, false)
        v.slide_button.text = "Aktifkan Order"

        v.slide_button.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                activeStatePresenter.setState(!activeStatePresenter.getState())
                view.resetSlider()
                view.isReversed = activeStatePresenter.getState()

                logi("order is --> ${activeStatePresenter.getState()}")

                if (!activeStatePresenter.getState()) {
                    view.slideInActive()
                    activeStatePresenter.deactivateState()
                } else {
                    view.slideActive()
                    activeStatePresenter.activeState()
                }
            }
        }

        Notify.listen(NotifyState::class.java, NotifyProvider(), Consumer {
            when (it.state) {
                NotifyState.DRIVER_UNREADY -> {
                    logi("driver unready in fragment")
                    v.slide_button.slideInActive()
                    context?.toast("failed, cannot connect to database")
                }
                NotifyState.DRIVER_READY -> {
                    logi("driver ready in fragment")
                    v.slide_button.slideActive()
                }
            }
        })

        return v
    }

    private fun SlideToActView.slideActive() {
        text = "Order Aktif"
        outerColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
        iconColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
    }

    private fun SlideToActView.slideInActive() {
        isReversed = false
        text = "Aktifkan Order"
        outerColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        iconColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        resetSlider()
    }
}