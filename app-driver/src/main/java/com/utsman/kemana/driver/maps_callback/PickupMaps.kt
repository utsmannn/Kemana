package com.utsman.kemana.driver.maps_callback

import android.content.Context
import android.graphics.Color
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
import com.utsman.kemana.base.dp
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.kemana.base.toast
import com.utsman.kemana.driver.R
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.place.PlacePresenter
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import io.reactivex.disposables.CompositeDisposable

class PickupMaps(
    private val context: Context?,
    private val composite: CompositeDisposable,
    private val orderData: OrderData,
    val ok: (MapboxMap, Marker?) -> Unit
) : OnMapReadyCallback {

    private val placePresenter = PlacePresenter(composite)

    private lateinit var mapbox: MapboxMap
    private lateinit var startLatLon: LatLng
    private lateinit var passengerLatLon: LatLng

    override fun onMapReady(mapbox: MapboxMap) {
        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
            this.mapbox = mapbox

            val startLat = orderData.attribute.driver?.position?.lat
            val startLon = orderData.attribute.driver?.position?.lon

            val passengerLat = orderData.attribute.passenger?.position?.lat
            val passengerLon = orderData.attribute.passenger?.position?.lon

            logi("driver is --> ${orderData.attribute.driver?.email} -> $startLat - $startLon ---- $passengerLat - $passengerLon")

            startLatLon = LatLng(startLat!!, startLon!!)
            passengerLatLon = LatLng(passengerLat!!, passengerLon!!)

            val startLatLonString = "$startLat,$startLon"
            val passengerLatLonString = "$passengerLat,$passengerLon"

            val markerOptionStart = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(startLatLon)
                .setId("me")
                .build(context!!)

            val markerOptionPassenger = MarkerOptions.Builder()
                .setIcon(R.drawable.mapbox_marker_icon_default)
                .setPosition(passengerLatLon)
                .setId("passenger")
                .build(context)

            val markerStart = mapbox.addMarker(markerOptionStart).get("me")
            val markerPassenger = mapbox.addMarker(markerOptionPassenger).get("passenger")
            ok.invoke(mapbox, markerStart)

            placePresenter.getPolyline(startLatLonString, passengerLatLonString) {
                setupPolylineRoute(it?.geometry!!, style) {
                    mapbox.uiSettings.setLogoMargins(30, 30, 30,(context.dp(200)) + 30)

                    val latLngBounds = LatLngBounds.Builder()
                        .include(startLatLon)
                        .include(passengerLatLon)
                        .build()

                    mapbox.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            latLngBounds,
                            200, 200, 200, (context.dp(200)) + 200
                        )
                    )
                }
            }

        }
    }

    private fun setupPolylineRoute(geometry: String, style: Style, ok: () -> Unit) {
        val id = "source-route"

        val lineString = LineString.fromPolyline(geometry, 5)
        val featureRoute = Feature.fromGeometry(lineString)
        val sourceRoute = GeoJsonSource(id, featureRoute)

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

        ok.invoke()
    }

    fun setPaddingBottom(padding: Int) {
        /*mapbox.uiSettings.setLogoMargins(30, 30, 30,(context!!.dp(padding)) + 30)

        val latLngBounds = LatLngBounds.Builder()
            .include(startLatLon)
            .include(passengerLatLon)
            .build()

        mapbox.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                200, 200, 200, (context.dp(padding)) + 200
            )
        )*/
    }
}