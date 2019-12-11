package com.utsman.kemana.remote.place

interface PlaceListener {
    fun search(query: String, from: String, places: (List<Places?>?) -> Unit)
    fun getAddress(from: String, places: (Places?) -> Unit)
}