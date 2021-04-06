package com.rohitthebest.phoco_theimagesearchingapp.ui.activities

import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_MESSAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ActivityPreviewImageBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToImageDownloadLinksAndInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PreviewImageActivity"

class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPreviewImageBinding
    private lateinit var imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo
    private lateinit var wallpaperManager: WallpaperManager

    private var isDownloadOptionsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_MESSAGE_KEY)
            ?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

        setImageInImageView()

        initListeners()

        wallpaperManager = WallpaperManager.getInstance(applicationContext)

    }

    private fun initListeners() {

        binding.downloadImageFAB.setOnClickListener(this)
        binding.setImageAsHomescreenFAB.setOnClickListener(this)
        binding.extractImageColorsFAB.setOnClickListener(this)
        binding.reloadFAB.setOnClickListener(this)

        binding.smallDownloadCV.setOnClickListener(this)
        binding.mediumDownloadCV.setOnClickListener(this)
        binding.originalDownloadCV.setOnClickListener(this)

        binding.previewImageIV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

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
                                        imageDownloadLinksAndInfo.imageUrls.original
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

                //todo : extract image colors
                hideDownloadOptions()
            }

            binding.reloadFAB.id -> {

                hideReloadBtn()
                setImageInImageView()

                hideDownloadOptions()
            }

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

            binding.previewImageIV.id -> {

                hideDownloadOptions()
            }
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

    private fun enableOrDisableDownloadOptions(isEnable: Boolean) {

        binding.smallDownloadCV.isEnabled = isEnable
        binding.mediumDownloadCV.isEnabled = isEnable
        binding.originalDownloadCV.isEnabled = isEnable
    }

    private fun downloadTheImage(imageUrl: String) {

        showToast(this, "Downloading Image")

        DownloadFile().downloadFile(
            this,
            imageUrl,
            if (imageDownloadLinksAndInfo.imageName != "" && !imageDownloadLinksAndInfo.imageName.contains(
                    "/"
                )
            )
                "${imageDownloadLinksAndInfo.imageName}.jpg"
            else "${System.currentTimeMillis()}.jpg"
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