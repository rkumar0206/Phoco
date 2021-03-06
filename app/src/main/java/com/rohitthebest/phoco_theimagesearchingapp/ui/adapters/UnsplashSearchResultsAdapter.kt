package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

private const val TAG = "UnsplashSearchResultsAd"

class UnsplashSearchResultsAdapter(var savedImageIdList: List<String> = emptyList()) :
    PagingDataAdapter<UnsplashPhoto, UnsplashSearchResultsAdapter.UnsplashSearchViewHolder>(
        DiffUtilCallback()
    ) {

    private var mListener: OnClickListener? = null

    fun updateSavedImageListIds(list: List<String>) {

        this.savedImageIdList = list
    }

    inner class UnsplashSearchViewHolder(val binding: PhotoItemForRvBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        fun setData(unsplashPhoto: UnsplashPhoto?) {

            unsplashPhoto?.let {

                binding.apply {

                    //displaying the actual image
                    setUpAndShowImageInImageView(unsplashPhoto)

                    val aspectRatio = unsplashPhoto.width.toFloat() / unsplashPhoto.height.toFloat()

                    ConstraintSet().apply {

                        clone(root)
                        setDimensionRatio(image.id, aspectRatio.toString())
                        applyTo(root)
                    }

                    //displaying the user image
                    Glide.with(binding.view)
                        .load(unsplashPhoto.user.profile_image.small)
                        .centerInside()
                        .error(R.drawable.ic_outline_account_circle_24)
                        .into(imageUserImage)

                    imageUserNameTV.text = unsplashPhoto.user.username

                    if (savedImageIdList.isNotEmpty()) {

                        if (savedImageIdList.contains(unsplashPhoto.id)) {

                            binding.addToFavouritesBtn.setImageResource(R.drawable.ic_baseline_bookmark_24)
                        } else {

                            binding.addToFavouritesBtn.setImageResource(R.drawable.ic_outline_bookmark_border_24)
                        }
                    }
                }
            }
        }

        //displaying the actual image
        private fun setUpAndShowImageInImageView(unsplashPhoto: UnsplashPhoto) {

            setImageToImageViewUsingGlide(
                binding.root.context,
                binding.image,
                unsplashPhoto.urls.regular,
                {
                    showReloadBtn()
                },
                {

                    hideReloadBtn()
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
            binding.addToFavouritesBtn.setOnClickListener(this)
            binding.addToFavouritesBtn.setOnLongClickListener(this)
            binding.downloadImageBtn.setOnClickListener(this)
            binding.imageUserNameTV.setOnClickListener(this)

            binding.reloadFAB.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (checkForNullability()) {

                when (v?.id) {

                    binding.image.id -> {

                        getItem(absoluteAdapterPosition)?.let { mListener!!.onImageClicked(it) }
                    }

                    binding.addToFavouritesBtn.id -> {

                        getItem(absoluteAdapterPosition)?.let { mListener!!.onAddToFavouriteBtnClicked(it, absoluteAdapterPosition) }
                    }

                    binding.downloadImageBtn.id -> {

                        getItem(absoluteAdapterPosition)?.let { mListener!!.onDownloadImageBtnClicked(it, binding.downloadImageBtn) }
                    }

                    binding.imageUserNameTV.id -> {

                        getItem(absoluteAdapterPosition)?.let { mListener!!.onImageUserNameClicked(it) }
                    }

                    binding.reloadFAB.id -> {

                        hideReloadBtn()
                        getItem(absoluteAdapterPosition)?.let { setUpAndShowImageInImageView(it) }
                    }
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {

            if (v?.id == binding.addToFavouritesBtn.id) {

                getItem(absoluteAdapterPosition)?.let { mListener!!.onAddToFavouriteLongClicked(it, absoluteAdapterPosition) }
            }

            return true
        }

        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }

    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean =
                    oldItem.urls == newItem.urls

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean =
                    oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: UnsplashSearchViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnsplashSearchViewHolder {

        val binding = PhotoItemForRvBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return UnsplashSearchViewHolder(binding)
    }

    interface OnClickListener {

        fun onImageClicked(unsplashPhoto: UnsplashPhoto)
        fun onAddToFavouriteBtnClicked(unsplashPhoto: UnsplashPhoto, position: Int)
        fun onDownloadImageBtnClicked(unsplashPhoto: UnsplashPhoto, view: View)
        fun onImageUserNameClicked(unsplashPhoto: UnsplashPhoto)

        fun onAddToFavouriteLongClicked(unsplashPhoto: UnsplashPhoto, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {

        mListener = listener
    }
}