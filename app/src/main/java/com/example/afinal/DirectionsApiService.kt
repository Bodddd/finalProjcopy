package com.example.afinal

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getComplexRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String,
        @Query("mode") mode: String = "walking",
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}
