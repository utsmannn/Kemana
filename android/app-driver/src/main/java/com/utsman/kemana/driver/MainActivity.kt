package com.utsman.kemana.driver

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SwitchCompat
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.feature.base.MAPBOX_TOKEN
import com.utsman.feature.base.RxAppCompatActivity
import com.utsman.feature.base.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity() {

    private val homeFragment by lazy {
        HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPBOX_TOKEN)
        setContentView(R.layout.activity_main)

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
            toast(isChecked.toString())
            buttonView.text = if (isChecked) {
                "Online"
            } else {
                "Offline"
            }
        }
        return super.onCreateOptionsMenu(menu)
    }
}