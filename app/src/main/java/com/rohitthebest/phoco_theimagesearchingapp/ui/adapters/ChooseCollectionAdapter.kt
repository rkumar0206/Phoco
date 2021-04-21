package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.AdapterChooseCollectionBinding

class ChooseCollectionAdapter(private val savedImageList: List<SavedImage>) :
        ListAdapter<Collection, ChooseCollectionAdapter.ChooseCollectionViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class ChooseCollectionViewHolder(val binding: AdapterChooseCollectionBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun setData(collection: Collection?) {

            collection?.let {

                binding.apply {

                    val savedImageInThisCollection = savedImageList.find { s ->

                        s.collectionKey == it.key
                    }

                    if (savedImageInThisCollection != null) {

                        Glide.with(binding.root)
                                .load(savedImageInThisCollection.imageUrls.medium)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(chooseCollectionIV)
                    } else {

                        chooseCollectionIV.setImageResource(0)
                    }

                    chooseCollectionNameTV.text = collection.collectionName
                }
            }
        }

        init {

            binding.root.setOnClickListener {

                if (checkForNullability()) {

                    mListener!!.onCollectionClicked(getItem(absoluteAdapterPosition))
                }
            }
        }

        private fun checkForNullability(): Boolean {

            return mListener != null && absoluteAdapterPosition != RecyclerView.NO_POSITION
        }


    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Collection>() {

        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean = oldItem.key == newItem.key

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseCollectionViewHolder {

        val binding = AdapterChooseCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChooseCollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseCollectionViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onCollectionClicked(collection: Collection)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
