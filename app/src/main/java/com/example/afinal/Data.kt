package com.example.afinal

import com.google.gson.annotations.SerializedName

data class Landmark(val id: String, val name: String, val imageUrl: String)
data class PlacesResponse(val results: List<Results>)
data class Results(val geometry: Geometry, val photos: List<Photos>, val name: String)
data class Geometry(val location: Location)
data class Location(val lat: Double, val lng: Double)
data class Photos(@SerializedName("photo_reference") val photoReference: String? = null)

data class DirectionsResponse(val routes: List<Route>)

data class Route(val overview_polyline: Polyline)

data class Polyline(val points: String)
