package com.utsman.kemana.driver.fragment.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ncorti.slidetoact.SlideToActView
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.driver.R
import com.utsman.kemana.driver.impl.view_state.IActiveState
import com.utsman.kemana.remote.Driver
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

                if (!orderReady) {
                    view.text = "Aktifkan Order"
                    view.outerColor = ContextCompat.getColor(context!!, R.color.colorAccent)
                    view.iconColor = ContextCompat.getColor(context!!, R.color.colorAccent)
                    iActiveState.deactiveState()
                } else {
                    view.text = "Order Aktif"
                    view.outerColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
                    view.iconColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
                    iActiveState.activeState()
                }
            }
        }

        return v
    }
}