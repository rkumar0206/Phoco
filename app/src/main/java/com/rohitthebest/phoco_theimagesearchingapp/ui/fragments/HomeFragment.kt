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
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToast
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToasty
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.UnsplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()

    private lateinit var homeAdapter: HomeRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        homeAdapter = HomeRVAdapter()

        unsplashViewModel.getRandomUnsplashImage()
        observeRandomImages()
    }

    private fun observeRandomImages() {

        unsplashViewModel.unsplashRandomImage.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                    /*show loading spinner*/

                    showToast(requireContext(), "Loading...")
                }

                is Resources.Success -> {

                    // hide loading spinner

                    setUpRecyclerView(it.data)
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

    private fun setUpRecyclerView(listOfImages: List<UnsplashPhoto>?) {

        try {

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