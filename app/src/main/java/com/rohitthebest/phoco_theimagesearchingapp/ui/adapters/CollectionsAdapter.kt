package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.databinding.AdapterCollectionsBinding

private const val TAG = "CollectionsAdapter"

class CollectionsAdapter(private val savedImageList: List<SavedImage>) : ListAdapter<Collection, CollectionsAdapter.CollectionViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class CollectionViewHolder(val binding: AdapterCollectionsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(collection: Collection?) {

            collection?.let {

                binding.apply {

                    val imageViewList = listOf(allSavedIV1, allSavedIV2, allSavedIV3, allSavedIV4)

                    collectionName.text = it.collectionName

                    if (savedImageList.isNotEmpty()) {

                        val savedImage = savedImageList.filter {

                            it.collectionKey == collection.key
                        }

                        try {

                            for (i in 0..3) {

                                Glide.with(binding.root)
                                        .load(savedImage[i].imageUrls.small)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(imageViewList[i])

                            }

                        } catch (e: IndexOutOfBoundsException) {

                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        init {

            Log.i(TAG, "CollectionViewHolder: ")
        }

    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<Collection>() {

            override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean = oldItem.key == newItem.key

            override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {

        Log.i(TAG, "onCreateViewHolder: ")

        val binding = AdapterCollectionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {

        Log.i(TAG, "onBindViewHolder: ")

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(collection: Collection)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
