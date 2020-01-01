package com.utsman.kemana.driver.control

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utsman.feature.base.*
import com.utsman.feature.remote.instance.UserInstance
import com.utsman.feature.remote.model.User
import com.utsman.kemana.driver.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.control_normal.view.*

class NormalControlFragment : RxFragment() {


    private val email by lazy {
        context?.Preferences_getEmail()
    }

    private val id by lazy {
        context?.Preferences_getId()
    }

    private val userInstance by lazy {
        UserInstance.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.control_normal, container, false)

        val observableUser = userInstance.getUser(email, id, "driver")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.data }
            .subscribe({
                setupContent(v, it)
            }, {
                it.printStackTrace()
            })

        composite.add(observableUser)

        return v
    }

    private fun setupContent(v: View, user: User) {
        v.text_name.text = user.name
        v.image_profile.loadCircleUrl(user.photo)
    }

    @SuppressLint("CheckResult")
    override fun onDestroyView() {
        super.onDestroyView()
        userInstance.deleteUser(email, id, "driver_active")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        composite.clear()
    }
}