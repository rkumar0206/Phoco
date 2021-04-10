package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.rohitthebest.phoco_theimagesearchingapp.data.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class PixabaySearchResultsAdapter :
        ListAdapter<PixabayPhoto, PixabaySearchResultsAdapter.PixabayViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class PixabayViewHolder(val binding: PhotoItemForRvBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        fun setData(pixabayPhoto: PixabayPhoto?) {

            pixabayPhoto?.let {

                binding.apply {

                    //displaying the actual image
                    setUpAndShowImageInImageView(it)

                    //displaying the user image
                    Glide.with(binding.view)
                            .load(pixabayPhoto.userImageURL)
                            .centerInside()
                            .error(R.drawable.ic_outline_account_circle_24)
                            .into(imageUserImage)

                    imageUserNameTV.text = it.user
                }
            }
        }

        private fun setUpAndShowImageInImageView(pixabayPhoto: PixabayPhoto) {

            Glide.with(binding.view)
                    .load(pixabayPhoto.webformatURL)
                    .apply {
                        this.error(R.drawable.ic_outline_error_outline_24)
                        this.centerCrop()
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
            binding.showMoreBtn.setOnClickListener(this)
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

                        getItem(absoluteAdapterPosition)?.let { mListener!!.onAddToFavouriteBtnClicked(it) }
                    }

                    binding.showMoreBtn.id -> {

                        getItem(absoluteAdapterPosition)?.let { mListener!!.onShowMoreOptionsBtnClicked(it) }
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

                getItem(absoluteAdapterPosition)?.let { mListener!!.onAddToFavouriteLongClicked(it) }
            }

            return true
        }

        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }

    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<PixabayPhoto>() {

            override fun areItemsTheSame(oldItem: PixabayPhoto, newItem: PixabayPhoto): Boolean {

                return oldItem.largeImageURL == newItem.largeImageURL
            }

            override fun areContentsTheSame(oldItem: PixabayPhoto, newItem: PixabayPhoto): Boolean {

                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixabayViewHolder {

        val binding = PhotoItemForRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PixabayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PixabayViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onImageClicked(pixabayPhoto: PixabayPhoto)
        fun onAddToFavouriteBtnClicked(pixabayPhoto: PixabayPhoto)
        fun onShowMoreOptionsBtnClicked(pixabayPhoto: PixabayPhoto)
        fun onImageUserNameClicked(pixabayPhoto: PixabayPhoto)

        fun onAddToFavouriteLongClicked(pixabayPhoto: PixabayPhoto)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
