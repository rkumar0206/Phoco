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
import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class PixabaySearchResultsAdapter(var savedImageIdList: List<String> = emptyList()) :
        PagingDataAdapter<PixabayPhoto, PixabaySearchResultsAdapter.PixabayViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    fun updateSavedImageListIds(list: List<String>) {

        this.savedImageIdList = list
    }

    inner class PixabayViewHolder(val binding: PhotoItemForRvBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        fun setData(pixabayPhoto: PixabayPhoto?) {

            pixabayPhoto?.let {

                binding.apply {

                    //displaying the actual image
                    setUpAndShowImageInImageView(it)

                    val aspectRatio =
                        pixabayPhoto.imageWidth.toFloat() / pixabayPhoto.imageHeight.toFloat()

                    ConstraintSet().apply {

                        clone(root)
                        setDimensionRatio(image.id, aspectRatio.toString())
                        applyTo(root)
                    }

                    //displaying the user image
                    Glide.with(binding.view)
                        .load(pixabayPhoto.userImageURL)
                        .centerInside()
                        .error(R.drawable.ic_outline_account_circle_24)
                        .into(imageUserImage)

                    imageUserNameTV.text = it.user

                    if (savedImageIdList.isNotEmpty()) {

                        if (savedImageIdList.contains(pixabayPhoto.id.toString())) {

                            binding.addToFavouritesBtn.setImageResource(R.drawable.ic_baseline_bookmark_24)
                        } else {

                            binding.addToFavouritesBtn.setImageResource(R.drawable.ic_outline_bookmark_border_24)
                        }
                    }

                }
            }
        }

        private fun setUpAndShowImageInImageView(pixabayPhoto: PixabayPhoto) {

            setImageToImageViewUsingGlide(
                binding.root.context,
                binding.image,
                pixabayPhoto.webformatURL,
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

                        getItem(absoluteAdapterPosition)?.let { mListener!!.ondownloadImageBtnClicked(it, binding.downloadImageBtn) }
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
        fun onAddToFavouriteBtnClicked(pixabayPhoto: PixabayPhoto, position: Int)
        fun ondownloadImageBtnClicked(pixabayPhoto: PixabayPhoto, view: View)
        fun onImageUserNameClicked(pixabayPhoto: PixabayPhoto)

        fun onAddToFavouriteLongClicked(pixabayPhoto: PixabayPhoto, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
