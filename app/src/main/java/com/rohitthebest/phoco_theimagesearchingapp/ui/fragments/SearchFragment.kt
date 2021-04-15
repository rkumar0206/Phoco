package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_MESSAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_PIXABAY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_UNSPLASH
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentSearchBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.activities.PreviewImageActivity
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.LoadingStateAdapterForPaging
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.PixabaySearchResultsAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.SpinnerSearchIconAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.UnsplashSearchResultsAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertImageDownloadLinksAndInfoToString
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.PixabayViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.UnsplashViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), UnsplashSearchResultsAdapter.OnClickListener, PixabaySearchResultsAdapter.OnClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()
    private val pixabayViewModel by viewModels<PixabayViewModel>()
    private val savedImageViewModel by viewModels<SavedImageViewModel>()

    private lateinit var spinnerAdapter: SpinnerSearchIconAdapter
    private lateinit var spinnerList: ArrayList<APIsInfo>

    private lateinit var unsplashSearchAdapter: UnsplashSearchResultsAdapter
    private lateinit var pixabaySearchAdapter: PixabaySearchResultsAdapter
    private lateinit var currentAPI: APIsInfo

    private var isRefreshEnabled = false

    private lateinit var savedImagesIds: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        spinnerList = ArrayList()

        initSpinnerList()

        spinnerAdapter = SpinnerSearchIconAdapter(requireContext(), spinnerList)

        isRefreshEnabled = true
        getAllSavedImageIds()

        initSearchEditText()

        observeUnsplashSearchResult()
        observePixabayResult()

        observeForIfSavedImageAddedToTheCollection()
    }

    private fun getAllSavedImageIds() {

        savedImageViewModel.getAllSavedImagesID().observe(viewLifecycleOwner, {

            savedImagesIds = it

            Log.d(TAG, "getAllSavedImageIds: $savedImagesIds")

            if (isRefreshEnabled) {

                if (it.isNotEmpty()) {

                    unsplashSearchAdapter = UnsplashSearchResultsAdapter(savedImagesIds)
                    pixabaySearchAdapter = PixabaySearchResultsAdapter()

                } else {

                    savedImagesIds = emptyList()
                    unsplashSearchAdapter = UnsplashSearchResultsAdapter()
                    pixabaySearchAdapter = PixabaySearchResultsAdapter()
                }

                setUpImageWebsiteOrApiSpinner()
                setUpLoadStateListener()

                isRefreshEnabled = false
            }
        })
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

        unsplashSearchAdapter.setOnClickListener(this)
        pixabaySearchAdapter.setOnClickListener(this)
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

    private fun setUpImageWebsiteOrApiSpinner() {

        binding.searchWithWebsiteSpinner.adapter = spinnerAdapter

        binding.searchWithWebsiteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val api = if (::currentAPI.isInitialized) currentAPI else parent?.getItemAtPosition(position) as APIsInfo

                currentAPI = parent?.getItemAtPosition(position) as APIsInfo

                if (binding.searchBoxACT.text.toString().validateString() && currentAPI != api) {

                    searchWithCorrectAPI(binding.searchBoxACT.text.toString())
                }

                setUpRecyclerView()

                updateHintOnTheSearchACT()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
    }

    private fun updateHintOnTheSearchACT() {

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

    private fun initSpinnerList() {

        spinnerList.add(APIsInfo(APIName.UNSPLASH, R.drawable.logo_unsplash))
        spinnerList.add(APIsInfo(APIName.PIXABAY, R.drawable.logo_pixabay_square))
        spinnerList.add(APIsInfo(APIName.PEXELS, R.drawable.logo_pexels))
        spinnerList.add(APIsInfo(APIName.WEB, R.drawable.ic_baseline_web_24))

    }

    private var position: Int = -1
    private var isObservingForImageSavedInCollection = false


    //----------------------------- Unsplash adapter click listeners ------------------------------
    override fun onImageClicked(unsplashPhoto: UnsplashPhoto) {

        Log.d(TAG, "onImageClicked: clicked by unsplash")

        val intent = Intent(requireContext(), PreviewImageActivity::class.java)

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
                ImageDownloadLinksAndInfo.ImageUrls(
                        unsplashPhoto.urls.small,
                        unsplashPhoto.urls.regular,
                        unsplashPhoto.links.download
                ),
                unsplashPhoto.alt_description ?: System.currentTimeMillis().toString(16),
                SEARCH_FRAGMENT_TAG_UNSPLASH,
                unsplashPhoto.id
        )

        intent.putExtra(
                PREVIEW_IMAGE_MESSAGE_KEY,
                convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )
        startActivity(intent)
    }

    override fun onAddToFavouriteBtnClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        if (savedImagesIds.contains(unsplashPhoto.id)) {

            savedImageViewModel.deleteImageByImageId(unsplashPhoto.id)

            updateItemOfUnsplashSearchAdapter(position)

            showToasty(requireContext(), "Image unsaved", ToastyType.INFO)

        } else {

            val savedImage = generateSavedImage(unsplashPhoto, APIName.UNSPLASH)

            savedImageViewModel.insertImage(savedImage)

            showSnackBar(binding.root, "Image Saved")

            updateItemOfUnsplashSearchAdapter(position)
        }

    }

    private fun updateItemOfUnsplashSearchAdapter(position: Int) {

        GlobalScope.launch {

            delay(100)

            withContext(Dispatchers.Main) {

                unsplashSearchAdapter.updateSavedImageListIds(savedImagesIds)

                unsplashSearchAdapter.notifyItemChanged(position)
            }
        }

    }

    override fun onDownloadImageBtnClicked(unsplashPhoto: UnsplashPhoto) {
        //TODO("Not yet implemented")
    }

    override fun onImageUserNameClicked(unsplashPhoto: UnsplashPhoto) {
        //TODO("Not yet implemented")
    }

    override fun onAddToFavouriteLongClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        isObservingForImageSavedInCollection = true
        this.position = position

        if (savedImagesIds.contains(unsplashPhoto.id)) {

            isRefreshEnabled = true

            savedImageViewModel.getSavedImageByImageId(unsplashPhoto.id).observe(viewLifecycleOwner, {

                if (isRefreshEnabled) {

                    if (it != null) {

                        val action = SearchFragmentDirections.actionSearchFragmentToChooseFromCollectionsFragment(
                                GsonConverters.convertSavedImageToString(it)
                        )

                        findNavController().navigate(action)
                    } else {

                        Log.d(TAG, "onAddToFavouriteLongClicked: Something went wrong!!")
                    }

                    isRefreshEnabled = false
                }
            })

        } else {

            val savedImage = generateSavedImage(unsplashPhoto, APIName.UNSPLASH)

            val action = SearchFragmentDirections.actionSearchFragmentToChooseFromCollectionsFragment(
                    GsonConverters.convertSavedImageToString(savedImage)
            )

            findNavController().navigate(action)
        }
    }
    //-------------------------------------------------------------------------------------------


    //----------------------------- Pixabay adapter click listeners ------------------------------
    override fun onImageClicked(pixabayPhoto: PixabayPhoto) {

        Log.d(TAG, "onImageClicked: clicked by pixabay")

        val intent = Intent(requireContext(), PreviewImageActivity::class.java)

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
                ImageDownloadLinksAndInfo.ImageUrls(
                        pixabayPhoto.previewURL,
                        pixabayPhoto.webformatURL,
                        pixabayPhoto.largeImageURL
                ),
                System.currentTimeMillis().toString(16),
                SEARCH_FRAGMENT_TAG_PIXABAY,
                pixabayPhoto.id.toString()
        )

        intent.putExtra(
                PREVIEW_IMAGE_MESSAGE_KEY,
                convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )
        startActivity(intent)
    }

    override fun onAddToFavouriteBtnClicked(pixabayPhoto: PixabayPhoto) {

        val im = generateSavedImage(pixabayPhoto, APIName.PIXABAY)

        Log.d(TAG, "onAddToFavouriteBtnClicked: Pixabay savedImage: $im")

        //TODO("Not yet implemented")
    }

    override fun ondownloadImageBtnClicked(pixabayPhoto: PixabayPhoto) {
        //TODO("Not yet implemented")
    }

    override fun onImageUserNameClicked(pixabayPhoto: PixabayPhoto) {
        //TODO("Not yet implemented")
    }

    override fun onAddToFavouriteLongClicked(pixabayPhoto: PixabayPhoto) {
        //TODO("Not yet implemented")
    }
    //---------------------------------------------------------------------------------------------


    private fun observeForIfSavedImageAddedToTheCollection() {

        findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Boolean>(Constants.IMAGE_SAVED_TO_COLLECTION_KEY)
                ?.observe(viewLifecycleOwner, {

                    if (isObservingForImageSavedInCollection) {

                        //true : user has selected one of the collection from the bottom sheet
                        //false : user hasn't selected any collection
                        if (it) {

                            showSnackBar(binding.root, "Image saved")

                            if (position != -1) {

                                when (currentAPI.apiName) {

                                    APIName.UNSPLASH -> updateItemOfUnsplashSearchAdapter(position)

                                    else -> Log.d(TAG, "observeForIfSavedImageAddedToTheCollection: ")
                                }

                                Log.d(TAG, "observeForCollectionAddition: updated")

                                position = -1
                            }
                        }
                        isObservingForImageSavedInCollection = false
                    }
                })

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}