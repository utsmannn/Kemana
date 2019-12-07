/*
 * Copyright 2019 Muhammad Utsman
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

const val kotlin_version = "1.3.50"

object Sdk {
    val minSdk              = 16
    val targetSdk           = 28
}

object Core {
    val kotlin              = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    val appCompat           = "androidx.appcompat:appcompat:1.0.2"
    val ktx                 = "androidx.core:core-ktx:1.0.2"
    val constraint          = "androidx.constraintlayout:constraintlayout:1.1.3"
    val legacySupportV4     = "androidx.legacy:legacy-support-v4:1.0.0"
    val permission          = "com.karumi:dexter:5.0.0"
    val multidex            = "androidx.multidex:multidex:2.0.1"
    val eventBus            = "org.greenrobot:eventbus:3.1.1"
    val notify              = "com.github.isfaaghyth:notify:1.0"
}

object Firebase {
    val google_auth         = "com.google.firebase:firebase-auth:19.2.0"
    val google_auth_ui      = "com.firebaseui:firebase-ui-auth:4.3.0"
    val google_auth_helper  = "com.utsman:easygooglelogin:1.0.10"
}

object Maps {
    val mapbox              = "com.mapbox.mapboxsdk:mapbox-android-sdk:8.4.0"
    val gmsLocationService  = "com.google.android.gms:play-services-location:17.0.0"
    val rxLocation          = "pl.charmas.android:android-reactive-location2:2.1@aar"
    val smartMarker         = "com.utsman.smartmarker:core:1.2.6@aar"
    val smartMarkerMapbox   = "com.utsman.smartmarker:ext-mapbox:1.2.6@aar"
    val smartMarkerLocation = "com.utsman.smartmarker:ext-location:1.2.6@aar"
}

object Design {
    val material            = "com.google.android.material:material:1.0.0"
    val recycling           = "com.utsman.recycling:recycling:1.3.8"
    val glide               = "com.github.bumptech.glide:glide:4.10.0"
    val glideKapt           = "com.github.bumptech.glide:compiler:4.10.0"
    val slideButton         = "com.ncorti:slidetoact:0.7.0"
}

object Lifecycle {
    val lifecycle           = "androidx.lifecycle:lifecycle-extensions:2.0.0"
}

object Room {
    val room                = "androidx.room:room-runtime:2.1.0"
    val rxSupport           = "androidx.room:room-rxjava2:2.1.0"
    val compiler            = "androidx.room:room-compiler:2.1.0"
}

object Rx {
    val rxJava              = "io.reactivex.rxjava2:rxjava:2.2.9"
    val rxAndroid           = "io.reactivex.rxjava2:rxandroid:2.1.1"
    val rxBinding3          = "com.jakewharton.rxbinding3:rxbinding:3.0.0"
    val rxNetwork           = "com.github.pwittchen:reactivenetwork-rx2:3.0.2"
}

object Retrofit {
    val retrofit            = "com.squareup.retrofit2:retrofit:2.5.0"
    val gsonConverter       = "com.squareup.retrofit2:converter-gson:2.5.0"
    val rxAdapter           = "com.squareup.retrofit2:adapter-rxjava2:2.5.0"
    val loggingInterceptor  = "com.squareup.okhttp3:logging-interceptor:4.0.1"
}

object Message {
    val rmqa                = "com.utsman.rmqa:rmqa:0.0.2"
}

object Module {
    val base                = ":base"
    val feature_auth        = ":feature:auth"
    val feature_map_util    = ":feature:maputil"
    val feature_place       = ":feature:places"
    val feature_message     = ":feature:message"
    val feature_backendless = ":feature:backendless"
}