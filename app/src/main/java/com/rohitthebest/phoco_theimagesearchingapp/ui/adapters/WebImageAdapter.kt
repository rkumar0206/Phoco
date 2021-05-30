package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.mohitImagApiData.WebPhoto
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class WebImageAdapter() :
        ListAdapter<WebPhoto, WebImageAdapter.WenImageViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class WenImageViewHolder(val binding: PhotoItemForRvBinding) : RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {

        @SuppressLint("SetTextI18n")
        fun setData(webPhoto: WebPhoto?) {

            webPhoto?.let {

                binding.apply {

                    //displaying the actual image
                    setUpAndShowImageInImageView(it)

                    val aspectRatio =
                        webPhoto.height?.toFloat()
                            ?.let { it1 -> webPhoto.width?.toFloat()?.div(it1) }

                    ConstraintSet().apply {

                        clone(root)
                        setDimensionRatio(image.id, aspectRatio.toString())
                        applyTo(root)
                    }

                    //displaying the user image
                    Glide.with(binding.view)
                        .load(R.drawable.ic_baseline_web_24)
                        .centerInside()
                        .into(imageUserImage)

                    imageUserNameTV.text = "Visit website"

                    addToFavouritesBtn.hide()
                }
            }
        }

        //displaying the actual image
        private fun setUpAndShowImageInImageView(webPhoto: WebPhoto) {

            Glide.with(binding.view)
                    .load(webPhoto.imgurl)
                    .apply {
                        this.error(R.drawable.ic_outline_error_outline_24)
                        this.centerCrop()
                    }
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {

                            showReloadBtn()
                            binding.downloadImageBtn.hide()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                            hideReloadBtn()
                            binding.downloadImageBtn.show()
                            return false
                        }

                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.image)

        }

        private fun showReloadBtn() {

            binding.reloadBackground.show()
            binding.reloadFAB.visibility = View.VISIBLE
        }

        private fun hideReloadBtn() {

            binding.reloadBackground.hide()
            binding.reloadFAB.visibility = View.GONE
        }

        init {

            binding.image.setOnClickListener(this)
            binding.downloadImageBtn.setOnClickListener(this)

            binding.reloadFAB.setOnClickListener(this)
            binding.imageUserNameTV.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (checkForNullability()) {

                when (v?.id) {

                    binding.image.id -> {

                        mListener!!.onImageClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.downloadImageBtn.id -> {

                        mListener!!.onDownloadImageBtnClicked(getItem(absoluteAdapterPosition), binding.downloadImageBtn)
                    }

                    binding.reloadFAB.id -> {

                        hideReloadBtn()
                        setUpAndShowImageInImageView(getItem(absoluteAdapterPosition))
                    }

                    binding.imageUserNameTV.id -> {

                        getItem(absoluteAdapterPosition).rurl?.let { mListener!!.onVisitWebsiteTVClicked(it) }
                    }
                }
            }
        }

        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }
    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<WebPhoto>() {
            override fun areItemsTheSame(oldItem: WebPhoto, newItem: WebPhoto): Boolean {

                return oldItem.imgurl == newItem.imgurl
            }

            override fun areContentsTheSame(oldItem: WebPhoto, newItem: WebPhoto): Boolean {

                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WenImageViewHolder {

        val binding = PhotoItemForRvBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return WenImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WenImageViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onImageClicked(webPhoto: WebPhoto)
        fun onDownloadImageBtnClicked(webPhoto: WebPhoto, view: View)
        fun onVisitWebsiteTVClicked(websiteUrl: String)
    }

    fun setOnClickListener(listener: OnClickListener) {

        mListener = listener
    }

}