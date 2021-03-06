package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_TAG_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SAVED_IMAGE_TAG
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentCollectionWithSavedImagesBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.activities.PreviewImageActivity
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionWithSavedImagesAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.itemDetailsLookUp.CollectionWithSavedImagesItemDetailsLookup
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.keyProvider.CollectionWithSavedImagesItemKeyProvider
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertImageDownloadLinksAndInfoToString
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertListOfStringString
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.APIName
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.ImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "CollectionFragmentWithS"

@AndroidEntryPoint
class CollectionFragmentWithSavedImages : Fragment(R.layout.fragment_collection_with_saved_images),
    CollectionWithSavedImagesAdapter.OnClickListener {

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

    private var savedImages: List<SavedImage> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCollectionWithSavedImagesBinding.bind(view)

        binding.savedImagesToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        collectionWithSavedImagesAdapter = CollectionWithSavedImagesAdapter()

        setUpMenuClickListener()

        getPassedArgument()

        setUpRecyclerView()

        setUpTracker()

        setObserverToTheTracker()

        observeForIfSavedImageAddedToTheCollection()

    }

    private fun getPassedArgument() {

        if (!arguments?.isEmpty!!) {

            Log.d(TAG, "getPassedArgument: ")

            val args = arguments?.let {

                CollectionFragmentWithSavedImagesArgs.fromBundle(it)
            }

            receivedCollectionKey = args?.collectionKey!!

            getCollectionInfo()

            getSavedImages()

        } else {

            showToast(requireContext(), "Something went wrong...")
            requireActivity().onBackPressed()
        }
    }

    private fun getCollectionInfo() {

        if (receivedCollectionKey != COLLECTION_KEY_FOR_ALL_PHOTOS) {

            collectionViewModel.getCollectionByKey(receivedCollectionKey).observe(viewLifecycleOwner, {

                if (isRefreshEnabled) {

                    receivedCollection = it

                    Log.d(TAG, "getCollectionInfo: $receivedCollection")

                    if (!::receivedCollection.isInitialized || it == null) {

                        showToast(requireContext(), "Something went wrong...")
                        requireActivity().onBackPressed()
                    }

                    binding.savedImagesToolbar.title = receivedCollection.collectionName
                }
            })

        } else {

            binding.savedImagesToolbar.title = "All photos"

            //binding.savedImagesToolbar.menu.clear()
            //binding.savedImagesToolbar.menu.close()
            binding.savedImagesToolbar.menu.findItem(R.id.menu_edit_collection).isVisible = false
            binding.savedImagesToolbar.menu.findItem(R.id.menu_delete_collection).isVisible = false
        }
    }

    private fun getSavedImages() {

        if (receivedCollectionKey == COLLECTION_KEY_FOR_ALL_PHOTOS) {

            savedImageViewModel.getAllSavedImages().observe(viewLifecycleOwner, { savedImageList ->

                initAllItemsKeyAndSubmitListToCollectionAdapter(savedImageList)

            })
        } else {

            savedImageViewModel.getSavedImagesByCollectionKey(receivedCollectionKey)
                .observe(viewLifecycleOwner, { savedImageList ->

                    initAllItemsKeyAndSubmitListToCollectionAdapter(savedImageList)
                })
        }
    }

    private fun initAllItemsKeyAndSubmitListToCollectionAdapter(savedImageList: List<SavedImage>) {

        savedImages = savedImageList

        allItemsKey = savedImageList.map { k -> k.key }

        if (isRefreshEnabled) {

            collectionWithSavedImagesAdapter.submitList(savedImageList)
            isRefreshEnabled = false
        }
    }

    private fun setUpMenuClickListener() {

        val menu = binding.savedImagesToolbar.menu

        menu.findItem(R.id.menu_edit_collection)
            .setOnMenuItemClickListener {

                val action = CollectionFragmentWithSavedImagesDirections
                    .actionCollectionFragmentWithSavedImagesToAddCollectionFragment(
                        receivedCollectionKey
                    )

                findNavController().navigate(action)

                isRefreshEnabled = true

                true
            }

        menu.findItem(R.id.menu_delete_collection)
            .setOnMenuItemClickListener {

                deleteCollection()

                true
            }

        menu.findItem(R.id.menu_share_collection_images_as_text).setOnMenuItemClickListener {

            if (savedImages.isNotEmpty()) {

                shareAsText(
                    requireActivity(),
                    getSavedImagesLinksAsString(
                        savedImages.map { it.imageUrls.original },
                        if (receivedCollectionKey == COLLECTION_KEY_FOR_ALL_PHOTOS) null else receivedCollection
                    ),
                    "Saved images"
                )
            } else {
                showToasty(requireContext(), "No images inside this collection", ToastyType.ERROR)
            }

            true
        }
        menu.findItem(R.id.menu_share_collection_images_as_pdf).setOnMenuItemClickListener {

            if (savedImages.isNotEmpty()) {
                shareSavedImagesLinksAsPdf(
                    requireActivity(),
                    getSavedImagesLinksAsItextParagraph(
                        savedImages.map { it.imageUrls.original },
                        if (receivedCollectionKey == COLLECTION_KEY_FOR_ALL_PHOTOS) null else receivedCollection
                    )
                )
            } else {
                showToasty(requireContext(), "No images inside this collection", ToastyType.ERROR)
            }

            true
        }
        menu.findItem(R.id.menu_export_images_link).setOnMenuItemClickListener {

            if (savedImages.isNotEmpty()) {
                val fileName =
                    if (receivedCollectionKey == COLLECTION_KEY_FOR_ALL_PHOTOS) "All_Saved" else receivedCollection.collectionName

                exportSavedImagesLinkToFile(
                    requireActivity(),
                    getSavedImagesLinksAsItextParagraph(
                        savedImages.map { it.imageUrls.original },
                        if (receivedCollectionKey == COLLECTION_KEY_FOR_ALL_PHOTOS) null else receivedCollection
                    ),
                    fileName
                )

                showToast(
                    requireContext(),
                    "Document saved to /PhoneStorage/Documents/$fileName.pdf",
                    Toast.LENGTH_LONG
                )
            } else {

                showToasty(requireContext(), "No images inside this collection", ToastyType.ERROR)
            }

            true
        }
        menu.findItem(R.id.menu_export_images_image).setOnMenuItemClickListener {

            showToast(requireContext(), "menu_export_images_image")

            true
        }

    }

    private fun deleteCollection() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete this collection?")
            .setMessage("All the images inside this collection will also be deleted and cannot be retrieved again.")
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton("Ok") { dialog, _ ->

                savedImageViewModel.deleteByCollectionKey(receivedCollectionKey)
                collectionViewModel.deleteCollection(receivedCollection)

                showToasty(requireContext(), "Collection deleted", ToastyType.INFO)

                dialog.dismiss()

                requireActivity().onBackPressed()

                }
                .setNegativeButton("Cancel") { dialog, _ ->

                    dialog.dismiss()
                }
                .create()
                .show()

    }

    private fun setUpRecyclerView() {

        binding.savedImagesRV.apply {

            setHasFixedSize(true)
            adapter = collectionWithSavedImagesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        collectionWithSavedImagesAdapter.setOnClickListener(this)
    }

    override fun onItemClick(savedImage: SavedImage) {

        Log.d(TAG, "onItemClick: $savedImage")

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            savedImage.imageUrls,
            receivedCollectionKey,  /*passing collection key to previewImageActivity instead of image
                 name and next every thing will be handled in the PreviewImageActivity*/
            savedImage.imageId,
            savedImage.userInfo,
            savedImage.width,
            savedImage.height
        )

        val intent = Intent(requireContext(), PreviewImageActivity::class.java)
        intent.putExtra(
            PREVIEW_IMAGE_KEY,
            convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )
        intent.putExtra(PREVIEW_IMAGE_TAG_KEY, SAVED_IMAGE_TAG)
        startActivity(intent)
    }

    override fun onUserTextViewClicked(savedImage: SavedImage) {

        when (savedImage.apiInfo.apiName) {

            APIName.UNSPLASH -> {

                val link =
                    "https://unsplash.com/${savedImage.userInfo.userIdOrUserName}?utm_source=ImageSearchApp&utm_medium=referral"

                openLinkInBrowser(requireContext(), link)
            }

            APIName.PEXELS -> {

                openLinkInBrowser(requireContext(), savedImage.userInfo.userImageUrl)
            }

            APIName.PIXABAY -> {

                val link =
                    "https://pixabay.com/users/${savedImage.userInfo.userName}-${savedImage.userInfo.userIdOrUserName}/"
                openLinkInBrowser(requireContext(), link)
            }

            else -> {
                openLinkInBrowser(requireContext(), savedImage.imageUrls.original)
            }
        }
    }

    override fun onDownloadImageBtnClicked(savedImage: SavedImage, view: View) {

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            savedImage.imageUrls,
            savedImage.imageName,
            "",
            null
        )

        showDownloadOptionPopupMenu(
                requireActivity(),
                view,
                imageDownloadLinksAndInfo
        )
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

        Log.d(TAG, "onDestroyView: ")

        _binding = null

        //unregister listener here
        backPressedDispatcherCallback.isEnabled = false
        backPressedDispatcherCallback.remove()
    }

}
