package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.phocoAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ImageRvItemProfileBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.PhocoImageItem
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class PhocoImageAdapter(
    var savedImageIdList: List<String> = emptyList(),
    var shouldShowFavouriteButton: Boolean = true
) : PagingDataAdapter<PhocoImageItem, PhocoImageAdapter.PhocoImageViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    fun updateSavedImageListIds(list: List<String>) {

        this.savedImageIdList = list
    }

    inner class PhocoImageViewHolder(val binding: ImageRvItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {


        fun setData(phocoImage: PhocoImageItem?) {

            phocoImage?.let {

                binding.apply {

                    setUpAndShowImageInImageView(phocoImage)

                    //displaying the user image
                    Glide.with(binding.view)
                        .load(R.drawable.ic_outline_account_circle_24)
                        .centerInside()
                        .error(R.drawable.ic_outline_account_circle_24)
                        .into(imageUserImage)

                    imageUserNameTV.text = phocoImage.user.name

                    if (!shouldShowFavouriteButton) {

                        binding.addToFavouritesBtn.hide()
                    } else {

                        binding.addToFavouritesBtn.show()

                        if (savedImageIdList.isNotEmpty()) {

                            if (savedImageIdList.contains(phocoImage.pk.toString())) {

                                binding.addToFavouritesBtn.setImageResource(R.drawable.ic_baseline_bookmark_24)
                            } else {

                                binding.addToFavouritesBtn.setImageResource(R.drawable.ic_outline_bookmark_border_24)
                            }
                        }
                    }

                    phocoImage.is_liked?.let { isLiked ->

                        if (isLiked) {

                            binding.likeBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
                        } else {

                            binding.likeBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                        }
                    }

                    binding.likesNumTV.text = phocoImage.num_likes.toString()
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
            binding.addToFavouritesBtn.setOnClickListener(this)

            if (shouldShowFavouriteButton) {

                binding.addToFavouritesBtn.setOnLongClickListener(this)
            }
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

                    mListener!!.onAddToFavouriteLongClicked(it, absoluteAdapterPosition)
                }
            }

            return true
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
        fun onAddToFavouriteBtnClicked(phocoImage: PhocoImageItem, position: Int)
        fun onDownloadImageBtnClicked(phocoImage: PhocoImageItem, view: View)
        fun onImageUserNameClicked(phocoImage: PhocoImageItem)

        fun onAddToFavouriteLongClicked(phocoImage: PhocoImageItem, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}

