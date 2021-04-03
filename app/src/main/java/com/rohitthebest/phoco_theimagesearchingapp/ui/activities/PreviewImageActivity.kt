package com.rohitthebest.phoco_theimagesearchingapp.ui.activities

import android.app.WallpaperManager
import android.graphics.Bitmap
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_MESSAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ActivityPreviewImageBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToImageDownloadLinksAndInfo
import java.io.IOException

private const val TAG = "PreviewImageActivity"

class PreviewImageActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPreviewImageBinding
    private lateinit var imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo
    private lateinit var wallpaperManager: WallpaperManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_MESSAGE_KEY)?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

        setImageInImageView()

        initListeners()

        wallpaperManager = WallpaperManager.getInstance(applicationContext)

    }

    private fun initListeners() {

        binding.downloadImageFAB.setOnClickListener(this)
        binding.setImageAsHomescreenFAB.setOnClickListener(this)
        binding.extractImageColorsFAB.setOnClickListener(this)
        binding.reloadFAB.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.downloadImageFAB.id -> {

                showToast(this, "Downloading Image")

                //todo : Show confirmation message with an option to choose quality of image

                DownloadFile().downloadFile(
                        this,
                        imageDownloadLinksAndInfo.imageUrls.original,
                        if (imageDownloadLinksAndInfo.imageName != "" && !imageDownloadLinksAndInfo.imageName.contains("/"))
                            "${imageDownloadLinksAndInfo.imageName}.jpg"
                        else "${System.currentTimeMillis()}.jpg"
                )
            }

            binding.setImageAsHomescreenFAB.id -> {

                setImageAsHomeScreenWallpaper()
            }

            binding.extractImageColorsFAB.id -> {

                //todo : extract image colors
            }

            binding.reloadFAB.id -> {

                hideReloadBtn()
                setImageInImageView()
            }
        }
    }

    private fun setImageAsHomeScreenWallpaper() {

        Glide.with(this)
                .asBitmap()
                .load(imageDownloadLinksAndInfo.imageUrls.medium)
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                        try {

                            wallpaperManager.setBitmap(resource)
                            showToast(applicationContext, "Image set as Home screen wallpaper")

                        } catch (e: IOException) {

                            e.printStackTrace()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

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
                .into(binding.priviewImageIV)


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