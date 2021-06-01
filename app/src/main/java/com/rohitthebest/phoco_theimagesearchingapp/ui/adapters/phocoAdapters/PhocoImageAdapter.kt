package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.phocoAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.PhocoImageItem
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide

class PhocoImageAdapter(var savedImageIdList: List<String> = emptyList()) :
    PagingDataAdapter<PhocoImageItem, PhocoImageAdapter.PhocoImageViewHolder>(DiffUtilCallback()) {


    fun updateSavedImageListIds(list: List<String>) {

        this.savedImageIdList = list
    }

    inner class PhocoImageViewHolder(val binding: PhotoItemForRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(phocoItem: PhocoImageItem?) {

            phocoItem?.let {

                binding.apply {

                    setUpAndShowImageInImageView(phocoItem)

                    val aspectRatio = phocoItem.width.toFloat() / phocoItem.height.toFloat()

                    ConstraintSet().apply {

                        clone(root)
                        setDimensionRatio(image.id, aspectRatio.toString())
                        applyTo(root)
                    }

                    //displaying the user image
                    Glide.with(binding.view)
                        .load(R.drawable.ic_outline_account_circle_24)
                        .centerInside()
                        .error(R.drawable.ic_outline_account_circle_24)
                        .into(imageUserImage)

                    imageUserNameTV.text = phocoItem.user.name

                }
            }
        }

        private fun setUpAndShowImageInImageView(phocoItem: PhocoImageItem) {

            setImageToImageViewUsingGlide(
                binding.root.context,
                binding.image,
                phocoItem.image.medium,
                {
                    //todo : showReloadBtn()
                },
                {
                    //todo : hideReloadBtn()
                }
            )

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

        return PhocoImageViewHolder(
            PhotoItemForRvBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PhocoImageViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

}