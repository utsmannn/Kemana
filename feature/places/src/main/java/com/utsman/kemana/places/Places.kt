package com.utsman.kemana.places

data class Places(val features: MutableList<Feature>,
                  val attribution: String)

data class Feature(val id: String,
                   val properties: Properties,
                   val text: String,
                   val place_name: String,
                   val geometry: Geometry
)

data class Properties(val address: String)

data class Geometry(val coordinates: List<Double>)