package com.example.afinal

data class Landmark(
    val id: String,
    val name: String,
    val imageUrl: String
)

data class PlacesResponse(
    val results: List<Place>
)

data class Place(
    val id: String,
    val name: String,
    val photos: List<Photo>
)

data class Photo(
    val photo_reference: String
)