package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.phoco_theimagesearchingapp.databinding.HeaderAndFooterLayoutForPagingAdaptersBinding

class LoadingStateAdapterForPaging(private val retry: () -> Unit) :
        LoadStateAdapter<LoadingStateAdapterForPaging.UnsplashLoadingStateViewHolder>() {

    inner class UnsplashLoadingStateViewHolder(val binding: HeaderAndFooterLayoutForPagingAdaptersBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {

            binding.buttonRetry.setOnClickListener {

                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {

            binding.apply {

                progressBar.isVisible = loadState is LoadState.Loading

                buttonRetry.isVisible = loadState !is LoadState.Loading
                textViewError.isVisible = loadState !is LoadState.Loading
            }
        }

    }

    override fun onBindViewHolder(holder: UnsplashLoadingStateViewHolder, loadState: LoadState) {

        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): UnsplashLoadingStateViewHolder {


        val binding = HeaderAndFooterLayoutForPagingAdaptersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )

        return UnsplashLoadingStateViewHolder(binding)
    }

}