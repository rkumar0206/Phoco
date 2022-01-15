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
import com.rohitthebest.phoco_theimagesearchingapp.Constants.IMAGE_SAVED_TO_COLLECTION_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_ACT_CHOOSE_COLLECTION_MESSAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentChooseFromCollectionsBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.ChooseCollectionAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToListOfStrings
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToSavedImage
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.isValidString
import com.rohitthebest.phoco_theimagesearchingapp.utils.show
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

    private var receivedImagesList = emptyList<SavedImage>()

    private var isArgumentReceivedFromNavigationComponent = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_choose_from_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChooseFromCollectionsBinding.bind(view)

        getPassedArgument()

        binding.addCollectionIB.setOnClickListener {

            findNavController().navigate(R.id.action_chooseFromCollectionsFragment_to_addCollectionFragment)
        }

        binding.addNewCollectionTV.setOnClickListener {

            findNavController().navigate(R.id.action_chooseFromCollectionsFragment_to_addCollectionFragment)
        }
    }

    private fun getPassedArgument() {

        if (!arguments?.isEmpty!!) {

            try {

                // argument is received from navigation component
                isArgumentReceivedFromNavigationComponent = true

                val args = arguments?.let {

                    ChooseFromCollectionsFragmentArgs.fromBundle(it)
                }

                val tag = args?.withTag

                if (tag?.isValidString()!!) {

                    // if tag is not null then it means that a list of saved images key is passed as argument

                    val listOfSavedImagesKeys = convertStringToListOfStrings(args.imageToSave!!)
                    getAllSavedImagesByKeys(listOfSavedImagesKeys)

                    Log.d(TAG, "getPassedArgument: receivedKeyList : $listOfSavedImagesKeys")

                    getAllSavedImages()
                } else {

                    receivedImageToBeSaved = convertStringToSavedImage(args.imageToSave!!)

                    getAllSavedImages()
                }

            } catch (e: IllegalArgumentException) {

                e.printStackTrace()
                Log.d(TAG, "getPassedArgument: Argument is not passed using navigation component")

                // argument is received from an activity using newInstance function

                try {

                    isArgumentReceivedFromNavigationComponent = false

                    val savedImageString =
                        arguments?.getString(PREVIEW_IMAGE_ACT_CHOOSE_COLLECTION_MESSAGE_KEY)

                    receivedImageToBeSaved =
                        savedImageString?.let { convertStringToSavedImage(it) }!!

                    getAllSavedImages()

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }
    }

    private fun getAllSavedImagesByKeys(listOfSavedImagesKeys: List<String>) {

        savedImagesViewModel.getAllSavedImagesByListOfKeys(listOfSavedImagesKeys).observe(viewLifecycleOwner, {

            receivedImagesList = it
        })
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

            if (it.isNotEmpty()) {

                binding.chooseCollectionRV.show()
                binding.addNewCollectionTV.hide()

                chooseCollectionAdapter.submitList(it)
            } else {

                binding.chooseCollectionRV.hide()
                binding.addNewCollectionTV.show()
            }
        })
    }

    override fun onCollectionClicked(collection: Collection) {


        if (receivedImagesList.isEmpty()) {

            if (receivedImageToBeSaved.collectionKey != collection.key) {

                receivedImageToBeSaved.collectionKey = collection.key

                savedImagesViewModel.insertImage(receivedImageToBeSaved)

                Log.d(
                    TAG,
                    "onCollectionClicked: Image saved to collection ${collection.collectionName}"
                )

                if (isArgumentReceivedFromNavigationComponent) {

                    //passing the value to fragment from which this bottom sheet has been called
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        IMAGE_SAVED_TO_COLLECTION_KEY,
                        true
                    )
                } else {

                    dismiss()
                }

            } else {

                Log.d(TAG, "onCollectionClicked: Image is already there in collection ${collection.collectionName}")
            }
        } else {

            receivedImagesList.forEach {

                it.collectionKey = collection.key
            }

            Log.d(TAG, "onCollectionClicked: receivedSavedImagesList : $receivedImagesList")

            savedImagesViewModel.insertImages(receivedImagesList)

            //passing the value to fragment from which this bottom sheet has been called
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                IMAGE_SAVED_TO_COLLECTION_KEY,
                true
            )

            Log.d(TAG, "onCollectionClicked: updated the key of all the received list")
        }

        collection.timestamp = System.currentTimeMillis()
        collectionViewModel.updateCollection(collection)

        dismiss()
    }

    companion object {

        @JvmStatic
        fun newInstance(bundle: Bundle): ChooseFromCollectionsFragment {

            val fragment = ChooseFromCollectionsFragment()
            fragment.arguments = bundle
            return fragment
        }
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