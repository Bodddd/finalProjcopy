package com.example.afinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.afinal.databinding.FragmentCityInputBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityInputFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var binding: FragmentCityInputBinding
    private val placesClient: PlacesClient by lazy {
        Places.createClient(requireContext())
    }

    fun onCitySelected(city: String) {
        sharedViewModel.selectCity(city)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCityInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val autocompleteAdapter = CityAutocompleteAdapter(requireContext(), placesClient, viewLifecycleOwner.lifecycleScope)
        binding.cityInput.setAdapter(autocompleteAdapter)
        binding.cityInput.setOnItemClickListener { _, _, position, _ ->
            val selectedPrediction = autocompleteAdapter.getItem(position)
            binding.cityInput.setText(selectedPrediction.getPrimaryText(null).toString(), false)
        }
    }

}