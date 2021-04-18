package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.Constants.COLLECTION_KEY_FOR_ALL_PHOTOS
import com.rohitthebest.phoco_theimagesearchingapp.Constants.COLLECTION_WITH_SAVED_IMAGES_SELECTION_ID
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentCollectionWithSavedImagesBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionWithSavedImagesAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.itemDetailsLookUp.CollectionWithSavedImagesItemDetailsLookup
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.keyProvider.CollectionWithSavedImagesItemKeyProvider
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertListOfStringString
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show
import com.rohitthebest.phoco_theimagesearchingapp.utils.showSnackBar
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToast
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
    private lateinit var receivedCollection: Collection

    private lateinit var collectionWithSavedImagesAdapter: CollectionWithSavedImagesAdapter
    private var tracker: SelectionTracker<String>? = null

    private lateinit var backPressedDispatcherCallback: OnBackPressedCallback

    private var selectedItems: Selection<String>? = null
    var mActionMode: ActionMode? = null

    private var isRefreshEnabled = true

    private var allItemsKey = emptyList<String>()

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

        observeForIfSavedImageAddedToTheCollection()

        setUpMenuClickListener()
    }

    private fun setUpMenuClickListener() {

        val menu = binding.savedImagesToolbar.menu

        menu.findItem(R.id.menu_edit_collection)
                .setOnMenuItemClickListener {

                    //todo :  navigate to dialog fragment for editing collection
                    showToast(requireContext(), "Edit collection clicked")
                    true
                }

        menu.findItem(R.id.menu_delete_collection)
                .setOnMenuItemClickListener {

                    //todo : delete the collection after showing a confirmation message
                    showToast(requireContext(), "Delete menu button clicked")
                    true
                }
    }


    private fun getPassedArgument() {

        if (!arguments?.isEmpty!!) {

            val args = arguments?.let {

                CollectionFragmentWithSavedImagesArgs.fromBundle(it)
            }

            receivedCollectionKey = args?.collectionKey!!

            getCollectionInfo()

            getAllSavedImages()

        }
    }

    private fun getCollectionInfo() {

        if (receivedCollectionKey != COLLECTION_KEY_FOR_ALL_PHOTOS) {

            collectionViewModel.getCollectionByKey(receivedCollectionKey).observe(viewLifecycleOwner, {

                receivedCollection = it

                binding.savedImagesToolbar.title = receivedCollection.collectionName
            })
        } else {

            binding.savedImagesToolbar.title = "All photos"
        }
    }

    private fun getAllSavedImages() {

        if (receivedCollectionKey == COLLECTION_KEY_FOR_ALL_PHOTOS) {

            savedImageViewModel.getAllSavedImages().observe(viewLifecycleOwner, {

                allItemsKey = it.map { k -> k.key }

                if (isRefreshEnabled) {

                    collectionWithSavedImagesAdapter.submitList(it)
                    isRefreshEnabled = false
                }
            })
        } else {

            savedImageViewModel.getSavedImagesByCollectionKey(receivedCollectionKey)
                .observe(viewLifecycleOwner, {

                    allItemsKey = it.map { k -> k.key }

                    if (isRefreshEnabled) {

                        collectionWithSavedImagesAdapter.submitList(it)
                        isRefreshEnabled = false
                    }
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

                selectedItems = tracker?.selection

                val items = selectedItems?.size()

                items?.let {

                    if (it > 0) {

                        binding.savedImagesAppBar.hide()

                        if (mActionMode != null) {

                            mActionMode?.title = "${selectedItems?.size()} selected"
                            return
                        }

                        mActionMode = (requireActivity() as (AppCompatActivity))
                                .startSupportActionMode(mActionModeCallback)
                    } else {

                        if (mActionMode != null) {

                            mActionMode?.finish()
                        }
                    }
                }

            }
        })
    }

    private var isObservingForImageSavedInCollection = false

    private val mActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

            mode?.menuInflater?.inflate(R.menu.menu_saved_images_action_mode, menu)

            mode?.title = "${selectedItems?.size()} selected"

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {

            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {

            return when (item?.itemId) {

                R.id.menu_delete_all_selected_saved_images -> {

                    deleteAllSelectedItems()
                    true
                }

                R.id.menu_move_selected_images_to_collection -> {

                    isObservingForImageSavedInCollection = true

                    isRefreshEnabled = true

                    val selectedKeyList = selectedItems?.toList()

                    Log.d(TAG, "onActionItemClicked: selectedKeyList : $selectedKeyList")

                    val action = CollectionFragmentWithSavedImagesDirections
                            .actionCollectionFragmentWithSavedImagesToChooseFromCollectionsFragment(
                                    convertListOfStringString(selectedKeyList!!), "listOfPhotos"
                            )

                    findNavController().navigate(action)
                    true
                }

                R.id.menu_select_all_savedImages -> {

                    tracker?.setItemsSelected(allItemsKey, true)
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {

            mActionMode = null
            tracker?.clearSelection()
            binding.savedImagesAppBar.show()
        }
    }

    private fun observeForIfSavedImageAddedToTheCollection() {

        findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Boolean>(Constants.IMAGE_SAVED_TO_COLLECTION_KEY)
                ?.observe(viewLifecycleOwner, {

                    if (isObservingForImageSavedInCollection) {

                        if (it) {

                            showSnackBar(binding.root, "Images moved")

                            Log.d(TAG, "observeForCollectionAddition: value is true")

                            tracker?.clearSelection()
                        }

                        isObservingForImageSavedInCollection = false
                    }
                })

    }


    private fun deleteAllSelectedItems() {

        val keyList = selectedItems?.toList()

        Log.d(TAG, "deleteAllSelectedItems: $keyList")

        MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("All the selected images will be deleted")
                .setPositiveButton("Ok") { dialog, _ ->

                    isRefreshEnabled = true
                    keyList?.let {

                        savedImageViewModel.deleteAllByKey(it)
                        showSnackBar(binding.root, "Image(s) deleted")
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->

                    dialog.dismiss()
                }
                .create()
                .show()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backPressedDispatcherCallback =
                requireActivity().onBackPressedDispatcher.addCallback(this) {
                    // Handle the back button event

                    Log.d(TAG, "onCreate: backPressed called")

                    if (tracker?.selection?.isEmpty!!) {

                        backPressedDispatcherCallback.isEnabled = false
                        requireActivity().onBackPressed()
                    } else {

                        tracker?.clearSelection()
                        backPressedDispatcherCallback.isEnabled = false
                    }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
