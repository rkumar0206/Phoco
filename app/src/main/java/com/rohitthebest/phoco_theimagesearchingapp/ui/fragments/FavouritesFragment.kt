package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentFavouriteBinding
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourite) {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private val savedImageViewModel by viewModels<SavedImageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavouriteBinding.bind(view)

        getAllSavedPhotosList()
    }

    private fun getAllSavedPhotosList() {

        savedImageViewModel.getAllSavesImages().observe(viewLifecycleOwner, {

            if (it.isNotEmpty()) {

                val imageViewList = listOf(binding.allSavedIV1, binding.allSavedIV2, binding.allSavedIV3, binding.allSavedIV4)

                try {

                    for (i in 0..3) {

                        Glide.with(requireContext())
                                .load(it[i].imageUrls.small)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(imageViewList[i])
                    }

                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}