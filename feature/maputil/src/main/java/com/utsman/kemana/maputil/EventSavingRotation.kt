package com.utsman.kemana.maputil

import com.utsman.kemana.auth.User

data class EventSavingRotation(val saving: Boolean,
                               val user: User)