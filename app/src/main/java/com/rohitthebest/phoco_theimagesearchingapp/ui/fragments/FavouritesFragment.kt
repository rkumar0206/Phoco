package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentFavouriteBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionsAdapter
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "FavouritesFragment"

@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourite) {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private val savedImageViewModel by viewModels<SavedImageViewModel>()
    private val collectionViewModel by viewModels<CollectionViewModel>()

    private lateinit var collectionAdapter: CollectionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavouriteBinding.bind(view)

        getAllSavedPhotosList()

        GlobalScope.launch {
            delay(250)

            withContext(Dispatchers.Main) {

                getAllCollections()
            }
        }

        binding.addMoreCollectionsFAB.setOnClickListener {

            findNavController().navigate(R.id.action_favouritesFragment_to_addCollectionFragment)
        }
    }

    private fun getAllSavedPhotosList() {

        savedImageViewModel.getAllSavedImages().observe(viewLifecycleOwner, {

            collectionAdapter = CollectionsAdapter(it)

            setUpCollectionsRecyclerView()

            if (it.isNotEmpty()) {

                val imageViewList = listOf(
                        binding.allSavedIV1,
                        binding.allSavedIV2,
                        binding.allSavedIV3,
                        binding.allSavedIV4
                )

                try {

                    for (i in imageViewList.indices) {

                        if (i < 4) {

                            Glide.with(requireContext())
                                    .load(it[i].imageUrls.small)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imageViewList[i])
                        }
                    }

                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun setUpCollectionsRecyclerView() {

        try {

            binding.collectionRV.apply {

                setHasFixedSize(true)
                adapter = collectionAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAllCollections() {

        collectionViewModel.getAllCollection().observe(viewLifecycleOwner, {

            Log.d(TAG, "getAllCollections: ")

            if (it.isNotEmpty()) {

                collectionAdapter.submitList(it)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}