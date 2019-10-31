package com.utsman.kemana.auth

import com.google.gson.annotations.SerializedName

data class User(val userId: String,
                val name: String,
                val email: String,
                val password: String? = null,
                val vehiclesType: String? = "passenger",
                val vehiclesPlat: String? = "passenger",
                val photoProfile: String? = "https://usa-latestnews.com/wp-content/plugins/all-in-one-seo-pack/images/default-user-image.png",
                val objectId: String? = null,
                @SerializedName("user-token")
                var token: String? = null,
                var lat: Double? = 0.0,
                var lon: Double? = 0.0,
                var angle: Double? = 0.0,
                var onOrder: Boolean = false)