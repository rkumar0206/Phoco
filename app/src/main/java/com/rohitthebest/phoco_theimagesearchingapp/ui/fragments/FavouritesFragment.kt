package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants.COLLECTION_KEY_FOR_ALL_PHOTOS
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentFavouriteBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionsAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.fragmentsViewModels.FavouriteFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "FavouritesFragment"

@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourite), CollectionsAdapter.OnClickListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private val savedImageViewModel by viewModels<SavedImageViewModel>()
    private val collectionViewModel by viewModels<CollectionViewModel>()
    private val favouriteFragmentViewModel by viewModels<FavouriteFragmentViewModel>()

    private lateinit var collectionAdapter: CollectionsAdapter

    private var recyclerViewSate: Parcelable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavouriteBinding.bind(view)

        binding.progressBar.show()

        // run this function to delete
        collectionViewModel.deleteAllEmptyCollections()

        lifecycleScope.launch {

            delay(100)
            getAllSavedPhotosList()
        }

        binding.allPhotosCV.setOnClickListener {

            navigateToCollectionFragment(COLLECTION_KEY_FOR_ALL_PHOTOS)
        }

        getRVState()
    }

    private fun getRVState() {

        favouriteFragmentViewModel.favouriteRVState.observe(viewLifecycleOwner, { rvState ->

            recyclerViewSate = rvState
        })
    }

    private fun getAllSavedPhotosList() {

        savedImageViewModel.getAllSavedImages().observe(viewLifecycleOwner, { savedImages ->

            collectionAdapter = CollectionsAdapter(savedImages)

            setUpCollectionsRecyclerView()

            lifecycleScope.launch {

                delay(200)
                getAllCollections()
            }

            if (savedImages.isNotEmpty()) {

                val imageViewList = listOf(
                    binding.allSavedIV1,
                    binding.allSavedIV2,
                    binding.allSavedIV3,
                    binding.allSavedIV4
                )

                try {

                    for (i in imageViewList.indices) {

                        if (i < 4) {

                            setImageToImageViewUsingGlide(
                                requireContext(),
                                imageViewList[i],
                                savedImages[i].imageUrls.small,
                                {}, {}
                            )
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

            collectionAdapter.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(collection: Collection) {

        navigateToCollectionFragment(
                collectionKey = collection.key
        )
    }

    private fun navigateToCollectionFragment(collectionKey: String) {

        val action = FavouritesFragmentDirections.actionFavouritesFragmentToCollectionFragmentWithSavedImages(collectionKey)

        findNavController().navigate(action)
    }

    private fun getAllCollections() {

        collectionViewModel.getAllCollection().observe(viewLifecycleOwner, {

            Log.d(TAG, "getAllCollections: ")

            if (it.isNotEmpty()) {

                binding.collectionRV.show()
                binding.noCollectionAddedTV.hide()

                collectionAdapter.submitList(it)

                recyclerViewSate?.let { rvState ->

                    binding.collectionRV.layoutManager?.onRestoreInstanceState(rvState)
                }

            } else {

                binding.collectionRV.hide()
                binding.noCollectionAddedTV.show()

            }

            binding.progressBar.hide()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        favouriteFragmentViewModel.saveFavouriteRVState(
            binding.collectionRV.layoutManager?.onSaveInstanceState()
        )

        _binding = null
    }
}