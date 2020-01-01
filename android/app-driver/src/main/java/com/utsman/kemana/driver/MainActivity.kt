package com.utsman.kemana.driver

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SwitchCompat
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.feature.base.MAPBOX_TOKEN
import com.utsman.feature.base.RxAppCompatActivity
import com.utsman.feature.base.toast
import com.utsman.feature.remote.instance.UserInstance
import com.utsman.kemana.driver.services.DriverService
import com.utsman.kemana.driver.subscriber.OnlineUpdater
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import isfaaghyth.app.notify.Notify
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity() {

    private val homeFragment by lazy {
        HomeFragment()
    }

    private val driverService by lazy {
        Intent(this, DriverService::class.java)
    }

    private val userInstance by lazy {
        UserInstance.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPBOX_TOKEN)
        setContentView(R.layout.activity_main)
        startService(driverService)

        setSupportActionBar(toolbar)

        val pagerAdapter = MainPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragments(homeFragment)
        main_pager.adapter = pagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val switchOnlineMenu = menu.findItem(R.id.switch_online)
        val switchButton =  switchOnlineMenu?.actionView as SwitchCompat?
        switchButton?.text = "Offline"
        switchButton?.setOnCheckedChangeListener { buttonView, isChecked ->

            val onlineUpdater = OnlineUpdater(isChecked)
            Notify.send(onlineUpdater)

            buttonView.text = if (isChecked) {
                "Online"
            } else {
                "Offline"
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(driverService)

        /*userInstance.deleteUser(email, id, "driver_active")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isAddedOnline = false
                composite.clear()
            }*/
    }
}