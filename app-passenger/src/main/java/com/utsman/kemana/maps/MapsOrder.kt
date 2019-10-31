package com.utsman.kemana.maps

import android.graphics.Color
import android.os.Handler
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CannotAddLayerException
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.CannotAddSourceException
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.ColorUtils
import com.utsman.kemana.R
import com.utsman.kemana.base.ProgressHelper
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.toast
import com.utsman.kemana.maputil.MarkerBuilder
import com.utsman.kemana.maputil.addMarker
import com.utsman.kemana.places.PlaceRouteApp
import com.utsman.kemana.places.Route
import io.reactivex.disposables.CompositeDisposable

class MapsOrder(
    private val activity: FragmentActivity,
    private val disposable: CompositeDisposable,
    private val onReady: (Route) -> Unit
) : OnMapReadyCallback {

    private var fromLatLng: LatLng = LatLng()
    private var toLatLng: LatLng = LatLng()
    private var paddingBottom = 0
    private val progressHelper = ProgressHelper(activity)

    private val placeRouteApp = PlaceRouteApp(disposable)

    fun setFromLatLng(fromLatLng: LatLng) {
        this.fromLatLng = fromLatLng
    }

    fun setToLatLng(toLatLng: LatLng) {
        this.toLatLng = toLatLng
    }

    fun setPaddingBottom(paddingBottom: Int) {
        this.paddingBottom = paddingBottom
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            mapboxMap.uiSettings.setLogoMargins(30, 30, 30,30)

            val bodyString = "coordinates=" +
                "${toLatLng.longitude}," +
                "${toLatLng.latitude};" +
                "${fromLatLng.longitude}," +
                "${fromLatLng.latitude}"

            progressHelper.showProgressDialog()
            placeRouteApp.getRoute(bodyString)
                .observe(activity as LifecycleOwner, Observer { route ->
                    progressHelper.hideProgressDialog()
                    mapboxMap.uiSettings.setLogoMargins(30, 30, 30, paddingBottom + 30)
                    if (route != null) {
                        onReady.invoke(route)
                        Handler().postDelayed({
                            getRouteLine(route, mapboxMap, style)
                        }, 500)
                    } else {
                        activity.toast("route cannot be found")
                    }
                })
        }
    }

    private fun getRouteLine(route: Route, mapboxMap: MapboxMap, style: Style) {
        val geometry = route.routes[0].geometry
        val id = "source-route"

        logi(route.routes[0].geometry)

        val lineString = LineString.fromPolyline(geometry, 5)
        val featureRoute = Feature.fromGeometry(lineString)
        val sourceRoute = GeoJsonSource(id, featureRoute)

        val latLngBounds = LatLngBounds.Builder()
            .include(LatLng(route.waypoints[0].location[1], route.waypoints[0].location[0]))
            .include(LatLng(route.waypoints[1].location[1], route.waypoints[1].location[0]))
            .include(fromLatLng)
            .include(toLatLng)
            .build()

        mapboxMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                200, 200, 200, paddingBottom + 200
            )
        )

        try {
            style.addSource(sourceRoute)
        } catch (e: IllegalStateException) {
            logi("tai")
        } catch (e: CannotAddSourceException) {
            loge("anjay ada source")
        }

        val lineLayer = LineLayer(id, id).apply {
            withProperties(
                PropertyFactory.lineColor(ColorUtils.colorToRgbaString(Color.parseColor("#3bb2d0"))),
                PropertyFactory.lineWidth(3f)
            )
        }

        try {
            style.addLayer(lineLayer)
        } catch (e: IllegalStateException) {
            logi("layer")
        } catch (e: CannotAddLayerException) {
            loge("anjay ada layer")
        }

        val markerBuilder = MarkerBuilder(activity, style)
        val fromMarker =
            markerBuilder.newMarker(
                "${fromLatLng.latitude}-${toLatLng.latitude}",
                fromLatLng,
                R.drawable.ic_person_location,
                true
            )
        style.addMarker(fromMarker)

        val toMarker = markerBuilder.newMarker(
            "to-${fromLatLng.latitude}-${toLatLng.latitude}",
            toLatLng,
            R.drawable.ic_dest_location,
            true
        )
        style.addMarker(toMarker)
    }
}