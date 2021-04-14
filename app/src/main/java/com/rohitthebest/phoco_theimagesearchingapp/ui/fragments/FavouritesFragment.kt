package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentFavouriteBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionsAdapter
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FavouritesFragment"

@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourite) {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var collectionAdapter: CollectionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavouriteBinding.bind(view)
        binding.addMoreCollectionsFAB.setOnClickListener {

            findNavController().navigate(R.id.action_favouritesFragment_to_addCollectionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}