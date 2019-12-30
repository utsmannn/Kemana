package com.utsman.kemana

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("test", edit_test.text.toString())
        setResult(Activity.RESULT_OK)
        finish()
    }
}