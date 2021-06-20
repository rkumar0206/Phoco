package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.phocoAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ImageRvItemProfileBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.PhocoImageItem
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class PhocoImageAdapter :
    PagingDataAdapter<PhocoImageItem, PhocoImageAdapter.PhocoImageViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class PhocoImageViewHolder(val binding: ImageRvItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun setData(phocoImage: PhocoImageItem?) {

            phocoImage?.let {

                binding.apply {

                    setUpAndShowImageInImageView(phocoImage)
                }
            }
        }

        private fun setUpAndShowImageInImageView(phocoItem: PhocoImageItem) {

            setImageToImageViewUsingGlide(
                binding.root.context,
                binding.image,
                phocoItem.image.medium,
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

            binding.reloadFAB.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (checkForNullability()) {

                when (v?.id) {

                    binding.image.id -> {

                        getItem(absoluteAdapterPosition)?.let { mListener!!.onImageClicked(it) }
                    }

                    binding.reloadFAB.id -> {

                        hideReloadBtn()
                        getItem(absoluteAdapterPosition)?.let { setUpAndShowImageInImageView(it) }
                    }
                }
            }
        }

        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }
    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<PhocoImageItem>() {
            override fun areItemsTheSame(
                oldItem: PhocoImageItem,
                newItem: PhocoImageItem
            ) = oldItem.pk == newItem.pk

            override fun areContentsTheSame(
                oldItem: PhocoImageItem,
                newItem: PhocoImageItem
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhocoImageViewHolder {

        val binding =
            ImageRvItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhocoImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhocoImageViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onImageClicked(phocoImage: PhocoImageItem)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}

