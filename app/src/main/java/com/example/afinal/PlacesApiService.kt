package com.example.afinal

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json?")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int = 1000,
        @Query("type") type: String = "tourist_attraction",
        @Query("key") apiKey: String,
        @Query("language") language: String = "uk"
    ): Response<PlacesResponse>

}