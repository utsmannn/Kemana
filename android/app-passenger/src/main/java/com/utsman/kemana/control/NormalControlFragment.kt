package com.utsman.kemana.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utsman.feature.base.HERE_API_KEY
import com.utsman.feature.base.RxFragment
import com.utsman.feature.remote.instance.PlaceInstance
import com.utsman.feature.remote.model.Place
import com.utsman.kemana.R
import com.utsman.smartmarker.location.LocationWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.control_main.view.*

class NormalControlFragment : RxFragment() {

    private val locationWatcher by lazy {
        LocationWatcher(context)
    }

    private val placeInstance = PlaceInstance.create()

    private var place: Place? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.control_main, container, false)
        locationWatcher.getLocation { location ->
            val from = listOf(location.latitude, location.longitude)
            val observablePlace = placeInstance.getCurrentPlace(from, HERE_API_KEY)
                .subscribeOn(Schedulers.io())
                .map { it.places[0] }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    place = it

                    v.text_from.text = place?.placeName
                }, {
                    it.printStackTrace()
                })

            composite.add(observablePlace)
        }



        return v
    }
}