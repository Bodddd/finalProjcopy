package com.example.afinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.afinal.databinding.FragmentLandmarksListBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandmarksListFragment : Fragment() {

    private lateinit var binding: FragmentLandmarksListBinding
    private val viewModel: LandmarksViewModel by viewModels()

    @Inject
    lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandmarksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(requireContext())

        binding.landmarksRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LandmarksAdapter(listOf())
        binding.landmarksRecyclerView.adapter = adapter


        val autocompleteAdapter = CityAutocompleteAdapter(
            requireContext(),
            placesClient,
            viewLifecycleOwner.lifecycleScope
        )
        binding.cityInput.setAdapter(autocompleteAdapter)
        binding.cityInput.setOnItemClickListener { _, _, position, _ ->
            val selectedPrediction = autocompleteAdapter.getItem(position)
            val placeId = selectedPrediction.placeId

            val placeFields = listOf(Place.Field.LAT_LNG)
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                place.latLng?.let {
                    viewModel.loadLandmarks(it)
                }
            }.addOnFailureListener { exception ->
                Log.e("LandmarksListFragment", "Error fetching place details: ${exception.message}")
            }
            val cityName = selectedPrediction.getPrimaryText(null).toString()
            binding.cityInput.setText(cityName)
        }

        viewModel.landmarks.observe(viewLifecycleOwner) { landmarks ->
            adapter.updateData(landmarks)
        }

        viewModel.cityLatLng.observe(viewLifecycleOwner) { cityLatLng ->
            viewModel.landmarksLatLng.observe(viewLifecycleOwner) { landmarksLatLng ->
                binding.showMapButton.setOnClickListener {
                    val mapsFragment = MapsFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("cityLatLng", cityLatLng)
                            putParcelableArrayList("landmarksLatLng", ArrayList(landmarksLatLng))
                        }
                    }
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mapsFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }
}



