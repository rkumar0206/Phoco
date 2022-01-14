package com.rohitthebest.phoco_theimagesearchingapp.ui.activities

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.Constants.EXTRACTED_COLORS_IMAGE_URL_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.HOME_FRAGMENT_TAG
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_TAG_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SAVED_IMAGE_TAG
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SEARCH_FRAGMENT_TAG_UNDRAW
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ActivityPreviewImageBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.viewPagerAdapters.PreviewImageViewPagerAdapter
import com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments.ExtractedColorsBottomSheetDialog
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.fromStringToPreviewUnDrawImagesMessage
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.UnsplashPhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "PreviewImageActivity"

@AndroidEntryPoint
class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPreviewImageBinding

    private lateinit var imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo
    private var apiTag = ""
    private lateinit var wallpaperManager: WallpaperManager

    private var isDownloadOptionsVisible = false
    private var isFABOptionVisible = true

    private val unsplashPhotoViewModel by viewModels<UnsplashPhotoViewModel>()
    private val savedImageViewModel by viewModels<SavedImageViewModel>()

    // this list consists the 30 random photos which appear on the home fragment
    private lateinit var homeImageList: List<UnsplashPhoto>
    private lateinit var previewUnDrawImagesMessage: PreviewUnDrawImagesMessage

    private var currentImageIndex: Int = 0

    private var receivedCollectionKey = ""  // will be used only when image comes from collection
    private lateinit var receivedSavedImageList: List<SavedImage>

    private lateinit var previewViewPagerAdapter: PreviewImageViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewViewPagerAdapter = PreviewImageViewPagerAdapter(emptyList())

        apiTag = intent.getStringExtra(PREVIEW_IMAGE_TAG_KEY)!!

        //imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_KEY)
        //  ?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

        when (apiTag) {

            HOME_FRAGMENT_TAG -> {

                imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_KEY)
                    ?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

                // in this condition images saved in UnsplashPhoto Database will be displayed (HomeFragment images)
                getImageList()
            }
            SAVED_IMAGE_TAG -> {

                imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_KEY)
                    ?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

                // in this condition images saved in collections will be displayed

                receivedCollectionKey =
                    imageDownloadLinksAndInfo.imageName // received as collection key here

                getSavedImageListRelatedToPassedCollectionKey()
            }

            SEARCH_FRAGMENT_TAG_UNDRAW -> {

                previewUnDrawImagesMessage =
                    fromStringToPreviewUnDrawImagesMessage(intent.getStringExtra(PREVIEW_IMAGE_KEY)!!)

                currentImageIndex = previewUnDrawImagesMessage.selectedPosition

                setUpPreviewImageViewPager(
                    previewUnDrawImagesMessage.unDrawImages.map { illo -> illo.image }
                )

                updateTheImageIndexNumberInTextView()

                binding.extractImageColorsFAB.hide()
                binding.setImageAsHomescreenFAB.hide()

            }

            else -> {

                imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_KEY)
                    ?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

                // preview only single image

                setUpPreviewImageViewPager(
                    listOf(imageDownloadLinksAndInfo.imageUrls.medium)
                )
            }
        }

        initListeners()

        wallpaperManager = WallpaperManager.getInstance(applicationContext)

        enableOrDisableDownloadOptions(false)
    }

    private fun getImageList() {

        unsplashPhotoViewModel.getAllUnsplashPhoto().observe(this, { unsplashPhotos ->

            homeImageList = unsplashPhotos

            currentImageIndex =
                homeImageList.indexOfFirst { image -> image.id == imageDownloadLinksAndInfo.imageId }

            setUpPreviewImageViewPager(homeImageList.map { it.urls.regular })

            updateTheImageIndexNumberInTextView()

        })
    }

    private fun setUpPreviewImageViewPager(imageUrlList: List<String>) {

        previewViewPagerAdapter = PreviewImageViewPagerAdapter(imageUrlList, apiTag)

        binding.previewImageViewPager.adapter = previewViewPagerAdapter
        binding.previewImageViewPager.currentItem = currentImageIndex

        binding.previewImageViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    Log.d(TAG, "onPageSelected: position : $position")

                    currentImageIndex = position
                    updateTheImageIndexNumberInTextView()
                    updateImageDownloadInfo()
                }
            }
        )
    }

    private fun getSavedImageListRelatedToPassedCollectionKey() {

        if (receivedCollectionKey == Constants.COLLECTION_KEY_FOR_ALL_PHOTOS) {

            savedImageViewModel.getAllSavedImages().observe(this, {

                receivedSavedImageList = it

                currentImageIndex =
                    receivedSavedImageList.indexOfFirst { image -> image.imageId == imageDownloadLinksAndInfo.imageId }

                setUpPreviewImageViewPager(receivedSavedImageList.map { it.imageUrls.medium })


                updateTheImageIndexNumberInTextView()
            })
        } else {

            savedImageViewModel.getSavedImagesByCollectionKey(receivedCollectionKey).observe(
                    this, {

                    receivedSavedImageList = it

                    currentImageIndex =
                        receivedSavedImageList.indexOfFirst { image -> image.imageId == imageDownloadLinksAndInfo.imageId }

                    setUpPreviewImageViewPager(receivedSavedImageList.map { it.imageUrls.medium })

                    updateTheImageIndexNumberInTextView()
            })
        }
    }

    private fun initListeners() {

        binding.downloadImageFAB.setOnClickListener(this)
        binding.setImageAsHomescreenFAB.setOnClickListener(this)
        binding.extractImageColorsFAB.setOnClickListener(this)
        binding.shareImageFAB.setOnClickListener(this)

        binding.smallDownloadCV.setOnClickListener(this)
        binding.mediumDownloadCV.setOnClickListener(this)
        binding.originalDownloadCV.setOnClickListener(this)

        binding.previewImageViewPager.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            /*[START OF FAB BUTTON CLICKS]*/
            binding.downloadImageFAB.id -> {

                if (apiTag != SEARCH_FRAGMENT_TAG_UNDRAW) {

                    if (!isDownloadOptionsVisible) {

                        showDownloadOptions()
                    } else {

                        hideDownloadOptions()
                    }
                } else {

                    // for unDraw images don't show download options

                    val unDraw = previewUnDrawImagesMessage.unDrawImages[currentImageIndex]

                    downloadFile(
                        this,
                        unDraw.image,
                        unDraw.slug + ".svg"
                    )

                }
            }

            binding.setImageAsHomescreenFAB.id -> {

                MaterialAlertDialogBuilder(this)
                        .setTitle("Home Screen wallpaper")
                        .setMessage("Set this image as Home screen wallpaper?")
                        .setPositiveButton("Yes") { dialog, _ ->

                            CoroutineScope(Dispatchers.Main).launch {

                                Log.d(TAG, "onClick: Setting image as Home screen wallpaper")

                                setImageAsHomeScreenWallpaperFromImageUrl(
                                        applicationContext,
                                        imageDownloadLinksAndInfo.imageUrls.medium
                                )
                            }

                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->

                            dialog.dismiss()
                        }
                        .create()
                        .show()

                hideDownloadOptions()
            }

            binding.extractImageColorsFAB.id -> {

                val bundle = Bundle()
                bundle.putString(EXTRACTED_COLORS_IMAGE_URL_KEY, imageDownloadLinksAndInfo.imageUrls.medium)

                supportFragmentManager.let {

                    ExtractedColorsBottomSheetDialog.newInstance(bundle).apply {
                        show(it, TAG)
                    }
                }

                hideDownloadOptions()
            }

            binding.shareImageFAB.id -> {

                shareAsText(
                    this,
                    "Follow this link to download the image :\n\n${
                        if (apiTag != SEARCH_FRAGMENT_TAG_UNDRAW) {

                            imageDownloadLinksAndInfo.imageUrls.original
                        } else {
                            previewUnDrawImagesMessage.unDrawImages[currentImageIndex].image
                        }
                    }",
                    "Image download link"
                )
                hideDownloadOptions()
            }
            /*[END OF FAB BUTTON CLICKS]*/


            /*[START OF download quality options]*/
            binding.smallDownloadCV.id -> {

                downloadTheImage(imageDownloadLinksAndInfo.imageUrls.small)
                hideDownloadOptions()
            }

            binding.mediumDownloadCV.id -> {

                downloadTheImage(imageDownloadLinksAndInfo.imageUrls.medium)
                hideDownloadOptions()
            }

            binding.originalDownloadCV.id -> {

                downloadTheImage(imageDownloadLinksAndInfo.imageUrls.original)
                hideDownloadOptions()
            }
            /*[END OF download quality options]*/


            binding.previewImageViewPager.id -> {

                if (isFABOptionVisible && !isDownloadOptionsVisible) {

                    hideFabButtonOptions()

                } else if (isFABOptionVisible && isDownloadOptionsVisible) {

                    hideDownloadOptions()
                } else {

                    showFabButtonOptions()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateTheImageIndexNumberInTextView() {

        when (apiTag) {
            HOME_FRAGMENT_TAG -> {

                binding.imageNumberTV.text = "${currentImageIndex + 1} / ${homeImageList.size}"
            }
            SAVED_IMAGE_TAG -> {

                binding.imageNumberTV.text =
                    "${currentImageIndex + 1} / ${receivedSavedImageList.size}"
            }
            SEARCH_FRAGMENT_TAG_UNDRAW -> {

                binding.imageNumberTV.text =
                    "${currentImageIndex + 1} / ${previewUnDrawImagesMessage.unDrawImages.size}"
            }
            else -> {

                binding.imageNumberTV.text = "1 / 1"
            }
        }
    }

    private fun updateImageDownloadInfo() {

        if (apiTag == HOME_FRAGMENT_TAG) {

            val currentImage = homeImageList[currentImageIndex]

            imageDownloadLinksAndInfo.imageUrls = ImageDownloadLinksAndInfo
                .ImageUrls(
                    currentImage.urls.small,
                    currentImage.urls.regular,
                    currentImage.links.download
                )

            imageDownloadLinksAndInfo.imageName = currentImage.alt_description ?: ""
            imageDownloadLinksAndInfo.imageId = currentImage.id

            //setImageInImageView()
        } else if (apiTag == SAVED_IMAGE_TAG) {

            val currentImage = receivedSavedImageList[currentImageIndex]

            imageDownloadLinksAndInfo.imageUrls = ImageDownloadLinksAndInfo
                .ImageUrls(
                    currentImage.imageUrls.small,
                    currentImage.imageUrls.medium,
                    currentImage.imageUrls.original
                )

            imageDownloadLinksAndInfo.imageName = currentImage.imageName
            imageDownloadLinksAndInfo.imageId = currentImage.imageId

            //setImageInImageView()
        }
    }

    private fun showDownloadOptions() {

        isDownloadOptionsVisible = !isDownloadOptionsVisible

        binding.imageDownloadOptionsLL.animate().alpha(1f).setDuration(600).start()
        enableOrDisableDownloadOptions(true)

    }

    private fun hideDownloadOptions() {

        if (isDownloadOptionsVisible) {

            isDownloadOptionsVisible = !isDownloadOptionsVisible

            binding.imageDownloadOptionsLL.animate().alpha(0f).setDuration(600).start()
            enableOrDisableDownloadOptions(false)
        }
    }

    private fun showFabButtonOptions() {

        isFABOptionVisible = !isFABOptionVisible

        binding.fabOptionButtonLL.animate().alpha(1f).setDuration(600).start()
        binding.imageNumberTV.animate().alpha(1f).setDuration(600).start()
        enableOrDisableFABOptions(true)
    }

    private fun hideFabButtonOptions() {

        if (isFABOptionVisible) {

            isFABOptionVisible = !isFABOptionVisible
            binding.fabOptionButtonLL.animate().alpha(0f).setDuration(600).start()
            binding.imageNumberTV.animate().alpha(0f).setDuration(600).start()
            enableOrDisableFABOptions(false)
        }
    }

    private fun enableOrDisableDownloadOptions(isEnable: Boolean) {

        binding.smallDownloadCV.isEnabled = isEnable
        binding.mediumDownloadCV.isEnabled = isEnable
        binding.originalDownloadCV.isEnabled = isEnable
    }

    private fun enableOrDisableFABOptions(isEnable: Boolean) {

        binding.downloadImageFAB.isEnabled = isEnable
        binding.setImageAsHomescreenFAB.isEnabled = isEnable
        binding.extractImageColorsFAB.isEnabled = isEnable
        binding.shareImageFAB.isEnabled = isEnable
    }

    private fun downloadTheImage(imageUrl: String) {

        showToast(this, "Downloading Image")

        downloadFile(
            this,
            imageUrl,
            if (imageDownloadLinksAndInfo.imageName != "" && !imageDownloadLinksAndInfo.imageName.contains(
                    "/"
                )
            )
                "${imageDownloadLinksAndInfo.imageName}.jpg"
            else "${UUID.randomUUID()}.jpg"
        )

    }

    override fun onDestroy() {
        super.onDestroy()

        binding.previewImageViewPager.unregisterOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {}
        )
    }

}