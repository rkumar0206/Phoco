package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentSearchBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.LoadingStateAdapterForPaging
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.PixabaySearchResultsAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.SpinnerSearchIconAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.UnsplashSearchResultsAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.PixabayViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.UnsplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()
    private val pixabayViewModel by viewModels<PixabayViewModel>()

    private lateinit var spinnerAdapter: SpinnerSearchIconAdapter
    private lateinit var spinnerList: ArrayList<APIsInfo>

    private lateinit var unsplashSearchAdapter: UnsplashSearchResultsAdapter
    private lateinit var pixabaySearchAdapter: PixabaySearchResultsAdapter
    private lateinit var currentAPI: APIsInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        spinnerList = ArrayList()

        initSpinnerList()

        spinnerAdapter = SpinnerSearchIconAdapter(requireContext(), spinnerList)

        setUpWebsiteSpinner()

        unsplashSearchAdapter = UnsplashSearchResultsAdapter()
        pixabaySearchAdapter = PixabaySearchResultsAdapter()

        initSearchEditText()

        setUpLoadStateListener()

        observeUnsplashSearchResult()
        observePixabayResult()

        //setUpRecyclerView()
    }

    private fun observePixabayResult() {

        pixabayViewModel.pixabaySearchResult.observe(viewLifecycleOwner, {

            pixabaySearchAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun observeUnsplashSearchResult() {

        unsplashViewModel.unsplashSearchResult.observe(viewLifecycleOwner, {

            unsplashSearchAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun setUpRecyclerView() {

        binding.searchRV.apply {

            adapter = when (currentAPI.apiName) {

                APIName.UNSPLASH -> {
                    unsplashSearchAdapter.withLoadStateHeaderAndFooter(
                            header = LoadingStateAdapterForPaging { unsplashSearchAdapter.retry() },
                            footer = LoadingStateAdapterForPaging { unsplashSearchAdapter.retry() }
                    )
                }

                APIName.PIXABAY -> {

                    pixabaySearchAdapter.withLoadStateHeaderAndFooter(
                            header = LoadingStateAdapterForPaging { pixabaySearchAdapter.retry() },
                            footer = LoadingStateAdapterForPaging { pixabaySearchAdapter.retry() }
                    )
                }

                else -> {

                    unsplashSearchAdapter.withLoadStateHeaderAndFooter(
                            header = LoadingStateAdapterForPaging { unsplashSearchAdapter.retry() },
                            footer = LoadingStateAdapterForPaging { unsplashSearchAdapter.retry() }
                    )
                }
            }

            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpLoadStateListener() {

        unsplashSearchAdapter.addLoadStateListener { loadState ->

            applyChangesAccordingToLoadState(loadState)

        }

        pixabaySearchAdapter.addLoadStateListener {

            applyChangesAccordingToLoadState(it)
        }

    }

    private fun applyChangesAccordingToLoadState(loadState: CombinedLoadStates) {

        binding.apply {

            progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            searchRV.isVisible = loadState.source.refresh is LoadState.NotLoading
            /*               buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                           textViewError.isVisible = loadState.source.refresh is LoadState.Error

                           if (
                                   loadState.source.refresh is LoadState.NotLoading
                                   && loadState.append.endOfPaginationReached
                                   && mAdapter.itemCount < 1
                           ) {

                               recyclerView.isVisible = false
                               textViewEmpty.isVisible = true
                           } else {

                               textViewEmpty.isVisible = false
                           }*/
        }

    }

    private fun initSearchEditText() {

        binding.searchBoxACT.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                if (binding.searchBoxACT.text.toString().validateString()) {

                    hideKeyBoard(requireActivity())

                    if (requireContext().isInternetAvailable()) {

                        searchWithCorrectAPI(binding.searchBoxACT.text.toString().trim())
                    } else {
                        requireContext().showNoInternetMessage()
                    }
                }
            }
            true
        }
    }

    private fun searchWithCorrectAPI(searchString: String) {

        when (currentAPI.apiName) {

            APIName.UNSPLASH -> {

                unsplashViewModel.searchImage(searchString)
            }

            APIName.PIXABAY -> {

                pixabayViewModel.searchWithPixabay(searchString)
            }

            else -> {
                unsplashViewModel.searchImage(searchString)
            }
        }

    }

    private fun setUpWebsiteSpinner() {

        binding.searchWithWebsiteSpinner.adapter = spinnerAdapter

        binding.searchWithWebsiteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val api = if (::currentAPI.isInitialized) currentAPI else parent?.getItemAtPosition(position) as APIsInfo

                currentAPI = parent?.getItemAtPosition(position) as APIsInfo

                if (binding.searchBoxACT.text.toString().validateString() && currentAPI != api) {

                    searchWithCorrectAPI(binding.searchBoxACT.text.toString())
                }

                setUpRecyclerView()

                when (currentAPI.apiName) {

                    APIName.UNSPLASH -> {

                        binding.searchBoxACT.hint = "Search on ${getString(R.string.unsplash)}"

                    }

                    APIName.PIXABAY -> {

                        binding.searchBoxACT.hint = "Search on ${getString(R.string.pixabay)}"
                    }

                    APIName.PEXELS -> {

                        binding.searchBoxACT.hint = "Search on ${getString(R.string.pexels)}"
                    }

                    APIName.WEB -> {

                        binding.searchBoxACT.hint = "Search on ${getString(R.string.web)}"
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
    }

    private fun initSpinnerList() {

        spinnerList.add(APIsInfo(APIName.UNSPLASH, R.drawable.logo_unsplash))
        spinnerList.add(APIsInfo(APIName.PIXABAY, R.drawable.logo_pixabay_square))
        spinnerList.add(APIsInfo(APIName.PEXELS, R.drawable.logo_pexels))
        spinnerList.add(APIsInfo(APIName.WEB, R.drawable.ic_baseline_web_24))

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}