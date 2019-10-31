package com.utsman.kemana

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.utsman.kemana.auth.User
import com.utsman.kemana.backendless.BackendlessApp
import com.utsman.kemana.message.OrderData
import com.utsman.kemana.message.toJSONObject
import com.utsman.rmqa.Rmqa
import com.utsman.rmqa.RmqaConnection
import io.reactivex.disposables.CompositeDisposable

class OrderFinding(private val activity: FragmentActivity, disposable: CompositeDisposable) {

    private val backendlessApp = BackendlessApp(activity.application, disposable)
    private var rmqaConnection: RmqaConnection? = null
    private val liveStatusFinding = MutableLiveData<Boolean>()
    private val liveSize = MutableLiveData<Int>()
    private var driverPriority = 0


    fun setRmqa(rmqaConnection: RmqaConnection?) {
        this.rmqaConnection = rmqaConnection
    }

    fun startFinding(orderData: OrderData, liveOrderData: MutableLiveData<User>) {
        liveStatusFinding.postValue(true)
        var position = 0
        backendlessApp.getDriversList().observe(activity as LifecycleOwner, Observer { users ->
            if (!users.isNullOrEmpty() && driverPriority <= users.size-1) {
                finder(users, orderData, position)
            }

            /*if (!users.isNullOrEmpty() && driverPriority <= users.size-1) {
                finder(users, orderData)
            }*/

            liveOrderData.observe(activity, Observer {  user ->
                if (user.onOrder) {
                    liveStatusFinding.postValue(false)
                } else {
                    liveStatusFinding.postValue(true)
                }
            })

            liveStatusFinding.observe(activity, Observer { find ->
                //var position = 0
                if (find) {
                    finder(users, orderData, position)
                } else if (!users.isNullOrEmpty() && driverPriority <= users.size-1) {
                    position++
                    finder(users, orderData, position)
                } else {
                    liveStatusFinding.postValue(false)
                }
            })

            /*liveOrderData.observe(activity as LifecycleOwner, Observer { user ->
                if (user.onOrder) {
                    activity.toast("found --> ${user.name}")
                    liveStatusFinding.postValue(false)
                } else {
                    if (!users.isNullOrEmpty() && driverPriority <= users.size-1) {
                        liveStatusFinding.postValue(true)
                        activity.toast("reject 1")
                        driverPriority+1
                        finder(users, orderData)
                    }
                }
            })*/

        })

    }

   /* private fun finder(users: List<User>, orderData: OrderData) {
        if (!users.isNullOrEmpty()) {
            liveSize.postValue(users.size)
            liveStatusFinding.postValue(true)

            if (driverPriority <= users.size - 1) {
                Rmqa.publishTo(users[driverPriority].userId, orderData.userId, orderData.toJSONObject())
            }
        } else {
            activity.toast("user null")
            liveSize.postValue(0)
            liveStatusFinding.postValue(false)
        }
    }*/

    private fun finder(users: List<User>, orderData: OrderData, position: Int) {
        val driverCallback = MutableLiveData<User>()
        Rmqa.publishTo(users[position].userId, orderData.userId, orderData.toJSONObject())
    }

    fun getStatusFinding() = liveStatusFinding


}