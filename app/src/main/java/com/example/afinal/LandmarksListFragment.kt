package com.example.afinal

import LandmarksViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.afinal.databinding.FragmentLandmarksListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandmarksListFragment : Fragment() {

    private var _binding: FragmentLandmarksListBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val landmarksViewModel: LandmarksViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLandmarksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.landmarksRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LandmarksAdapter(listOf())
        binding.landmarksRecyclerView.adapter = adapter

        sharedViewModel.selectedCity.observe(viewLifecycleOwner) { city ->
            landmarksViewModel.loadLandmarks(city)
        }

        landmarksViewModel.landmarks.observe(viewLifecycleOwner) { landmarks ->
            adapter.updateData(landmarks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
