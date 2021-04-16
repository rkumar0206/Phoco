package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants.COLLECTION_WITH_SAVED_IMAGES_SELECTION_ID
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentCollectionWithSavedImagesBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionWithSavedImagesAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.itemDetailsLookUp.CollectionWithSavedImagesItemDetailsLookup
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.keyProvider.CollectionWithSavedImagesItemKeyProvider
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "CollectionFragmentWithS"

@AndroidEntryPoint
class CollectionFragmentWithSavedImages : Fragment(R.layout.fragment_collection_with_saved_images) {

    private var _binding: FragmentCollectionWithSavedImagesBinding? = null
    private val binding get() = _binding!!

    private val savedImageViewModel by viewModels<SavedImageViewModel>()
    private val collectionViewModel by viewModels<CollectionViewModel>()

    private var receivedCollectionKey = ""

    private lateinit var collectionWithSavedImagesAdapter: CollectionWithSavedImagesAdapter
    private var tracker: SelectionTracker<String>? = null

    private lateinit var backPressedDispatcherCallback: OnBackPressedCallback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCollectionWithSavedImagesBinding.bind(view)

        binding.savedImagesToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        collectionWithSavedImagesAdapter = CollectionWithSavedImagesAdapter()

        getPassedArgument()

        setUpRecyclerView()

        setUpTracker()

        setObserverToTheTracker()
    }


    private fun getPassedArgument() {

        if (!arguments?.isEmpty!!) {

            val args = arguments?.let {

                CollectionFragmentWithSavedImagesArgs.fromBundle(it)
            }

            receivedCollectionKey = args?.collectionKey!!

            getAllSavedImages()

        }
    }
    private fun getAllSavedImages() {

        if (receivedCollectionKey == "all_photos") {

            savedImageViewModel.getAllSavedImages().observe(viewLifecycleOwner, {

                collectionWithSavedImagesAdapter.submitList(it)
            })
        } else {

            savedImageViewModel.getSavedImagesByCollectionKey(receivedCollectionKey)
                .observe(viewLifecycleOwner, {

                    collectionWithSavedImagesAdapter.submitList(it)
                })
        }
    }

    private fun setUpRecyclerView() {

        binding.savedImagesRV.apply {

            setHasFixedSize(true)
            adapter = collectionWithSavedImagesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpTracker() {

        tracker = SelectionTracker.Builder(
            COLLECTION_WITH_SAVED_IMAGES_SELECTION_ID,
            binding.savedImagesRV,
            CollectionWithSavedImagesItemKeyProvider(collectionWithSavedImagesAdapter),
            CollectionWithSavedImagesItemDetailsLookup(binding.savedImagesRV),
            StorageStrategy.createStringStorage()
        )
            .withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            )
            .build()

        collectionWithSavedImagesAdapter.tracker = tracker
    }

    private fun setObserverToTheTracker() {

        tracker?.addObserver(object : SelectionTracker.SelectionObserver<String>() {

            override fun onSelectionChanged() {
                super.onSelectionChanged()

                Log.d(TAG, "onSelectionChanged: ")

                backPressedDispatcherCallback.isEnabled = true

                //set up action mode
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backPressedDispatcherCallback =
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                // Handle the back button event

                Log.d(TAG, "onCreate: backPressed called")

                tracker?.clearSelection()
                backPressedDispatcherCallback.isEnabled = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
