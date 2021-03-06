package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants.HOME_FRAGMENT_TAG
import com.rohitthebest.phoco_theimagesearchingapp.Constants.IMAGE_SAVED_TO_COLLECTION_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_TAG_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.UserInfo
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentHomeBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.ui.activities.PreviewImageActivity
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.HomeRVAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertImageDownloadLinksAndInfoToString
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertSavedImageToString
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.APIName
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.ImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.UnsplashViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.UnsplashPhotoViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.fragmentsViewModels.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), HomeRVAdapter.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()
    private val unsplashPhotoViewModel by viewModels<UnsplashPhotoViewModel>()  //room database methods
    private val savedImageViewModel by viewModels<SavedImageViewModel>()
    private val homeFragmentViewModel by viewModels<HomeFragmentViewModel>()

    private var isRefreshEnabled = true
    private var isObservingForImageSavedInCollection = false
    private lateinit var homeAdapter: HomeRVAdapter

    private var lastDateSaved: String? = ""

    private var savedImagesIdList = emptyList<String>()

    private var rvStateParcelable: Parcelable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        loadUnsplashPhotoSavedDate()

        isRefreshEnabled = true

        getSavedImagesIdList()

        binding.homeSwipeRefreshLayout.setOnRefreshListener {

            if (requireContext().isInternetAvailable()) {

                makeNewAPIRequest()
            } else {

                binding.homeSwipeRefreshLayout.isRefreshing = false
                /* isRefreshEnabled = true
                 getSavedImagesIdList()*/
            }

        }

        observeForIfSavedImageAddedToTheCollection()

        getRecyclerViewState()

        //deleteUserProfileDataFromSharedPreference(requireActivity())
        //deleteAuthTokensFromSharedPreference(requireActivity())
    }

    private fun getRecyclerViewState() {

        homeFragmentViewModel.rVState.observe(viewLifecycleOwner, { parcelable ->

            rvStateParcelable = parcelable
        })
    }

    private fun getSavedImagesIdList() {

        savedImageViewModel.getAllSavedImagesID().observe(viewLifecycleOwner, {

            savedImagesIdList = it

            if (isRefreshEnabled) {

                homeAdapter = HomeRVAdapter(savedImagesIdList)
                setUpRecyclerView()

                getSavedUnsplashPhoto()
            }
        })
    }

    private fun getSavedUnsplashPhoto() {

        unsplashPhotoViewModel.getAllUnsplashPhoto().observe(viewLifecycleOwner, {

            if (isRefreshEnabled) {

                Log.d(TAG, "getSavedUnsplashPhoto: ")

                if (it.isEmpty() || lastDateSaved == "") {

                    //call the api
                    makeNewAPIRequest()
                } else {

                    //check for the date
                    //if the date(which is saved in the datastore) is Today then send this list to adapter
                    // else call the api and delete all the data and insert the new list received by the api

                    if (lastDateSaved == getCurrentDate()) {

                        if (!requireContext().isInternetAvailable()) {

                            requireContext().showNoInternetMessage()
                        }

                        homeAdapter.submitList(it)

                        //restoreRecyclerViewState()

                    } else {

                        //if it's the next day i.e after 12:00 AM
                        //calling the api
                        makeNewAPIRequest()
                    }
                }

                isRefreshEnabled = false
            }

        })
    }

    private fun restoreRecyclerViewState() {

        rvStateParcelable?.let {

            binding.homeRV.layoutManager?.onRestoreInstanceState(it)
        }
    }

    private fun makeNewAPIRequest() {

        if (requireContext().isInternetAvailable()) {

            Log.d(TAG, "makeNewAPIRequest: making new request")

            binding.homeSwipeRefreshLayout.isRefreshing = true
            unsplashViewModel.getRandomUnsplashImage()
            observeRandomImages()
        } else {

            requireContext().showNoInternetMessage()
        }
    }

    private fun observeRandomImages() {

        Log.d(TAG, "observeRandomImages: ")

        unsplashViewModel.unsplashRandomImage.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                    binding.homeSwipeRefreshLayout.isRefreshing = true
                }

                is Resources.Success -> {

                    //clearing the app cache
                    requireContext().clearAppCache()

                    saveTheListToDatabase(it.data)

                    //homeAdapter.submitList(it.data)

                }

                else -> {

                    try {
                        showToasty(requireContext(), it.message.toString(), ToastyType.ERROR)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

    }

    private fun saveTheListToDatabase(data: ArrayList<UnsplashPhoto>?) {

        try {

            data?.let {

                unsplashPhotoViewModel.deleteAllUnsplashPhoto()
                unsplashPhotoViewModel.insertUnsplashPhotoList(data)

                binding.homeSwipeRefreshLayout.isRefreshing = false

                homeAdapter.submitList(data)

            }

            saveUnsplashPhotoDate()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setUpRecyclerView() {

        Log.d(TAG, "setUpRecyclerView: ")

        try {

            binding.homeRV.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = homeAdapter
            }

            homeAdapter.setOnClickListener(this)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //[START OF CLICK LISTENERS]

    override fun onImageClicked(unsplashPhoto: UnsplashPhoto) {

        Log.d(TAG, "onImageClicked: Download : ${unsplashPhoto.id}")

        val intent = Intent(requireContext(), PreviewImageActivity::class.java)

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            ImageDownloadLinksAndInfo.ImageUrls(
                unsplashPhoto.urls.small,
                unsplashPhoto.urls.regular,
                unsplashPhoto.links.download
            ),
            unsplashPhoto.alt_description ?: "",
            unsplashPhoto.id,
            UserInfo(
                unsplashPhoto.user.name,
                unsplashPhoto.user.username,
                unsplashPhoto.user.profile_image.medium
            ),
            unsplashPhoto.width,
            unsplashPhoto.height
        )

        intent.putExtra(
            PREVIEW_IMAGE_KEY,
            convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )

        intent.putExtra(PREVIEW_IMAGE_TAG_KEY, HOME_FRAGMENT_TAG)

        startActivity(intent)
    }

    override fun onAddToFavouriteBtnClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        Log.d(TAG, "onAddToFavouriteBtnClicked: Download : ${unsplashPhoto.links.download}")

        if (savedImagesIdList.contains(unsplashPhoto.id)) {

            savedImageViewModel.deleteImageByImageId(unsplashPhoto.id)

            updateItemOfUnsplashSearchAdapter(position)

            showToasty(requireContext(), "Image unsaved", ToastyType.INFO)
        } else {

            val savedImage = generateSavedImage(unsplashPhoto, APIName.UNSPLASH)

            savedImageViewModel.insertImage(savedImage)

            updateItemOfUnsplashSearchAdapter(position)

            //showSnackBar(binding.root, "Image saved")

            binding.root.showSnackBar(
                "Image saved",
                actionMessage = "Choose collection"
            ) {

                isObservingForImageSavedInCollection = true
                this.position = position
                isRefreshEnabled = true

                getSavedImageAndPassItToCollectionBottomSheet(unsplashPhoto.id)
            }

            //upload to firestore if cloud support is available in future
        }
    }

    private fun updateItemOfUnsplashSearchAdapter(position: Int) {

        lifecycleScope.launch {

            delay(100)

            withContext(Dispatchers.Main) {

                homeAdapter.updateSavedImageListIds(savedImagesIdList)

                homeAdapter.notifyItemChanged(position)
            }
        }

    }
    
    @SuppressLint("RestrictedApi")
    override fun onDownloadImageBtnClicked(unsplashPhoto: UnsplashPhoto, view: View) {

        Log.d(TAG, "onShowMoreOptionsBtnClicked: raw : ${unsplashPhoto.urls.raw}")

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            ImageDownloadLinksAndInfo.ImageUrls(
                unsplashPhoto.urls.small,
                unsplashPhoto.urls.regular,
                unsplashPhoto.links.download
            ),
            unsplashPhoto.alt_description ?: UUID.randomUUID().toString() + "_unsplash",
            "",
            null
        )


        showDownloadOptionPopupMenu(
                requireActivity(),
                view,
                imageDownloadLinksAndInfo
        )

    }

    override fun onImageUserNameClicked(unsplashPhoto: UnsplashPhoto) {

        Log.d(TAG, "onImageUserNameClicked: ")

        openLinkInBrowser(requireContext(), unsplashPhoto.user.attributionUrl)

    }

    private var position: Int = -1

    override fun onAddToFavouriteLongClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        Log.d(TAG, "onAddToFavouriteLongClicked: ")

        isObservingForImageSavedInCollection = true

        this.position = position

        if (savedImagesIdList.contains(unsplashPhoto.id)) {

            isRefreshEnabled = true

            getSavedImageAndPassItToCollectionBottomSheet(unsplashPhoto.id)

        } else {

            val savedImage = generateSavedImage(unsplashPhoto, APIName.UNSPLASH)

            val action = HomeFragmentDirections.actionHomeFragmentToChooseFromCollectionsFragment(
                convertSavedImageToString(savedImage)
            )

            findNavController().navigate(action)
        }
    }

    private fun getSavedImageAndPassItToCollectionBottomSheet(imageId: String) {

        savedImageViewModel.getSavedImageByImageId(imageId).observe(viewLifecycleOwner, {

            if (isRefreshEnabled) {

                if (it != null) {

                    val action =
                        HomeFragmentDirections.actionHomeFragmentToChooseFromCollectionsFragment(
                            convertSavedImageToString(it)
                        )

                    findNavController().navigate(action)
                } else {

                    Log.d(TAG, "onAddToFavouriteLongClicked: Something went wrong!!")
                }

                isRefreshEnabled = false
            }
        })

    }

    private fun observeForIfSavedImageAddedToTheCollection() {

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>(IMAGE_SAVED_TO_COLLECTION_KEY)
            ?.observe(viewLifecycleOwner, {

                Log.d(
                    TAG,
                    "observeForCollectionAddition: isObserve... $isObservingForImageSavedInCollection"
                )

                if (isObservingForImageSavedInCollection) {

                    //true : user has selected one of the collection from the bottom sheet
                    //false : user hasn't selected any collection
                    if (it) {

                        showSnackBar(binding.root, "Image saved")

                        Log.d(TAG, "observeForCollectionAddition: value is true")

                        if (position != -1) {

                            Log.d(TAG, "observeForCollectionAddition: position : $position")

                            updateItemOfUnsplashSearchAdapter(position)

                                Log.d(TAG, "observeForCollectionAddition: updated")

                                position = -1
                            }
                        }

                        isObservingForImageSavedInCollection = false
                    }
                })

    }

    //[END OF CLICK LISTENERS]


    private fun saveUnsplashPhotoDate() {

        val sharedPreference = requireActivity().getSharedPreferences(
                UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
        )

        val edit = sharedPreference.edit()

        edit.putString(UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_KEY, getCurrentDate())

        edit.apply()
    }

    private fun loadUnsplashPhotoSavedDate() {

        val sharedPreference = requireActivity().getSharedPreferences(
            UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

        lastDateSaved = sharedPreference.getString(UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_KEY, "")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        homeFragmentViewModel.saveRVState(
            binding.homeRV.layoutManager?.onSaveInstanceState()
        )

        _binding = null
    }

}