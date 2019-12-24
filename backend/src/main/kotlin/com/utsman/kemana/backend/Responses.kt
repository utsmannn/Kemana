package com.utsman.kemana.backend

import com.mongodb.lang.Nullable

data class Responses(
        val message: String,
        @Nullable
        val data: Any? = null
)