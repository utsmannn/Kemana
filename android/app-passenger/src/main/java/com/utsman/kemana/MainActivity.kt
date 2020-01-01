package com.utsman.kemana

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.feature.base.MAPBOX_TOKEN
import com.utsman.feature.base.logi
import com.utsman.feature.rabbitmq.Rabbit
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val homeFragment by lazy {
        HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPBOX_TOKEN)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorBg)
        }

        val pagerAdapter = MainPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragments(homeFragment)

        main_pager.adapter = pagerAdapter

        Rabbit.getInstance()?.listen { from, body ->
            logi("anjay rabbit -> $from -> $body")
        }
    }
}