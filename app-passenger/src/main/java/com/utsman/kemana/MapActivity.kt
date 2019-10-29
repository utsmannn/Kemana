package com.utsman.kemana

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.RxAppCompatActivity
import com.utsman.kemana.base.collapse
import com.utsman.kemana.base.dpToInt
import com.utsman.kemana.base.expand
import com.utsman.kemana.base.hidden
import com.utsman.kemana.base.isExpand
import com.utsman.kemana.base.replaceFragment
import com.utsman.kemana.callback.CallbackFragment
import com.utsman.kemana.callback.CallbackFragmentStart
import com.utsman.kemana.callback.MapReady
import com.utsman.kemana.fragment.SheetStartFragment
import com.utsman.kemana.map.MapOrder
import com.utsman.kemana.map.MapStart
import com.utsman.kemana.maputil.getLocation
import com.utsman.kemana.maputil.toLatlng
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.bottom_sheet.*

class MapActivity : RxAppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var sheetStartFragment: SheetStartFragment
    private var fromLatLng: LatLng? = null
    private var toLatLng: LatLng? = null
    private var onOrder = false

    private val callbackFragment = object : CallbackFragment {
        override fun onOrder() {
            onOrder = true

            sheetStartFragment.isShowingPricing(true)
            if (fromLatLng != null && toLatLng != null) {
                val mapsOrder = MapOrder(this@MapActivity, compositeDisposable)
                mapsOrder.setFromLatLng(fromLatLng!!)
                mapsOrder.setToLatLng(toLatLng!!)
                mapsOrder.setPaddingBottom(dpToInt(200))
                sheetStartFragment.isShowingPricing(true)

                Handler().postDelayed({
                    map_view.getMapAsync(mapsOrder)
                }, 200)
            }
        }

        override fun onCollapse() {
            sheetStartFragment.clearFocus()
            bottomSheetBehavior.collapse()

            Handler().postDelayed({
                if (onOrder) {
                    bottomSheetBehavior.peekHeight = dpToInt(250)
                } else {
                    bottomSheetBehavior.peekHeight = dpToInt(200)
                }
            }, 500)


        }

        override fun onExpand() {
            bottomSheetBehavior.expand()
            sheetStartFragment.isShowingPricing(false)
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

    private val mapsStart = MapStart(this, object : MapReady {
        override fun onMapReady(latLng: LatLng) {
            replaceFragment(sheetStartFragment, R.id.main_frame_bottom)
            bottomSheetBehavior.collapse()
        }
    })

    private val mapsOrder = MapOrder(this, compositeDisposable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, Key.MAP_KEY)
        setContentView(R.layout.activity_map)

        bottomSheetBehavior = BottomSheetBehavior.from(main_bottom_sheet)
        bottomSheetBehavior.hidden()

        sheetStartFragment = SheetStartFragment(callbackFragment, callbackFragmentStart)

        getLocation(compositeDisposable, false) { loc ->
            fromLatLng = loc.toLatlng()
            mapsStart.setCurrentLatLng(loc.toLatlng())
            mapsStart.setPaddingBottom(dpToInt(200))
            sheetStartFragment.setCurrentLatLng(loc.toLatlng())
            map_view.getMapAsync(mapsStart)
        }
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.isExpand()) {
            callbackFragment.onCollapse()
        }

        if (onOrder) {
            onOrder = false
            callbackFragment.onCollapse()
        }

        else {
            super.onBackPressed()
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