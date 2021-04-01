package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NO_INTERNET_MESSAGE
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATE_SHARED_PREFERENCE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentHomeBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.HomeRVAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.UnsplashViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.UnsplashPhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()
    private val unsplashPhotoViewModel by viewModels<UnsplashPhotoViewModel>()

    private lateinit var homeAdapter: HomeRVAdapter

    private var isRefreshEnabled = true

    private var lastDateSaved: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        homeAdapter = HomeRVAdapter()

        binding.homeShimmerLayout.startShimmer()

        loadUnplashPhotoSavedDate()

        getSavedUnsplashPhoto()

        binding.homeSwipeRefreshLayout.setOnRefreshListener {

            binding.homeRV.hide()
            binding.homeShimmerLayoutNSV.show()
            makeNewAPIRequest()
        }
    }

    private fun getSavedUnsplashPhoto() {

        unsplashPhotoViewModel.getAllUnsplashPhoto().observe(viewLifecycleOwner, {

            if (isRefreshEnabled) {

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

                        setUpRecyclerView(it)
                    } else {

                        //calling the api
                        makeNewAPIRequest()
                    }
                }

                isRefreshEnabled = false
            }
        })
    }

    private fun makeNewAPIRequest() {

        //clearing the app cache
        requireContext().clearAppCache()

        binding.homeSwipeRefreshLayout.isRefreshing = true
        unsplashViewModel.getRandomUnsplashImage()
        observeRandomImages()
    }

    private fun observeRandomImages() {

        if (requireContext().isInternetAvailable()) {

            unsplashViewModel.unsplashRandomImage.observe(viewLifecycleOwner, {

                when (it) {

                    is Resources.Loading -> {

                        binding.homeShimmerLayout.startShimmer()
                    }

                    is Resources.Success -> {

                        binding.homeSwipeRefreshLayout.isRefreshing = false

                        saveTheListToDatabase(it.data)

                        setUpRecyclerView(it.data)
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
        } else {

            showToasty(requireContext(), NO_INTERNET_MESSAGE, ToastyType.ERROR)
        }
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

    private fun setUpRecyclerView(listOfImages: List<UnsplashPhoto>?) {

        try {

            binding.homeShimmerLayout.stopShimmer()
            binding.homeShimmerLayoutNSV.hide()

            binding.homeSwipeRefreshLayout.show()
            binding.homeRV.show()

            listOfImages?.let {

                homeAdapter.submitList(it)

                binding.homeRV.apply {

                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = homeAdapter
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

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