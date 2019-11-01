package com.utsman.kemana.places

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.utsman.kemana.base.rx.RxAndroidViewModel
import com.utsman.kemana.base.ext.loge
import com.utsman.kemana.base.ext.logi
import com.utsman.recycling.extentions.NetworkState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class PlaceViewModel(application: Application) : RxAndroidViewModel(application) {

    private val retrofit = RetrofitInstance.create()
    private val liveAddress = MutableLiveData<Feature>()
    private val livePlaces = MutableLiveData<MutableList<Feature>>()
    private val liveNetwork = MutableLiveData<NetworkState>()

    private val liveRoute = MutableLiveData<Route?>()

    private var isClear = false

    private val token = "pk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazFjZXB4aDIyb3gwM2Nxajlza2c2aG8zIn0.htmYJKp9aaJnh-JhWZA85Q"


    fun getMyAddress(location: Location): LiveData<Feature> {
        val obs = retrofit.getAddress(location.longitude, location.latitude, token)
            .subscribeOn(Schedulers.io())
            .map { it.features[0] }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ address ->
                liveAddress.postValue(address)
            }, { t ->
                loge("error get address -> ${t.localizedMessage}")
            })
        disposable.add(obs)
        return liveAddress
    }

    fun getPlaces(query: String, bbox: String): LiveData<MutableList<Feature>> {
        liveNetwork.postValue(NetworkState.LOADING)
        val obs = retrofit.getPlaces(query, bbox, token)
            .subscribeOn(Schedulers.io())
            .map { it.features }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ places ->
                liveNetwork.postValue(NetworkState.LOADED)
                livePlaces.postValue(places)
            },{ t ->
                liveNetwork.postValue(NetworkState.error(t.localizedMessage))
                loge("error get query -> ${t.localizedMessage}")
            })

        disposable.add(obs)

        return livePlaces
    }

    fun getRoute(body: String): LiveData<Route?> {
        val requestBody = RequestBody.create("text/plaint".toMediaTypeOrNull(), body)
        val obs = retrofit.getRoutes(requestBody, "toll", token)
            .subscribeOn(Schedulers.io())
            .doOnNext { logi(it.code) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ route ->
                liveRoute.postValue(route)
            },{ tw ->
                loge(tw.localizedMessage)
            })

        disposable.add(obs)

        return liveRoute
    }

    fun clearGeometry() {
        liveRoute.postValue(null)
    }

    fun addGeometry() {
        isClear = false
    }

    fun getNetworkState() = liveNetwork
}