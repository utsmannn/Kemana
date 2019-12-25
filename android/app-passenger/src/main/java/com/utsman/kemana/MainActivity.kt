package com.utsman.kemana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import com.utsman.feature.base.MAPKEY
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val homeFragment by lazy {
        HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, MAPKEY)
        setContentView(R.layout.activity_main)

        val pagerAdapter = MainPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragments(homeFragment)

        main_pager.adapter = pagerAdapter
    }
}