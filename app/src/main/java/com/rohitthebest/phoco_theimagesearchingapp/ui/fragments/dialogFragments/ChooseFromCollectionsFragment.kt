package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentChooseFromCollectionsBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.ChooseCollectionAdapter
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ChooseFromCollectionsFr"

@AndroidEntryPoint
class ChooseFromCollectionsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChooseFromCollectionsBinding? = null
    private val binding get() = _binding!!

    private val collectionViewModel by viewModels<CollectionViewModel>()
    private val savedImagesViewModel by viewModels<SavedImageViewModel>()

    private lateinit var chooseCollectionAdapter: ChooseCollectionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_choose_from_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChooseFromCollectionsBinding.bind(view)

        getAllSavedImages()

    }

    private fun getAllSavedImages() {

        savedImagesViewModel.getAllSavedImages().observe(viewLifecycleOwner, {

            Log.d(TAG, "getAllSavedImages: ")

            chooseCollectionAdapter = ChooseCollectionAdapter(it)
            setUpRecyclerView()

            getAllCollections()
        })
    }

    private fun setUpRecyclerView() {

        Log.d(TAG, "setUpRecyclerView: ")

        binding.chooseCollectionRV.apply {

            setHasFixedSize(true)
            adapter = chooseCollectionAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    private fun getAllCollections() {

        collectionViewModel.getAllCollection().observe(viewLifecycleOwner, {

            Log.d(TAG, "getAllCollections: ")

            chooseCollectionAdapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}