package com.utsman.kemana.fragment.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utsman.featurerabbitmq.Rabbit
import com.utsman.featurerabbitmq.Type
import com.utsman.kemana.R
import com.utsman.kemana.base.RABBIT_URL
import com.utsman.kemana.base.RxFragment
import com.utsman.kemana.presenter.MapsPresenter
import com.utsman.kemana.presenter.MessagingPresenter
import com.utsman.kemana.remote.driver.OrderData
import kotlinx.android.synthetic.main.bottom_sheet_frg_pickup.view.*
import org.json.JSONObject

class PickupBottomSheet(private val orderData: OrderData, private val messagingPresenter: MessagingPresenter) : RxFragment() {

    private val driver by lazy {
        orderData.attribute.driver
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_frg_pickup, container, false)

        v.btn_cancel.setOnClickListener {
            val jsonObject = JSONObject()
            val jsonEmpty = JSONObject()

            jsonObject.apply {
                put("type", Type.ORDER_CANCEL)
                put("data", jsonEmpty)
            }

            Rabbit.fromUrl(RABBIT_URL).publishTo(driver?.email!!, true, jsonObject)

            messagingPresenter.orderCancel()
        }

        return v
    }
}