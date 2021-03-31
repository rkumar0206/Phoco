package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.view.LayoutInflater
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

    inner class HomeRVViewHolder(val binding: AdapterHomeRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(unsplashPhoto: UnsplashPhoto?) {

            unsplashPhoto?.let {

                binding.apply {

                    Glide.with(binding.view)
                            .load(unsplashPhoto.urls.regular)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .error(R.drawable.ic_outline_error_outline_24)
                            .into(image)
                }
            }
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


}