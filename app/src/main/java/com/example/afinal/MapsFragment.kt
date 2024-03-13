package com.example.afinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var directionsApiService: DirectionsApiService

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        context?.let { ctx ->
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(ctx, R.raw.style_json))
        } ?: Log.e("MapsFragment", "Context is null, cannot set map style")

        arguments?.let { bundle ->
            val cityLatLng = bundle.getParcelable<LatLng>("cityLatLng")
            val landmarksLatLng = bundle.getParcelableArrayList<LatLng>("landmarksLatLng")

            cityLatLng?.let {
                val cityMarkerOptions = MarkerOptions()
                    .position(it)
                    .title("City")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                map.addMarker(cityMarkerOptions)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 13.5F))
            }

            landmarksLatLng?.forEach { latLng ->
                val landmarkMarkerOptions = MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                map.addMarker(landmarkMarkerOptions.position(latLng))
            }

//            landmarksLatLng?.forEach { latLng ->
//                map.addMarker(MarkerOptions().position(latLng))
//            }
        }

        val cityLatLng = arguments?.getParcelable<LatLng>("cityLatLng")
        val landmarksLatLng = arguments?.getParcelableArrayList<LatLng>("landmarksLatLng")
        val routeColor = ContextCompat.getColor(requireContext(), R.color.magenta_haze)

        lifecycleScope.launch {
            val apiKey = getString(R.string.google_maps_key)

            if (cityLatLng != null && landmarksLatLng != null && landmarksLatLng.isNotEmpty()) {
                try {
                    val origin = "${cityLatLng.latitude},${cityLatLng.longitude}"
                    val destination = origin
                    val waypoints = landmarksLatLng.joinToString(separator = "|") { latLng ->
                        "${latLng.latitude},${latLng.longitude}"
                    }

                    val response = directionsApiService.getComplexRoute(
                        origin,
                        destination,
                        waypoints,
                        "walking",
                        apiKey
                    )

                    if (response.isSuccessful) {
                        val polylinePoints =
                            response.body()?.routes?.firstOrNull()?.overview_polyline?.points
                                ?: return@launch
                        val decodedPath = PolyUtil.decode(polylinePoints)
                        withContext(Dispatchers.Main) {
                            map.addPolyline(
                                PolylineOptions().addAll(decodedPath)
                                    .color(routeColor)
                                    .width(10f)
                            )
                        }
                    } else {
                        Log.e(
                            "MapsFragment",
                            "Failed to fetch route: ${response.errorBody()?.string()}"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("MapsFragment", "Exception when fetching route", e)
                }
            }
        }
    }
}