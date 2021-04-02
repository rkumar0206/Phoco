package com.rohitthebest.phoco_theimagesearchingapp.ui.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PREVIEW_IMAGE_MESSAGE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ActivityPreviewImageBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.DownloadFile
import com.rohitthebest.phoco_theimagesearchingapp.utils.GsonConverters.Companion.convertStringToImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.ImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToast

private const val TAG = "PreviewImageActivity"

class PreviewImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewImageBinding
    private lateinit var imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageDownloadLinksAndInfo = intent.getStringExtra(PREVIEW_IMAGE_MESSAGE_KEY)?.let { convertStringToImageDownloadLinksAndInfo(it) }!!

        setImageInImageView()

        binding.downloadImageFAB.setOnClickListener {

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
                        //TODO("Not yet implemented")
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        //TODO("Not yet implemented")
                        return false
                    }

                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.priviewImageIV)


    }
}