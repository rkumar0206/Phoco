package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
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
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
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

                //displaying the user image
                Glide.with(binding.view)
                    .load(it.userInfo.userImageUrl)
                    .centerInside()
                    .error(R.drawable.ic_outline_account_circle_24)
                    .into(imageUserImage)

                imageUserNameTV.text = it.userInfo.userName

                //todo : do something for bookmark button
            }

        }

        private fun setUpAndShowImageInImageView(imageUrl: String) {

            Glide.with(binding.view)
                .load(imageUrl)
                .apply {
                    this.error(R.drawable.ic_outline_error_outline_24)
                    this.centerCrop()
                }
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {

                        showReloadBtn()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
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

        fun getItemsDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {

                override fun getPosition(): Int = absoluteAdapterPosition

                override fun getSelectionKey(): String = getItem(absoluteAdapterPosition).key
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

        fun onItemClick(model: SavedImage)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
