package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentHomeBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.HomeRVAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.ToastyType
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToasty
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.UnsplashViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.UnsplashPhotoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()
    private val unsplashPhotoViewModel by viewModels<UnsplashPhotoViewModel>()

    private lateinit var homeAdapter: HomeRVAdapter

    private var isRefreshEnabled = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        homeAdapter = HomeRVAdapter()

        binding.homeShimmerLayout.startShimmer()

        getSavedUnsplashPhoto()

        unsplashViewModel.getRandomUnsplashImage()
        observeRandomImages()
    }

    private fun getSavedUnsplashPhoto() {

        unsplashPhotoViewModel.getAllUnsplashPhoto().observe(viewLifecycleOwner, {

            if (isRefreshEnabled) {

                if (it.isEmpty()) {

                    //call the api
                } else {

                    //check for the date
                    //if the date(which is saved in the datastore) is Today then send this list to adapter
                    // else call the api and delete all the data and insert the new list received by the api

                }

                isRefreshEnabled = false
            }
        })
    }

    private fun observeRandomImages() {

        unsplashViewModel.unsplashRandomImage.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                    binding.homeShimmerLayout.startShimmer()
                }

                is Resources.Success -> {

                    binding.homeShimmerLayout.stopShimmer()
                    binding.homeShimmerLayoutNSV.hide()

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
    }

    private fun setUpRecyclerView(listOfImages: List<UnsplashPhoto>?) {

        try {

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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}