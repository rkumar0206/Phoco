package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentChooseFromCollectionsBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.ChooseCollectionAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToSavedImage
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToasty
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ChooseFromCollectionsFr"

@AndroidEntryPoint
class ChooseFromCollectionsFragment : BottomSheetDialogFragment(), ChooseCollectionAdapter.OnClickListener {

    private var _binding: FragmentChooseFromCollectionsBinding? = null
    private val binding get() = _binding!!

    private val collectionViewModel by viewModels<CollectionViewModel>()
    private val savedImagesViewModel by viewModels<SavedImageViewModel>()

    private lateinit var chooseCollectionAdapter: ChooseCollectionAdapter

    private lateinit var receivedImageToBeSaved: SavedImage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_choose_from_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChooseFromCollectionsBinding.bind(view)

        getPassedArgument()

        binding.addCollectionIB.setOnClickListener {

            findNavController().navigate(R.id.action_chooseFromCollectionsFragment_to_addCollectionFragment)
        }
    }

    private fun getPassedArgument() {

        if (!arguments?.isEmpty!!) {

            val args = arguments?.let {

                ChooseFromCollectionsFragmentArgs.fromBundle(it)
            }

            receivedImageToBeSaved = convertStringToSavedImage(args?.imageToSave!!)

            getAllSavedImages()
        }
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

        chooseCollectionAdapter.setOnClickListener(this)
    }

    private fun getAllCollections() {

        collectionViewModel.getAllCollection().observe(viewLifecycleOwner, {

            Log.d(TAG, "getAllCollections: ")

            chooseCollectionAdapter.submitList(it)
        })
    }

    override fun onCollectionClicked(collection: Collection) {

        receivedImageToBeSaved.collectionKey = collection.key

        savedImagesViewModel.insertImage(receivedImageToBeSaved)

        showToasty(requireContext(), "Saved to ${collection.collectionName}")

        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        Log.d(TAG, "onDismiss: ")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}