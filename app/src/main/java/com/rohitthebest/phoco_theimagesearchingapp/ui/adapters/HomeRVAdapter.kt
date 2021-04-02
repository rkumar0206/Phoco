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
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.databinding.AdapterHomeRecyclerviewBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class HomeRVAdapter : ListAdapter<UnsplashPhoto, HomeRVAdapter.HomeRVViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class HomeRVViewHolder(val binding: AdapterHomeRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        fun setData(unsplashPhoto: UnsplashPhoto?) {

            unsplashPhoto?.let {

                binding.apply {

                    //displaying the actual image
                    setUpAndShowImageInImageView(unsplashPhoto)

                    //displaying the user image
                    Glide.with(binding.view)
                            .load(unsplashPhoto.user.profile_image.small)
                            .centerInside()
                            .error(R.drawable.ic_outline_account_circle_24)
                            .into(imageUserImage)

                    imageUserNameTV.text = it.user.username
                }
            }
        }

        //displaying the actual image
        private fun setUpAndShowImageInImageView(unsplashPhoto: UnsplashPhoto) {

            Glide.with(binding.view)
                    .load(unsplashPhoto.urls.regular)
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
                            //TODO("Not yet implemented")
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

                        mListener!!.onImageClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.addToFavouritesBtn.id -> {

                        mListener!!.onAddToFavouriteBtnClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.showMoreBtn.id -> {

                        mListener!!.onShowMoreOptionsBtnClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.imageUserNameTV.id -> {

                        mListener!!.onImageUserNameClicked(getItem(absoluteAdapterPosition))
                    }

                    binding.reloadFAB.id -> {

                        hideReloadBtn()
                        setUpAndShowImageInImageView(getItem(absoluteAdapterPosition))
                    }
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {

            if (v?.id == binding.addToFavouritesBtn.id) {

                mListener!!.onAddToFavouriteLongClicked(getItem(absoluteAdapterPosition))
            }

            return true
        }

        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }
    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean {

                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean {

                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRVViewHolder {

        val binding = AdapterHomeRecyclerviewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return HomeRVViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeRVViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onImageClicked(unsplashPhoto: UnsplashPhoto)
        fun onAddToFavouriteBtnClicked(unsplashPhoto: UnsplashPhoto)
        fun onShowMoreOptionsBtnClicked(unsplashPhoto: UnsplashPhoto)
        fun onImageUserNameClicked(unsplashPhoto: UnsplashPhoto)

        fun onAddToFavouriteLongClicked(unsplashPhoto: UnsplashPhoto)
    }

    fun setOnClickListener(listener: OnClickListener) {

        mListener = listener
    }

}