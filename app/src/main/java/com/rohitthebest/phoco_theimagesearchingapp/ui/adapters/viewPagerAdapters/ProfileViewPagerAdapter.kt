package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.viewPagerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ProfileViewPagerLayoutBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.PhocoImageItem
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.phocoAdapters.PhocoImageAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileViewPagerAdapter(
    private val imageList: List<PagingData<PhocoImageItem>>
) : RecyclerView.Adapter<ProfileViewPagerAdapter.ProfileViewPagerViewHolder>() {

    private var mListener: OnClickListener? = null
    var mAdapter: PhocoImageAdapter? = null

    inner class ProfileViewPagerViewHolder(val binding: ProfileViewPagerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), PhocoImageAdapter.OnClickListener {

        fun setData(pagingData: PagingData<PhocoImageItem>) {

            mAdapter = PhocoImageAdapter()

            binding.rvImagesProfile.apply {

                setHasFixedSize(true)
                adapter = mAdapter
                layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            }

            GlobalScope.launch {

                mAdapter?.submitData(pagingData)
            }

            mAdapter?.setOnClickListener(this)
        }

        override fun onImageClicked(phocoImage: PhocoImageItem) {

            if (checkForNullability()) {

                mListener!!.onImageClicked(phocoImage)
            }
        }

        private fun checkForNullability(): Boolean {

            return mListener != null && absoluteAdapterPosition != RecyclerView.NO_POSITION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewPagerViewHolder {

        val binding = ProfileViewPagerLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewPagerViewHolder, position: Int) {

        holder.setData(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    interface OnClickListener {

        fun onImageClicked(phocoImage: PhocoImageItem)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}