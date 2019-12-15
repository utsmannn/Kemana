package com.utsman.kemana.maps_callback

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
import com.utsman.kemana.R
import com.utsman.kemana.base.*
import com.utsman.kemana.remote.driver.OrderData
import com.utsman.kemana.remote.place.PlacePresenter
import com.utsman.smartmarker.mapbox.Marker
import com.utsman.smartmarker.mapbox.MarkerOptions
import com.utsman.smartmarker.mapbox.addMarker
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class PickupMaps(
    val context: Context?,
    val composite: CompositeDisposable,
    val orderData: OrderData,
    val ok: (mapbox: MapboxMap, marker: Marker?, cameraDisposable: Disposable) -> Unit
) : OnMapReadyCallback, BaseDisposableCompletable() {

    private val placePresenter = PlacePresenter(composite)

    private lateinit var mapbox: MapboxMap
    private lateinit var startLatLon: LatLng
    private lateinit var destLatLon: LatLng
    private lateinit var driverLatLon: LatLng

    private lateinit var style: Style

    override fun onMapReady(mapbox: MapboxMap) {
        mapbox.setStyle(Style.MAPBOX_STREETS) { style ->
            this.style = style

            this.mapbox = mapbox

            val startLat = orderData.from?.geometry?.get(0)
            val startLon = orderData.from?.geometry?.get(1)

            val destLat = orderData.to?.geometry?.get(0)
            val destLon = orderData.to?.geometry?.get(1)

            val driverLat = orderData.attribute.driver?.position?.lat
            val driverLon = orderData.attribute.driver?.position?.lon

            //logi("driver is --> ${orderData.attribute.driver?.email} -> $startLat - $startLon ---- $destLat - $destLon")

            startLatLon = LatLng(startLat!!, startLon!!)
            destLatLon = LatLng(destLat!!, destLon!!)
            driverLatLon = LatLng(driverLat!!, driverLon!!)

            val startLatLonString = "$startLat,$startLon"
            val passengerLatLonString = "$destLat,$destLon"



            placePresenter.getPolyline(startLatLonString, passengerLatLonString) {
                setupPolylineRoute(it?.geometry!!, style) {
                    mapbox.uiSettings.setLogoMargins(30, 30, 30,(context!!.dp(200)) + 30)


                    val markerOptionStart = MarkerOptions.Builder()
                        .setIcon(R.drawable.mapbox_marker_icon_default)
                        .setPosition(startLatLon)
                        .setId("me")
                        .build(context)

                    val markerOptionDest = MarkerOptions.Builder()
                        .setIcon(R.drawable.mapbox_marker_icon_default)
                        .setPosition(destLatLon)
                        .setId("destination")
                        .build(context)

                    val markerOptionDriver = MarkerOptions.Builder()
                        .setIcon(R.drawable.mapbox_marker_icon_default)
                        .setPosition(driverLatLon)
                        .setId("driver")
                        .build(context)

                    val markerStart = mapbox.addMarker(markerOptionStart).get("me")
                    val markerDestination = mapbox.addMarker(markerOptionDest).get("destination")
                    val markerDriver = mapbox.addMarker(markerOptionDriver).get("driver")

                    val latLngBounds = LatLngBounds.Builder()
                        .include(startLatLon)
                        .include(driverLatLon)
                        .build()

                    mapbox.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            latLngBounds,
                            200, 200, 200, (context.dp(200)) + 200
                        )
                    )

                    val cameraDisposable = timer(5000) {
                        val newBounds = LatLngBounds.Builder()
                            .include(markerStart?.getLatLng()!!)
                            .include(markerDriver?.getLatLng()!!)
                            .build()

                        mapbox.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                newBounds,
                                200, 200, 200, (context.dp(200)) + 200
                            )
                        )
                    }

                    ok.invoke(mapbox, markerDriver, cameraDisposable)
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

    override fun onComplete() {
        super.onComplete()
        style.layers.clear()
    }
}