package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.databinding.AdapterHomeRecyclerviewBinding

class HomeRVAdapter : ListAdapter<UnsplashPhoto, HomeRVAdapter.HomeRVViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class HomeRVViewHolder(val binding: AdapterHomeRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        fun setData(unsplashPhoto: UnsplashPhoto?) {

            unsplashPhoto?.let {

                binding.apply {

                    Glide.with(binding.view)
                            .load(unsplashPhoto.urls.regular)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .error(R.drawable.ic_outline_error_outline_24)
                            .into(image)

                    Glide.with(binding.view)
                            .load(unsplashPhoto.user.profile_image.small)
                            .centerInside()
                            .error(R.drawable.ic_outline_account_circle_24)
                            .into(imageUserImage)

                    imageUserNameTV.text = it.user.username
                }
            }
        }

        init {

            binding.image.setOnClickListener(this)
            binding.addToFavouritesBtn.setOnClickListener(this)
            binding.addToFavouritesBtn.setOnLongClickListener(this)
            binding.showMoreBtn.setOnClickListener(this)
            binding.imageUserNameTV.setOnClickListener(this)
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
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {

            if (v?.id == binding.addToFavouritesBtn.id) {

                mListener!!.onAddToFavouriteLongClicked(getItem(absoluteAdapterPosition))
            }

            return true
        }

        fun checkForNullability(): Boolean {

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

        val binding = AdapterHomeRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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