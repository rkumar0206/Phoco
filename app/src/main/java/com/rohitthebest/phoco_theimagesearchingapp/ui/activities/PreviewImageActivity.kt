package com.rohitthebest.phoco_theimagesearchingapp.ui.activities

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.Constants.EXTRACTED_COLORS_IMAGE_URL_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.HOME_FRAGMENT_TAG
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_MESSAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SAVED_IMAGE_TAG
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ActivityPreviewImageBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments.ExtractedColorsBottomSheetDialog
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.SavedImageViewModel
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.UnsplashPhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "PreviewImageActivity"

@AndroidEntryPoint
class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPreviewImageBinding
    private lateinit var imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo
    private lateinit var wallpaperManager: WallpaperManager

    private var isDownloadOptionsVisible = false
    private var isFABOptionVisible = true

    private val unsplashPhotoViewModel by viewModels<UnsplashPhotoViewModel>()
    private val savedImageViewModel by viewModels<SavedImageViewModel>()

    private lateinit var homeImageList: List<UnsplashPhoto>

    private var currentImageIndex: Int = 0

    private var receivedCollectionKey = ""  // will be used only when image comes from collection
    private lateinit var receivedSavedImageList: List<SavedImage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_MESSAGE_KEY)
                ?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

        when (imageDownloadLinksAndInfo.tag) {

            HOME_FRAGMENT_TAG -> {

                // in this condition images saved in UnsplashPhoto Database will be displayed (HomeFragment images)
                getImageList()
            }
            SAVED_IMAGE_TAG -> {

                // in this condition images saved in collections will be displayed

                receivedCollectionKey = imageDownloadLinksAndInfo.imageName // received as collection key here

                getSavedImageListRelatedToPassedCollectionKey()
            }
            else -> {

                binding.nextPreviewImageBtn.hide()
                binding.previousPreviewImageBtn.hide()
                binding.imageNumberTV.hide()
            }
        }

        setImageInImageView()

        initListeners()

        wallpaperManager = WallpaperManager.getInstance(applicationContext)

        enableOrDisableDownloadOptions(false)
    }

    private fun getImageList() {

        unsplashPhotoViewModel.getAllUnsplashPhoto().observe(this, {

            homeImageList = it

            currentImageIndex = homeImageList.indexOfFirst { image -> image.id == imageDownloadLinksAndInfo.imageId }

            updateTheImageIndexNumberInTextView()

        })
    }

    private fun getSavedImageListRelatedToPassedCollectionKey() {

        if (receivedCollectionKey == Constants.COLLECTION_KEY_FOR_ALL_PHOTOS) {

            savedImageViewModel.getAllSavedImages().observe(this, {

                receivedSavedImageList = it

                currentImageIndex = receivedSavedImageList.indexOfFirst { image -> image.imageId == imageDownloadLinksAndInfo.imageId }

                updateTheImageIndexNumberInTextView()
            })
        } else {

            savedImageViewModel.getSavedImagesByCollectionKey(receivedCollectionKey).observe(
                    this, {

                receivedSavedImageList = it

                currentImageIndex = receivedSavedImageList.indexOfFirst { image -> image.imageId == imageDownloadLinksAndInfo.imageId }

                updateTheImageIndexNumberInTextView()
            })
        }

    }

    private fun initListeners() {

        binding.downloadImageFAB.setOnClickListener(this)
        binding.setImageAsHomescreenFAB.setOnClickListener(this)
        binding.extractImageColorsFAB.setOnClickListener(this)
        binding.reloadFAB.setOnClickListener(this)
        binding.shareImageFAB.setOnClickListener(this)

        binding.smallDownloadCV.setOnClickListener(this)
        binding.mediumDownloadCV.setOnClickListener(this)
        binding.originalDownloadCV.setOnClickListener(this)

        binding.nextPreviewImageBtn.setOnClickListener(this)
        binding.previousPreviewImageBtn.setOnClickListener(this)
        binding.previewImageIV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            /*[START OF FAB BUTTON CLICKS]*/
            binding.downloadImageFAB.id -> {

                if (!isDownloadOptionsVisible) {

                    showDownloadOptions()
                } else {

                    hideDownloadOptions()
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

            binding.reloadFAB.id -> {

                hideReloadBtn()
                setImageInImageView()

                hideDownloadOptions()
            }

            binding.shareImageFAB.id -> {

                shareAsText(this,

                        "Follow this link to download the image :\n\n${imageDownloadLinksAndInfo.imageUrls.original}",
                        "Image download link")
                hideDownloadOptions()
            }
            /*[EBD OF FAB BUTTON CLICKS]*/


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


            binding.previewImageIV.id -> {

                if (isFABOptionVisible && !isDownloadOptionsVisible) {

                    hideFabButtonOptions()

                } else if (isFABOptionVisible && isDownloadOptionsVisible) {

                    hideDownloadOptions()
                } else {

                    showFabButtonOptions()
                }
            }

            binding.nextPreviewImageBtn.id -> {

                handleNextImageBtn()
                showClickEffect(binding.nextClickEffextView)
            }

            binding.previousPreviewImageBtn.id -> {

                handlePreviousImageBtn()
                showClickEffect(binding.previousClcikEffectView)
            }
        }
    }


    private fun showClickEffect(view: View) {

        view.animate().alpha(1f).setDuration(600).start()

        GlobalScope.launch {
            delay(150)

            withContext(Dispatchers.Main) {

                view.animate().alpha(0f).setDuration(600).start()

            }
        }

    }

    private fun handleNextImageBtn() {

        if (imageDownloadLinksAndInfo.tag == HOME_FRAGMENT_TAG) {

            if (currentImageIndex < homeImageList.size - 1) {

                currentImageIndex++

            } else {
                currentImageIndex = 0
            }
        } else {

            if (currentImageIndex < receivedSavedImageList.size - 1) {

                currentImageIndex++
            } else {

                currentImageIndex = 0
            }
        }

        updateTheImageIndexNumberInTextView()
        updateImageDownloadInfo()
    }

    private fun handlePreviousImageBtn() {

        if (imageDownloadLinksAndInfo.tag == HOME_FRAGMENT_TAG) {

            if (currentImageIndex > 0) {

                currentImageIndex--
            } else {

                currentImageIndex = homeImageList.size - 1
            }
        } else {

            if (currentImageIndex > 0) {

                currentImageIndex--
            } else {

                currentImageIndex = receivedSavedImageList.size - 1
            }
        }

        updateTheImageIndexNumberInTextView()
        updateImageDownloadInfo()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTheImageIndexNumberInTextView() {

        if (imageDownloadLinksAndInfo.tag == HOME_FRAGMENT_TAG) {

            binding.imageNumberTV.text = "${currentImageIndex + 1} / ${homeImageList.size}"
        } else {

            binding.imageNumberTV.text = "${currentImageIndex + 1} / ${receivedSavedImageList.size}"
        }
    }

    private fun updateImageDownloadInfo() {

        if (imageDownloadLinksAndInfo.tag == HOME_FRAGMENT_TAG) {

            val currentImage = homeImageList[currentImageIndex]

            imageDownloadLinksAndInfo.imageUrls = ImageDownloadLinksAndInfo
                    .ImageUrls(currentImage.urls.small, currentImage.urls.regular, currentImage.links.download)

            imageDownloadLinksAndInfo.imageName = currentImage.alt_description ?: ""
            imageDownloadLinksAndInfo.imageId = currentImage.id

            setImageInImageView()
        } else {

            val currentImage = receivedSavedImageList[currentImageIndex]

            imageDownloadLinksAndInfo.imageUrls = ImageDownloadLinksAndInfo
                    .ImageUrls(currentImage.imageUrls.small, currentImage.imageUrls.medium, currentImage.imageUrls.original)

            imageDownloadLinksAndInfo.imageName = currentImage.imageName
            imageDownloadLinksAndInfo.imageId = currentImage.imageId

            setImageInImageView()
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
        binding.nextPreviewImageBtn.animate().alpha(1f).setDuration(600).start()
        binding.previousPreviewImageBtn.animate().alpha(1f).setDuration(600).start()
        binding.imageNumberTV.animate().alpha(1f).setDuration(600).start()
        enableOrDisableFABOptions(true)
    }

    private fun hideFabButtonOptions() {

        if (isFABOptionVisible) {

            isFABOptionVisible = !isFABOptionVisible
            binding.fabOptionButtonLL.animate().alpha(0f).setDuration(600).start()
            binding.nextPreviewImageBtn.animate().alpha(0f).setDuration(600).start()
            binding.previousPreviewImageBtn.animate().alpha(0f).setDuration(600).start()
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
        binding.nextPreviewImageBtn.isEnabled = isEnable
        binding.previousPreviewImageBtn.isEnabled = isEnable
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
                else "${generateKey()}.jpg"
        )

    }

    private fun setImageInImageView() {

        Log.d(TAG, "setImageInImageView: ")

        Glide.with(this)
                .load(imageDownloadLinksAndInfo.imageUrls.medium)
                .apply {

                    error(R.drawable.ic_outline_error_outline_24)
                }
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {

                        showReloadBtn()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                        hideReloadBtn()
                        return false
                    }

                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.previewImageIV)

    }

    private fun showReloadBtn() {

        binding.reloadBackground.show()
        binding.reloadFAB.visibility = View.VISIBLE
    }

    private fun hideReloadBtn() {

        binding.reloadBackground.hide()
        binding.reloadFAB.visibility = View.GONE
    }


}