package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants.HOME_FRAGMENT_TAG
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

    private lateinit var homeAdapter: HomeRVAdapter

    private var lastDateSaved: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        homeAdapter = HomeRVAdapter()

        binding.homeShimmerLayout.startShimmer()

        loadUnplashPhotoSavedDate()

        getSavedUnsplashPhoto()

        binding.homeSwipeRefreshLayout.setOnRefreshListener {

            if (requireContext().isInternetAvailable()) {

                binding.homeRV.hide()
                binding.homeShimmerLayoutNSV.show()
                makeNewAPIRequest()
            } else {

                binding.homeSwipeRefreshLayout.isRefreshing = false
                getSavedUnsplashPhoto()
            }

        }

        setUpRecyclerView()
    }

    private fun getSavedUnsplashPhoto() {

        unsplashPhotoViewModel.getAllUnsplashPhoto().observe(viewLifecycleOwner, {


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

                        hideShimmerAndShowRecyclerView()

                    } else {

                        //calling the api
                        makeNewAPIRequest()
                    }
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

                    binding.homeShimmerLayout.startShimmer()
                }

                is Resources.Success -> {

                    //clearing the app cache
                    requireContext().clearAppCache()

                    binding.homeSwipeRefreshLayout.isRefreshing = false

                    saveTheListToDatabase(it.data)

                    homeAdapter.submitList(it.data)

                    hideShimmerAndShowRecyclerView()
                }

                else -> {

                    try {
                        binding.homeShimmerLayout.stopShimmer()
                        binding.homeShimmerLayoutNSV.hide()

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

            val savedImage = SavedImage(
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

            showToasty(requireContext(), "Image removed", ToastyType.INFO)
        }

    }

    override fun onDownloadImageBtnClicked(unsplashPhoto: UnsplashPhoto) {

        Log.d(TAG, "onShowMoreOptionsBtnClicked: raw : ${unsplashPhoto.urls.raw}")
        //TODO("Not yet implemented")
    }

    override fun onImageUserNameClicked(unsplashPhoto: UnsplashPhoto) {

        Log.d(TAG, "onImageUserNameClicked: ")
        //TODO("Not yet implemented")
    }

    override fun onAddToFavouriteLongClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        Log.d(TAG, "onAddToFavouriteLongClicked: ")
        //TODO("Not yet implemented")
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

    private fun hideShimmerAndShowRecyclerView() {

        binding.homeShimmerLayout.stopShimmer()
        binding.homeShimmerLayoutNSV.hide()

        binding.homeSwipeRefreshLayout.show()
        binding.homeRV.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}