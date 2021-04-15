package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants.HOME_FRAGMENT_TAG
import com.rohitthebest.phoco_theimagesearchingapp.Constants.IMAGE_SAVED_TO_COLLECTION_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_MESSAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.UserInfo
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentHomeBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.activities.PreviewImageActivity
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.HomeRVAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertImageDownloadLinksAndInfoToString
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertSavedImageToString
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.UnsplashViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.UnsplashPhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), HomeRVAdapter.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()
    private val unsplashPhotoViewModel by viewModels<UnsplashPhotoViewModel>()  //room database methods
    private val savedImageViewModel by viewModels<SavedImageViewModel>()

    private var isRefreshEnabled = true
    private var isObservingForCollectionAdd = false
    private lateinit var homeAdapter: HomeRVAdapter

    private var lastDateSaved: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        homeAdapter = HomeRVAdapter()

       loadUnplashPhotoSavedDate()

        isRefreshEnabled = true
        getSavedUnsplashPhoto()

        binding.homeSwipeRefreshLayout.setOnRefreshListener {

            if (requireContext().isInternetAvailable()) {

                makeNewAPIRequest()
            } else {

                binding.homeSwipeRefreshLayout.isRefreshing = false
                getSavedUnsplashPhoto()
            }

        }

        setUpRecyclerView()

        observeForCollectionAddition()
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

                    binding.homeSwipeRefreshLayout.isRefreshing = false

                    saveTheListToDatabase(it.data)

                    homeAdapter.submitList(it.data)

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

                savedImageViewModel.getAllSavedImages().observe(viewLifecycleOwner, {

                    val idList = it.map { s ->

                        s.imageId
                    }

                    data.forEach { unsplashPhoto ->

                        if (idList.contains(unsplashPhoto.id)) {

                            unsplashPhoto.isImageSavedInCollection = true
                        }
                    }

                    unsplashPhotoViewModel.insertUnsplashPhotoList(data)
                })

                //unsplashPhotoViewModel.insertUnsplashPhotoList(data)
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
                HOME_FRAGMENT_TAG,
                unsplashPhoto.id
        )

        intent.putExtra(PREVIEW_IMAGE_MESSAGE_KEY,
                convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo))

        startActivity(intent)
    }

    override fun onAddToFavouriteBtnClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        Log.d(TAG, "onAddToFavouriteBtnClicked: Download : ${unsplashPhoto.links.download}")

        if (!unsplashPhoto.isImageSavedInCollection) {

            val savedImage = getSavedImage(unsplashPhoto)

            savedImageViewModel.insertImage(savedImage)

            unsplashPhoto.isImageSavedInCollection = true

            unsplashPhotoViewModel.updateUnsplashPhoto(unsplashPhoto)

            homeAdapter.notifyItemChanged(position)

            showToasty(requireContext(), "Image saved", ToastyType.SUCCESS)

            //todo : upload to firestore if cloud support is available in future
        } else {

            savedImageViewModel.deleteImageByImageId(unsplashPhoto.id)

            unsplashPhoto.isImageSavedInCollection = false

            unsplashPhotoViewModel.updateUnsplashPhoto(unsplashPhoto)

            homeAdapter.notifyItemChanged(position)

            showToasty(requireContext(), "Image unsaved", ToastyType.INFO)
        }

    }

    private fun getSavedImage(unsplashPhoto: UnsplashPhoto): SavedImage {

        return SavedImage(
                key = generateKey(),
                collectionKey = "",
                timeStamp = System.currentTimeMillis(),
                apiInfo = APIsInfo(APIName.UNSPLASH, R.drawable.logo_unsplash),
                imageName = unsplashPhoto.alt_description ?: generateKey(),
                imageId = unsplashPhoto.id,
                imageUrls = ImageDownloadLinksAndInfo.ImageUrls(unsplashPhoto.urls.small, unsplashPhoto.urls.regular, unsplashPhoto.links.download),
                userInfo = UserInfo(
                        unsplashPhoto.user.name,
                        unsplashPhoto.user.id,
                        unsplashPhoto.user.profile_image.medium
                ),
                uid = ""
        )
    }

    override fun onDownloadImageBtnClicked(unsplashPhoto: UnsplashPhoto) {

        Log.d(TAG, "onShowMoreOptionsBtnClicked: raw : ${unsplashPhoto.urls.raw}")
        //TODO("Not yet implemented")
    }

    override fun onImageUserNameClicked(unsplashPhoto: UnsplashPhoto) {

        Log.d(TAG, "onImageUserNameClicked: ")
        //TODO("Not yet implemented")
    }

    //this variable is used for updating the isImageSavedToCollection value, when user long presses the
    // add to favourite button and the bottom sheet pops up
    private var unsplashPhotoForUpdatingSaveToCollectionValue: UnsplashPhoto? = null
    private var position: Int = -1


    override fun onAddToFavouriteLongClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        Log.d(TAG, "onAddToFavouriteLongClicked: ")

        isObservingForCollectionAdd = true

        isRefreshEnabled = true

        unsplashPhotoForUpdatingSaveToCollectionValue = unsplashPhoto
        this.position = position

        if (unsplashPhoto.isImageSavedInCollection) {

            savedImageViewModel.getSavedImageByImageId(unsplashPhoto.id).observe(viewLifecycleOwner, {

                if (isRefreshEnabled) {

                    if (it != null) {

                        val action = HomeFragmentDirections.actionHomeFragmentToChooseFromCollectionsFragment(
                                convertSavedImageToString(it)
                        )

                        findNavController().navigate(action)
                    } else {

                        Log.d(TAG, "onAddToFavouriteLongClicked: Something went wrong!!")
                    }

                    isRefreshEnabled = false
                }
            })
        } else {

            val savedImage = getSavedImage(unsplashPhoto)

            val action = HomeFragmentDirections.actionHomeFragmentToChooseFromCollectionsFragment(
                    convertSavedImageToString(savedImage)
            )

            findNavController().navigate(action)
        }
    }

    private fun observeForCollectionAddition() {

        findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Boolean>(IMAGE_SAVED_TO_COLLECTION_KEY)
                ?.observe(viewLifecycleOwner, {

                    Log.d(TAG, "observeForCollectionAddition: isObserva... $isObservingForCollectionAdd")

                    if (isObservingForCollectionAdd) {


                        //true : user has selected one of the collection from the bottom sheet
                        //false : user hasn't selected any collection
                        if (it) {

                            showSnackBar(binding.root, "Image saved")

                            Log.d(TAG, "observeForCollectionAddition: value is true")

                            //updating the unsplashPhoto isImageSavedToCollection value

                            if (unsplashPhotoForUpdatingSaveToCollectionValue != null && position != -1) {

                                Log.d(TAG, "observeForCollectionAddition: photo : $unsplashPhotoForUpdatingSaveToCollectionValue")
                                Log.d(TAG, "observeForCollectionAddition: position : $position")

                                unsplashPhotoForUpdatingSaveToCollectionValue?.isImageSavedInCollection = true
                                unsplashPhotoViewModel.updateUnsplashPhoto(unsplashPhotoForUpdatingSaveToCollectionValue!!)
                                homeAdapter.notifyItemChanged(position)

                                Log.d(TAG, "observeForCollectionAddition: updated")

                                unsplashPhotoForUpdatingSaveToCollectionValue = null
                                position = -1
                            }
                        }

                        isObservingForCollectionAdd = false
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

    private fun loadUnplashPhotoSavedDate() {

        val sharedPreference = requireActivity().getSharedPreferences(
                UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
        )

        lastDateSaved = sharedPreference.getString(UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_KEY, "")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}