package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show

class CollectionWithSavedImagesAdapter :
    ListAdapter<SavedImage, CollectionWithSavedImagesAdapter.CollectionWithSavedImageViewHolder>(
        DiffUtilCallback()
    ) {

    private var mListener: OnClickListener? = null
    var tracker: SelectionTracker<String>? = null

    inner class CollectionWithSavedImageViewHolder(val binding: PhotoItemForRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(savedImage: SavedImage?, isSelected: Boolean) = with(binding) {

            savedImage?.let {

                setUpAndShowImageInImageView(it.imageUrls.medium)

                val aspectRatio = savedImage.width.toFloat() / savedImage.height.toFloat()

                ConstraintSet().apply {

                    clone(root)
                    setDimensionRatio(image.id, aspectRatio.toString())
                    applyTo(root)
                }

                //displaying the user image
                Glide.with(binding.view)
                    .load(it.userInfo.userImageUrl)
                    .centerInside()
                    .error(R.drawable.ic_outline_account_circle_24)
                    .into(imageUserImage)

                imageUserNameTV.text = it.userInfo.userName

                addToFavouritesBtn.hide()

                updateViewForIsSelectedValue(isSelected)
            }

        }

        private fun updateViewForIsSelectedValue(isSelected: Boolean) {

            binding.reloadBackground.isVisible = isSelected
            binding.selectedIV.isVisible = isSelected
        }

        private fun setUpAndShowImageInImageView(imageUrl: String) {

            setImageToImageViewUsingGlide(
                binding.root.context,
                binding.image,
                imageUrl,
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

        fun getItemsDetails(): ItemDetailsLookup.ItemDetails<String> =
                object : ItemDetailsLookup.ItemDetails<String>() {

                    override fun getPosition(): Int = absoluteAdapterPosition

                    override fun getSelectionKey(): String = getItem(absoluteAdapterPosition).key
                }

        init {

            binding.root.setOnClickListener {

                if (checkForNullability()) {

                    mListener!!.onItemClick(getItem(absoluteAdapterPosition))
                }
            }

            binding.downloadImageBtn.setOnClickListener {

                if (checkForNullability()) {

                    mListener!!.onDownloadImageBtnClicked(
                        getItem(absoluteAdapterPosition),
                        binding.downloadImageBtn
                    )
                }
            }

            binding.imageUserNameTV.setOnClickListener {

                if (checkForNullability()) {

                    mListener!!.onUserTextViewClicked(getItem(absoluteAdapterPosition))
                }

            }
        }

        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }

    }

    override fun getItemId(position: Int): Long = position.toLong()


    class DiffUtilCallback : DiffUtil.ItemCallback<SavedImage>() {

        override fun areItemsTheSame(oldItem: SavedImage, newItem: SavedImage): Boolean =
            oldItem.key == newItem.key

        override fun areContentsTheSame(oldItem: SavedImage, newItem: SavedImage): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectionWithSavedImageViewHolder {

        val binding =
            PhotoItemForRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionWithSavedImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionWithSavedImageViewHolder, position: Int) {

        tracker?.let {

            holder.setData(getItem(position), it.isSelected(getItem(position).key))
        }

    }

    interface OnClickListener {

        fun onItemClick(savedImage: SavedImage)
        fun onUserTextViewClicked(savedImage: SavedImage)
        fun onDownloadImageBtnClicked(savedImage: SavedImage, view: View)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
