package com.utsman.kemana.remote

import com.utsman.kemana.base.loge

fun Throwable.printThrow(state: String) {
    loge("$state failed --> ${this.localizedMessage}")
    this.printStackTrace()
}