package com.utsman.kemana.driver.fragment.bottom_sheet

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ncorti.slidetoact.SlideToActView
import com.utsman.kemana.base.*
import com.utsman.kemana.driver.R
import com.utsman.kemana.driver.impl.view_state.IActiveState
import isfaaghyth.app.notify.Notify
import kotlinx.android.synthetic.main.bottom_sheet_frg_main.view.*

class MainBottomSheet(private val iActiveState: IActiveState) : RxFragment() {

    private var orderReady = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_frg_main, container, false)
        v.slide_button.text = "Aktifkan Order"


        v.slide_button.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                orderReady = !orderReady
                view.resetSlider()
                view.isReversed = orderReady

                logi("order is --> $orderReady")

                if (!orderReady) {
                    view.slideInActive()
                    iActiveState.deactiveState()
                } else {
                    view.slideActive()
                    iActiveState.activeState()
                }
            }
        }


        Notify.listenNotifyState {
            when (it) {
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
        }

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