package com.example.afinal

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandmarksViewModel @Inject constructor(
    private val placesApiService: PlacesApiService,
    private val context: Context
) : ViewModel() {

    private val _landmarks = MutableLiveData<List<Landmark>>()
    val landmarks: LiveData<List<Landmark>> = _landmarks

    private val _cityLatLng = MutableLiveData<LatLng?>()
    val cityLatLng: LiveData<LatLng?> = _cityLatLng

    private val _landmarksLatLng = MutableLiveData<List<LatLng>>()
    val landmarksLatLng: LiveData<List<LatLng>> = _landmarksLatLng

    fun loadLandmarks(cityLatLng: LatLng?) {
        this._cityLatLng.value = cityLatLng

        cityLatLng?.let { latLng ->
            val location = "${latLng.latitude},${latLng.longitude}"
            val apiKey = getApiKey()
            Log.d("LandmarksViewModel", "Loading landmarks for location: $location")

            viewModelScope.launch {
                try {
                    val response = placesApiService.getNearbyPlaces(
                        location = location,
                        radius = 2000,
                        type = "tourist_attraction",
                        apiKey = apiKey
                    )
                    Log.d("LandmarksViewModel", "Raw API Response: ${response.body()}")

                    if (response.isSuccessful && response.body() != null) {
                        val places = response.body()!!.results
                        Log.d("LandmarksViewModel", "API Response: $places")

                        val landmarks = places.take(15).map { place ->
                            val photoReference = place.photos.firstOrNull()?.photoReference
                            val photoUrl = photoReference?.let {
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$it&key=$apiKey"
                            } ?: ""

                            Landmark(
                                id = place.name.hashCode().toString(),
                                name = place.name,
                                imageUrl = photoUrl
                            )
                        }
                        _landmarks.postValue(landmarks)

                        _landmarksLatLng.postValue(
                            places.take(15)
                                .map { LatLng(it.geometry.location.lat, it.geometry.location.lng) })
                    } else {
                        Log.e(
                            "LandmarksViewModel",
                            "Failed to fetch landmarks: ${response.errorBody()?.string()}"
                        )
                        _landmarks.postValue(emptyList())
                    }
                } catch (e: Exception) {
                    Log.e("LandmarksViewModel", "Exception when fetching landmarks", e)
                    _landmarks.postValue(emptyList())
                }
            }

        }
    }

    private fun getApiKey(): String {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        return applicationInfo.metaData.getString("com.google.android.geo.API_KEY") ?: ""
    }
}