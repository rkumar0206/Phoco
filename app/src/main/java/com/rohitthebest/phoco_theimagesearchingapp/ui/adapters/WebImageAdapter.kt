package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.mohitImagApiData.WebPhoto
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
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

            setImageToImageViewUsingGlide(
                binding.root.context,
                binding.image,
                webPhoto.imgurl,
                {
                    showReloadBtn()
                    binding.downloadImageBtn.hide()
                },
                {
                    hideReloadBtn()
                    binding.downloadImageBtn.show()
                }
            )
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