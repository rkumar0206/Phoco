package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentSearchBinding
import com.rohitthebest.phoco_theimagesearchingapp.databinding.SearchFragmentLayoutBinding

enum class SearchWith {

    UNSPLASH,
    PIXABAY,
    PEXELS,
    WEB
}

class SearchFragment : Fragment(R.layout.fragment_search), View.OnClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var includeBinding: SearchFragmentLayoutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        includeBinding = binding.include

        initListeners()
    }

    private fun initListeners() {

        includeBinding.searchWithUnsplashMCV.setOnClickListener(this)
        includeBinding.searchWithPexelsMCV.setOnClickListener(this)
        includeBinding.searchWithPixabayMCV.setOnClickListener(this)
        includeBinding.searchWithWebMCV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            includeBinding.searchWithUnsplashMCV.id -> {


            }

            includeBinding.searchWithPexelsMCV.id -> {


            }

            includeBinding.searchWithPixabayMCV.id -> {


            }

            includeBinding.searchWithWebMCV.id -> {


            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}