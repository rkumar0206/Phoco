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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_TAG_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_PEXEL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_PIXABAY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_UNDRAW
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_UNSPLASH
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_WEB
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentSearchBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.mohitImagApiData.WebPhoto
import com.rohitthebest.phoco_theimagesearchingapp.remote.pexelsData.PexelPhoto
import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.remote.undrawData.Illo
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.ui.activities.PreviewImageActivity
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertImageDownloadLinksAndInfoToString
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertSavedImageToString
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.fromPreviewUnDrawImagesMessageToString
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.*
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    UnsplashSearchResultsAdapter.OnClickListener, PixabaySearchResultsAdapter.OnClickListener,
    PexelSearchResultsAdapter.OnClickListener, WebImageAdapter.OnClickListener,
    UnDrawImageAdapter.OnClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel by viewModels<UnsplashViewModel>()
    private val pixabayViewModel by viewModels<PixabayViewModel>()
    private val pexelViewModel by viewModels<PexelViewModel>()
    private val savedImageViewModel by viewModels<SavedImageViewModel>()
    private val webImageViewModel by viewModels<WebPhotoViewModel>()
    private val unDrawImageViewModel by viewModels<UnDrawViewModel>()

    private lateinit var spinnerAdapter: SpinnerSearchIconAdapter
    private lateinit var spinnerList: ArrayList<APIsInfo>

    private lateinit var unsplashSearchAdapter: UnsplashSearchResultsAdapter
    private lateinit var pixabaySearchAdapter: PixabaySearchResultsAdapter
    private lateinit var pexelSearchAdapter: PexelSearchResultsAdapter
    private lateinit var webImageAdapter: WebImageAdapter
    private lateinit var unDrawImageAdapter: UnDrawImageAdapter

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
        webImageAdapter = WebImageAdapter()
        unDrawImageAdapter = UnDrawImageAdapter()

        initSearchEditText()

        // ------------------------- Observe image results / response ----------
        observeUnsplashSearchResult()
        observePixabayResult()
        observePexelResult()
        observeWebImageResult()
        observeUnDrawImageResult()
        // ------------------------------------------------------------

        observeForIfSavedImageAddedToTheCollection()

        binding.searchBoxACT.showKeyboard(requireActivity())
    }

    private fun getAllSavedImageIds() {

        savedImageViewModel.getAllSavedImagesID().observe(viewLifecycleOwner, {

            savedImagesIds = it

            Log.d(TAG, "getAllSavedImageIds: $savedImagesIds")

            if (isRefreshEnabled) {

                if (it.isNotEmpty()) {

                    unsplashSearchAdapter = UnsplashSearchResultsAdapter(savedImagesIds)
                    pixabaySearchAdapter = PixabaySearchResultsAdapter(savedImagesIds)
                    pexelSearchAdapter = PexelSearchResultsAdapter(savedImagesIds)

                } else {

                    savedImagesIds = emptyList()
                    unsplashSearchAdapter = UnsplashSearchResultsAdapter()
                    pixabaySearchAdapter = PixabaySearchResultsAdapter()
                    pexelSearchAdapter = PexelSearchResultsAdapter()
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
            binding.noResultsFoundTV.hide()
        })
    }

    private fun observeUnsplashSearchResult() {

        unsplashViewModel.unsplashSearchResult.observe(viewLifecycleOwner, {

            Log.d(TAG, "observeUnsplashSearchResult: $it")
            unsplashSearchAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.noResultsFoundTV.hide()

        })
    }

    private fun observePexelResult() {

        pexelViewModel.pexelSearchResult.observe(viewLifecycleOwner, {

            Log.d(TAG, "observePexelResult: $it")

            pexelSearchAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.noResultsFoundTV.hide()
        })
    }

    private fun observeWebImageResult() {

        webImageViewModel.webImage.observe(viewLifecycleOwner, {

            Log.d(TAG, "observeWebImageResult: $it")

            when (it) {

                is Resources.Loading -> {

                    Log.d(TAG, "observeWebImageResult: Loading")

                    binding.apply {

                        progressBar.show()
                        searchRV.hide()
                    }
                }

                is Resources.Success -> {

                    binding.apply {

                        progressBar.hide()
                        searchRV.show()
                    }

                    Log.d(TAG, "observeWebImageResult: Success ${it.data?.result}")

                    if (it.data?.result.isNullOrEmpty()) {

                        binding.noResultsFoundTV.show()
                    } else {

                        binding.noResultsFoundTV.hide()
                        binding.searchRV.scrollToPosition(0)
                        webImageAdapter.submitList(it.data?.result)
                    }

                }

                is Resources.Error -> {

                    binding.apply {

                        progressBar.show()
                        searchRV.hide()
                    }

                    Log.d(TAG, "observeWebImageResult: Error ${it.message}")

                    showToasty(requireContext(), it.message.toString(), ToastyType.ERROR)
                }
            }
        })
    }

    private fun observeUnDrawImageResult() {

        unDrawImageViewModel.unDrawImage.observe(viewLifecycleOwner, {

            Log.d(TAG, "observeWebImageResult: $it")

            when (it) {

                is Resources.Loading -> {

                    Log.d(TAG, "observeWebImageResult: Loading")

                    binding.apply {

                        progressBar.show()
                        searchRV.scrollToPosition(0)
                        searchRV.hide()
                    }
                }

                is Resources.Success -> {

                    binding.apply {

                        progressBar.hide()
                        searchRV.show()
                    }

                    Log.d(TAG, "observeWebImageResult: Success ${it.data?.illos}")

                    if (it.data?.illos.isNullOrEmpty()) {

                        binding.noResultsFoundTV.show()
                    } else {

                        binding.noResultsFoundTV.hide()
                        binding.searchRV.scrollToPosition(0)
                        unDrawImageAdapter.submitList(it.data?.illos)
                    }

                }

                is Resources.Error -> {

                    binding.apply {

                        progressBar.show()
                        searchRV.hide()
                    }

                    Log.d(TAG, "observeWebImageResult: Error ${it.message}")

                    showToasty(requireContext(), it.message.toString(), ToastyType.ERROR)
                }
            }
        })

    }

    private fun setUpRecyclerView() {

        Log.d(TAG, "setUpRecyclerView: current Api ${currentAPI.apiName}")

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

                APIName.PEXELS -> {

                    pexelSearchAdapter.withLoadStateHeaderAndFooter(
                        header = LoadingStateAdapterForPaging { pixabaySearchAdapter.retry() },
                        footer = LoadingStateAdapterForPaging { pixabaySearchAdapter.retry() }
                    )
                }

                APIName.WEB -> {

                    webImageAdapter
                }

                APIName.UNDRAW -> {

                    unDrawImageAdapter
                }
            }

            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        unsplashSearchAdapter.setOnClickListener(this)
        pixabaySearchAdapter.setOnClickListener(this)
        pexelSearchAdapter.setOnClickListener(this)
        webImageAdapter.setOnClickListener(this)
        unDrawImageAdapter.setOnClickListener(this)
    }

    private fun setUpLoadStateListener() {

        unsplashSearchAdapter.addLoadStateListener { loadState ->

            applyChangesAccordingToLoadState(loadState)

        }

        pixabaySearchAdapter.addLoadStateListener {

            applyChangesAccordingToLoadState(it)
        }

        pexelSearchAdapter.addLoadStateListener {

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

                if (binding.searchBoxACT.text.toString().isValidString()) {

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

        if (requireContext().isInternetAvailable()) {

            when (currentAPI.apiName) {

                APIName.UNSPLASH -> {

                    unsplashViewModel.searchImage(searchString)
                }

                APIName.PIXABAY -> {

                    pixabayViewModel.searchWithPixabay(searchString)
                }

                APIName.PEXELS -> {

                    pexelViewModel.searchImage(searchString)
                }

                APIName.WEB -> {

                    webImageViewModel.searchImage(searchString)
                }

                APIName.UNDRAW -> {

                    unDrawImageViewModel.searchImage(searchString)
                }
            }
        } else {

            requireContext().showNoInternetMessage()
        }
    }

    private fun setUpImageWebsiteOrApiSpinner() {

        binding.searchWithWebsiteSpinner.adapter = spinnerAdapter

        binding.searchWithWebsiteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val api = if (::currentAPI.isInitialized) currentAPI else parent?.getItemAtPosition(position) as APIsInfo

                currentAPI = parent?.getItemAtPosition(position) as APIsInfo

                setUpRecyclerView()

                if (binding.searchBoxACT.text.toString().isValidString() && currentAPI != api) {

                    searchWithCorrectAPI(binding.searchBoxACT.text.toString())
                    hideKeyBoard(requireActivity())
                }

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

            APIName.UNDRAW -> {

                binding.searchBoxACT.hint = "Search on ${getString(R.string.unDraw)}"
            }

        }

    }

    private fun initSpinnerList() {

        spinnerList.add(APIsInfo(APIName.UNSPLASH, R.drawable.logo_unsplash))
        spinnerList.add(APIsInfo(APIName.PIXABAY, R.drawable.logo_pixabay_square))
        spinnerList.add(APIsInfo(APIName.PEXELS, R.drawable.logo_pexels))
        spinnerList.add(APIsInfo(APIName.WEB, R.drawable.ic_baseline_web_24))
        spinnerList.add(APIsInfo(APIName.UNDRAW, R.drawable.undraw_icon))
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
            unsplashPhoto.id
        )

        intent.putExtra(
            PREVIEW_IMAGE_KEY,
            convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )

        intent.putExtra(PREVIEW_IMAGE_TAG_KEY, SEARCH_FRAGMENT_TAG_UNSPLASH)

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

        lifecycleScope.launch {

            delay(100)

            withContext(Dispatchers.Main) {

                unsplashSearchAdapter.updateSavedImageListIds(savedImagesIds)

                unsplashSearchAdapter.notifyItemChanged(position)
            }
        }

    }

    override fun onDownloadImageBtnClicked(unsplashPhoto: UnsplashPhoto, view: View) {

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            ImageDownloadLinksAndInfo.ImageUrls(
                unsplashPhoto.urls.small,
                unsplashPhoto.urls.regular,
                unsplashPhoto.links.download
            ),
            unsplashPhoto.alt_description ?: UUID.randomUUID().toString() + "_unsplash",
            ""
        )

        showDownloadOptionPopupMenu(
                requireActivity(),
                view,
                imageDownloadLinksAndInfo
        )
    }

    override fun onImageUserNameClicked(unsplashPhoto: UnsplashPhoto) {

        openLinkInBrowser(requireContext(), unsplashPhoto.user.attributionUrl)
    }

    override fun onAddToFavouriteLongClicked(unsplashPhoto: UnsplashPhoto, position: Int) {

        isObservingForImageSavedInCollection = true
        this.position = position

        if (savedImagesIds.contains(unsplashPhoto.id)) {

            isRefreshEnabled = true

            getTheSavedImageAndPassItToTheChooseCollectionBottomSheet(unsplashPhoto.id)

        } else {

            val savedImage = generateSavedImage(unsplashPhoto, APIName.UNSPLASH)

            val action = SearchFragmentDirections.actionSearchFragmentToChooseFromCollectionsFragment(
                    convertSavedImageToString(savedImage)
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
            UUID.randomUUID().toString() + "_pixabay",
            pixabayPhoto.id.toString()
        )

        intent.putExtra(
            PREVIEW_IMAGE_KEY,
            convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )

        intent.putExtra(PREVIEW_IMAGE_TAG_KEY, SEARCH_FRAGMENT_TAG_PIXABAY)

        startActivity(intent)
    }

    override fun onAddToFavouriteBtnClicked(pixabayPhoto: PixabayPhoto, position: Int) {

        if (savedImagesIds.contains(pixabayPhoto.id.toString())) {

            savedImageViewModel.deleteImageByImageId(pixabayPhoto.id.toString())

            updateItemOfPixabaySearchAdapter(position)

            showToasty(requireContext(), "Image unsaved", ToastyType.INFO)

        } else {

            val savedImage = generateSavedImage(pixabayPhoto, APIName.PIXABAY)

            savedImageViewModel.insertImage(savedImage)

            showSnackBar(binding.root, "Image Saved")

            updateItemOfPixabaySearchAdapter(position)

        }

    }

    private fun updateItemOfPixabaySearchAdapter(position: Int) {

        lifecycleScope.launch {

            delay(100)

            withContext(Dispatchers.Main) {

                pixabaySearchAdapter.updateSavedImageListIds(savedImagesIds)

                pixabaySearchAdapter.notifyItemChanged(position)
            }
        }

    }

    override fun ondownloadImageBtnClicked(pixabayPhoto: PixabayPhoto, view: View) {

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            ImageDownloadLinksAndInfo.ImageUrls(
                pixabayPhoto.previewURL,
                pixabayPhoto.webformatURL,
                pixabayPhoto.largeImageURL
            ),
            UUID.randomUUID().toString() + "_pixabay",
            ""
        )

        showDownloadOptionPopupMenu(
                requireActivity(),
                view,
                imageDownloadLinksAndInfo
        )
    }

    override fun onImageUserNameClicked(pixabayPhoto: PixabayPhoto) {

        openLinkInBrowser(requireContext(), pixabayPhoto.attributionUrl)
    }

    override fun onAddToFavouriteLongClicked(pixabayPhoto: PixabayPhoto, position: Int) {

        isObservingForImageSavedInCollection = true
        this.position = position

        if (savedImagesIds.contains(pixabayPhoto.id.toString())) {

            isRefreshEnabled = true

            getTheSavedImageAndPassItToTheChooseCollectionBottomSheet(pixabayPhoto.id.toString())

        } else {

            val savedImage = generateSavedImage(pixabayPhoto, APIName.PIXABAY)

            val action =
                SearchFragmentDirections.actionSearchFragmentToChooseFromCollectionsFragment(
                    convertSavedImageToString(savedImage)
                )

            findNavController().navigate(action)
        }
    }

    //---------------------------------------------------------------------------------------------


    //----------------------------- Pexel adapter click listeners ------------------------------

    override fun onImageClicked(pexelPhoto: PexelPhoto) {

        val intent = Intent(requireContext(), PreviewImageActivity::class.java)

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            ImageDownloadLinksAndInfo.ImageUrls(
                pexelPhoto.src.medium,
                pexelPhoto.src.large,
                pexelPhoto.src.original
            ),
            UUID.randomUUID().toString() + "_pexel",
            pexelPhoto.id.toString()
        )

        intent.putExtra(
            PREVIEW_IMAGE_KEY,
            convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )

        intent.putExtra(PREVIEW_IMAGE_TAG_KEY, SEARCH_FRAGMENT_TAG_PEXEL)

        startActivity(intent)
    }

    override fun onAddToFavouriteBtnClicked(pexelPhoto: PexelPhoto, position: Int) {

        if (savedImagesIds.contains(pexelPhoto.id.toString())) {

            savedImageViewModel.deleteImageByImageId(pexelPhoto.id.toString())

            updateItemOfPixabaySearchAdapter(position)

            showToasty(requireContext(), "Image unsaved", ToastyType.INFO)

        } else {

            val savedImage = generateSavedImage(pexelPhoto, APIName.PEXELS)

            savedImageViewModel.insertImage(savedImage)

            showSnackBar(binding.root, "Image Saved")

            updateItemOfPexelSearchAdapter(position)

        }

    }

    private fun updateItemOfPexelSearchAdapter(position: Int) {

        lifecycleScope.launch {

            delay(100)

            withContext(Dispatchers.Main) {

                pexelSearchAdapter.updateSavedImageListIds(savedImagesIds)

                pexelSearchAdapter.notifyItemChanged(position)
            }
        }

    }

    override fun onDownloadImageBtnClicked(pexelPhoto: PexelPhoto, view: View) {

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            ImageDownloadLinksAndInfo.ImageUrls(
                pexelPhoto.src.medium,
                pexelPhoto.src.large,
                pexelPhoto.src.original
            ),
            UUID.randomUUID().toString() + "_pexel",
            ""
        )

        showDownloadOptionPopupMenu(
            requireActivity(),
            view,
            imageDownloadLinksAndInfo
        )
    }

    override fun onImageUserNameClicked(pexelPhoto: PexelPhoto) {

        openLinkInBrowser(requireContext(), pexelPhoto.photographer_url)
    }

    override fun onAddToFavouriteLongClicked(pexelPhoto: PexelPhoto, position: Int) {

        isObservingForImageSavedInCollection = true
        this.position = position

        if (savedImagesIds.contains(pexelPhoto.id.toString())) {

            isRefreshEnabled = true

            getTheSavedImageAndPassItToTheChooseCollectionBottomSheet(pexelPhoto.id.toString())

        } else {

            val savedImage = generateSavedImage(pexelPhoto, APIName.PEXELS)

            val action =
                    SearchFragmentDirections.actionSearchFragmentToChooseFromCollectionsFragment(
                            convertSavedImageToString(savedImage)
                    )

            findNavController().navigate(action)
        }
    }

    //---------------------------------------------------------------------------------------------


    //------------------------------ Web image adapter click listener ---------------------------

    override fun onImageClicked(webPhoto: WebPhoto) {

        val intent = Intent(requireContext(), PreviewImageActivity::class.java)

        val imageDownloadLinksAndInfo = ImageDownloadLinksAndInfo(
            ImageDownloadLinksAndInfo.ImageUrls(
                webPhoto.preview!!,
                webPhoto.imgurl!!,
                webPhoto.imgurl
            ),
            UUID.randomUUID().toString() + "_web",
            ""
        )

        intent.putExtra(
            PREVIEW_IMAGE_KEY,
            convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo)
        )

        intent.putExtra(PREVIEW_IMAGE_TAG_KEY, SEARCH_FRAGMENT_TAG_WEB)

        startActivity(intent)
    }

    override fun onDownloadImageBtnClicked(webPhoto: WebPhoto, view: View) {

        downloadFile(
                requireActivity(),
                webPhoto.imgurl,
            UUID.randomUUID().toString() + "_web.jpg"
        )
    }

    override fun onVisitWebsiteTVClicked(websiteUrl: String) {

        openLinkInBrowser(requireContext(), websiteUrl)
    }


    //-------------------------------------------------------------------------------------------


    //------------------------------ UnDraw image adapter click listener ---------------------------

    override fun onImageClicked(selectedPosition: Int) {

        Log.d(TAG, "onImageClicked: Selected Position : $selectedPosition")

        val intent = Intent(requireContext(), PreviewImageActivity::class.java)

        val previewUnDrawImagesMessage = PreviewUnDrawImagesMessage(
            unDrawImageAdapter.currentList,
            selectedPosition
        )

        intent.putExtra(
            PREVIEW_IMAGE_KEY, fromPreviewUnDrawImagesMessageToString(
                previewUnDrawImagesMessage
            )
        )

        intent.putExtra(PREVIEW_IMAGE_TAG_KEY, SEARCH_FRAGMENT_TAG_UNDRAW)

        startActivity(intent)
    }

    override fun onDownloadBtnClicked(unDraw: Illo) {

        showToast(requireContext(), "Downloading image...")

        downloadFile(
            requireActivity(),
            unDraw.image,
            unDraw.slug + ".svg"
        )

    }

    override fun onImageTitleClicked(unDraw: Illo) {

        openLinkInBrowser(
            requireContext(),
            unDraw.image
        )
    }

    // ---------------------------------------------------------------------------

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

                                APIName.PIXABAY -> updateItemOfPixabaySearchAdapter(position)

                                APIName.PEXELS -> updateItemOfPexelSearchAdapter(position)

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

    /*this function will get the image already saved in the database and then pass it to the
    Bottom sheet for choosing another collection*/
    private fun getTheSavedImageAndPassItToTheChooseCollectionBottomSheet(id: String) {

        savedImageViewModel.getSavedImageByImageId(id).observe(viewLifecycleOwner, {

            if (isRefreshEnabled) {

                if (it != null) {

                    val action =
                            SearchFragmentDirections.actionSearchFragmentToChooseFromCollectionsFragment(
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

    override fun onPause() {
        super.onPause()

        hideKeyBoard(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}