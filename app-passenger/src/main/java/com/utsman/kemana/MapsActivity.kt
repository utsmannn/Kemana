package com.utsman.kemana

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.ProgressHelper
import com.utsman.kemana.base.RxAppCompatActivity
import com.utsman.kemana.base.collapse
import com.utsman.kemana.base.dp
import com.utsman.kemana.base.dpFloat
import com.utsman.kemana.base.expand
import com.utsman.kemana.base.hidden
import com.utsman.kemana.base.isExpand
import com.utsman.kemana.base.replaceFragment
import com.utsman.kemana.fragment.OrderBottomFragment
import com.utsman.kemana.fragment.StartBottomFragment
import com.utsman.kemana.fragment.callback.CallbackFragment
import com.utsman.kemana.fragment.callback.CallbackFragmentOrder
import com.utsman.kemana.fragment.callback.CallbackFragmentStart
import com.utsman.kemana.maps.MapsOrder
import com.utsman.kemana.maps.MapsStart
import com.utsman.kemana.maputil.getLocation
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.bottom_sheet.*

class MapsActivity : RxAppCompatActivity() {

    private lateinit var bottomStart: StartBottomFragment
    private lateinit var bottomOrder: OrderBottomFragment
    private var fromLatLng: LatLng? = null
    private var toLatLng: LatLng? = null
    private var onOrder = false

    private val progressHelper by lazy { ProgressHelper(this) }

    private val callbackFragment = object : CallbackFragment {
        override fun onCollapse() {
            bottomSheetLayout.collapse()
        }

        override fun onExpand() {
            bottomSheetLayout.expand()
        }

        override fun onHidden() {
            bottomSheetLayout.hidden()
        }

        override fun onOrder(order: Boolean) {
            onOrder = order
            map_view.invalidate()

            if (fromLatLng != null && toLatLng != null) {

                val mapsOrder = MapsOrder(this@MapsActivity, compositeDisposable) { route ->
                    bottomSheetLayout.peekHeight = dp(280)
                    if (fromLatLng != null && toLatLng != null) {
                        bottomOrder.setFromLatLng(fromLatLng!!)
                        bottomOrder.setToLatLng(toLatLng!!)
                        bottomOrder.setDistance(route.routes[0].distance)
                    }
                    replaceFragment(bottomOrder, R.id.main_frame_bottom)
                }
                mapsOrder.setFromLatLng(fromLatLng!!)
                mapsOrder.setToLatLng(toLatLng!!)
                mapsOrder.setPaddingBottom(dp(280))

                Handler().postDelayed({
                    map_view.getMapAsync(mapsOrder)
                }, 200)
            }
        }
    }

    private val callbackFragmentStart = object : CallbackFragmentStart {
        override fun fromLatLng(latLng: LatLng) {
            fromLatLng = latLng
        }

        override fun toLatLng(latLng: LatLng) {
            toLatLng = latLng
        }
    }

    private val callbackFragmentOrder = object : CallbackFragmentOrder {
        override fun onBackPress() {
            onBackPressed()
        }
    }

    private val bottomSheetLayout by lazy {
        BottomSheetBehavior.from(main_bottom_sheet) as BottomSheetBehavior<*>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, Key.MAP_KEY)
        setContentView(R.layout.activity_map)
        map_view.onCreate(savedInstanceState)

        bottomStart = StartBottomFragment(callbackFragment, callbackFragmentStart)
        bottomOrder = OrderBottomFragment(callbackFragment, callbackFragmentOrder)
        userStarted()

        bottomSheetLayout.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, slide: Float) {

                if (slide == 1f) {
                    main_bottom_sheet.radius = dpFloat(0)
                } else {
                    main_bottom_sheet.radius = dpFloat(16)
                }
            }

            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED && bottomStart.isVisible) {
                    bottomStart.clearFocus()
                }
            }
        })
    }

    private fun userStarted() {
        bottomSheetLayout.hidden()
        val mapStart = MapsStart(this, compositeDisposable) {
            bottomSheetLayout.peekHeight = dp(200)
            onOrder = false
            replaceFragment(bottomStart, R.id.main_frame_bottom)
        }
        mapStart.setPaddingBottom(dp(200))
        progressHelper.showProgressDialog()

        if (fromLatLng != null && toLatLng != null) {
            progressHelper.hideProgressDialog()
            map_view.getMapAsync(mapStart)
            mapStart.setCurrentLatLng(fromLatLng!!)
            bottomStart.setCurrentLatLng(fromLatLng!!)
        } else {
            getLocation(compositeDisposable, false) { latLng ->
                progressHelper.hideProgressDialog()
                map_view.getMapAsync(mapStart)
                mapStart.setCurrentLatLng(latLng)
                bottomStart.setCurrentLatLng(latLng)
            }
        }
    }

    override fun onBackPressed() {
        when {
            onOrder -> userStarted()
            bottomSheetLayout.isExpand() -> bottomSheetLayout.collapse()
            else -> super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }
}