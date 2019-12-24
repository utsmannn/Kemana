/*
 * Copyright (c) 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.feature.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver

interface BaseRx {
    val composite: CompositeDisposable
        get() = CompositeDisposable()
}

open class RxAppCompatActivity : AppCompatActivity(), BaseRx {

    override fun onDestroy() {
        composite.clear()
        super.onDestroy()
    }
}

open class RxFragment : Fragment(), BaseRx {

    override fun onDestroyView() {
        composite.clear()
        super.onDestroyView()
    }
}

open class RxService : Service(), BaseRx {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        composite.clear()
        super.onDestroy()
    }

}

open class BaseDisposableCompletable : DisposableCompletableObserver() {
    override fun onComplete() {
        logi("complete")
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }
}