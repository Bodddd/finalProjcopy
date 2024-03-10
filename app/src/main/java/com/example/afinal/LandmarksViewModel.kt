import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.afinal.Landmark
import com.example.afinal.PlacesApiService
import dagger.hilt.android.lifecycle.HiltViewModel

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandmarksViewModel @Inject constructor(
    private val placesApiService: PlacesApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _landmarks = MutableLiveData<List<Landmark>>()
    val landmarks: LiveData<List<Landmark>> = _landmarks

    fun loadLandmarks(location: String) {
        val apiKey = getApiKey()
        viewModelScope.launch {
            try {
                val response = placesApiService.getNearbyPlaces(
                    location = location,
                    radius = 1000,
                    type = "tourist_attraction",
                    apiKey = apiKey
                )
                if (response.isSuccessful) {
                    val places = response.body()?.results ?: emptyList()
                    val landmarks = places.map { place ->
                        Landmark(
                            id = place.id,
                            name = place.name,
                            imageUrl = place.photos?.firstOrNull()?.let { photo ->
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo.photo_reference}&key=$apiKey"
                            } ?: ""
                        )
                    }
                    _landmarks.postValue(landmarks)
                } else {
                    _landmarks.postValue(emptyList())
                }
            } catch (e: Exception) {
                _landmarks.postValue(emptyList())
                Log.e("LandmarksViewModel", "Error loading landmarks", e)
            }
        }
    }

    private fun getApiKey(): String {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        return applicationInfo.metaData.getString("com.google.android.geo.API_KEY")!!
    }
}