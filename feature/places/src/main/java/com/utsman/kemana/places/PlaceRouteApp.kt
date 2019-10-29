package com.utsman.kemana.places

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.utsman.kemana.base.Key
import com.utsman.kemana.base.loge
import com.utsman.kemana.base.logi
import com.utsman.recycling.extentions.NetworkState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class PlaceRouteApp(private val disposable: CompositeDisposable) {

    private val retrofit = RetrofitInstance.create()
    private val liveAddress = MutableLiveData<Feature>()
    private val livePlaces = MutableLiveData<MutableList<Feature>>()
    private val liveNetwork = MutableLiveData<NetworkState>()

    private val liveRoute = MutableLiveData<Route?>()

    private var isClear = false
    private val token = Key.MAP_KEY

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
        val obs = retrofit.getRoutes(requestBody, "motorway", token)
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