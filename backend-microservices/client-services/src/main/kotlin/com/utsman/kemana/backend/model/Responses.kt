package com.utsman.kemana.backend.model

import com.mongodb.lang.Nullable

data class Responses(
        val message: String,
        @Nullable
        val data: Any? = null
)

data class CheckPort(val port: Int)