package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.pexelsData.PexelPhoto
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class PexelSearchResultsAdapter(var savedImageIdList: List<String> = emptyList()) :
    PagingDataAdapter<PexelPhoto, PexelSearchResultsAdapter.PexelSearchViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    fun updateSavedImageListIds(list: List<String>) {

        this.savedImageIdList = list
    }

    inner class PexelSearchViewHolder(val binding: PhotoItemForRvBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        fun setData(pexelPhoto: PexelPhoto?) {

            pexelPhoto?.let {

                binding.apply {

                    //displaying the actual image
                    setUpAndShowImageInImageView(it)

                    //displaying the user image
                    Glide.with(binding.view)
                        .load(it.photographer_url)
                        .centerInside()
                        .error(R.drawable.ic_outline_account_circle_24)
                        .into(imageUserImage)

                    imageUserNameTV.text = it.photographer

                    if (savedImageIdList.isNotEmpty()) {

                        if (savedImageIdList.contains(it.id.toString())) {

                            binding.addToFavouritesBtn.setImageResource(R.drawable.ic_baseline_bookmark_24)
                        } else {

                            binding.addToFavouritesBtn.setImageResource(R.drawable.ic_outline_bookmark_border_24)
                        }
                    }
                }
            }
        }

        //displaying the actual image
        private fun setUpAndShowImageInImageView(pexelPhoto: PexelPhoto) {

            Glide.with(binding.view)
                .load(pexelPhoto.src.medium)
                .apply {
                    this.error(R.drawable.ic_outline_error_outline_24)
                    this.centerCrop()
                }
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {

                        showReloadBtn()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        hideReloadBtn()
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

                        getItem(absoluteAdapterPosition)?.let {
                            mListener!!.onAddToFavouriteBtnClicked(
                                it,
                                absoluteAdapterPosition
                            )
                        }
                    }

                    binding.downloadImageBtn.id -> {

                        getItem(absoluteAdapterPosition)?.let {
                            mListener!!.onDownloadImageBtnClicked(
                                it,
                                binding.downloadImageBtn
                            )
                        }
                    }

                    binding.imageUserNameTV.id -> {

                        getItem(absoluteAdapterPosition)?.let {
                            mListener!!.onImageUserNameClicked(
                                it
                            )
                        }
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

                getItem(absoluteAdapterPosition)?.let {
                    mListener!!.onAddToFavouriteLongClicked(
                        it,
                        absoluteAdapterPosition
                    )
                }
            }

            return true
        }

        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }

    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<PexelPhoto>() {
            override fun areItemsTheSame(oldItem: PexelPhoto, newItem: PexelPhoto): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PexelPhoto, newItem: PexelPhoto): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: PexelSearchViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PexelSearchViewHolder {

        val binding = PhotoItemForRvBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return PexelSearchViewHolder(binding)
    }

    interface OnClickListener {

        fun onImageClicked(pexelPhoto: PexelPhoto)
        fun onAddToFavouriteBtnClicked(pexelPhoto: PexelPhoto, position: Int)
        fun onDownloadImageBtnClicked(pexelPhoto: PexelPhoto, view: View)
        fun onImageUserNameClicked(pexelPhoto: PexelPhoto)

        fun onAddToFavouriteLongClicked(pexelPhoto: PexelPhoto, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {

        mListener = listener
    }
}