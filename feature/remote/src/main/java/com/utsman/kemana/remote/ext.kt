package com.utsman.kemana.remote

import com.utsman.kemana.base.loge
import com.utsman.kemana.remote.driver.*
import com.utsman.kemana.remote.place.Places
import org.json.JSONObject

fun Throwable.printThrow(state: String) {
    loge("$state failed --> ${this.localizedMessage}")
    this.printStackTrace()
}