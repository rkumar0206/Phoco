package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PhotoItemForRvBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.undrawData.Illo
import com.rohitthebest.phoco_theimagesearchingapp.utils.invisible
import com.rohitthebest.phoco_theimagesearchingapp.utils.setSvgImageUrlToImageViewUsingGlide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "UnDrawImageAdapter"

class UnDrawImageAdapter :
    ListAdapter<Illo, UnDrawImageAdapter.UnDrwaViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class UnDrwaViewHolder(val binding: PhotoItemForRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.image.setOnClickListener {

                if (checkForNullability()) {

                    mListener!!.onImageClicked(absoluteAdapterPosition)
                }
            }

            binding.downloadImageBtn.setOnClickListener {

                if (checkForNullability()) {

                    mListener!!.onDownloadBtnClicked(getItem(absoluteAdapterPosition))
                }
            }

            binding.imageUserNameTV.setOnClickListener {

                if (checkForNullability()) {

                    mListener!!.onImageTitleClicked(getItem(absoluteAdapterPosition))
                }
            }
        }

        fun setData(unDraw: Illo?) {

            unDraw?.let {

                binding.apply {

                    addToFavouritesBtn.invisible()

                    //displaying the actual image
                    setUpAndShowImageInImageView(it)
                    imageUserImage.setImageResource(R.drawable.undraw_icon)
                    imageUserNameTV.text = it.title

                    val aspectRatio = 300f / 300f

                    ConstraintSet().apply {

                        clone(root)
                        setDimensionRatio(image.id, aspectRatio.toString())
                        applyTo(root)
                    }

                }
            }
        }

        private fun setUpAndShowImageInImageView(unDraw: Illo) {

            Log.d(TAG, "setUpAndShowImageInImageView: ${unDraw.image}")

            CoroutineScope(Dispatchers.Main).launch {

                setSvgImageUrlToImageViewUsingGlide(
                    binding.root.context,
                    binding.image,
                    unDraw.image
                )

            }

        }


        private fun checkForNullability(): Boolean {

            return absoluteAdapterPosition != RecyclerView.NO_POSITION && mListener != null
        }
    }

    companion object {

        class DiffUtilCallback : DiffUtil.ItemCallback<Illo>() {

            override fun areItemsTheSame(
                oldItem: Illo,
                newItem: Illo
            ): Boolean = oldItem.image == newItem.image

            override fun areContentsTheSame(
                oldItem: Illo,
                newItem: Illo
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnDrwaViewHolder {

        val binding =
            PhotoItemForRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UnDrwaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnDrwaViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onImageClicked(selectedPosition: Int)
        fun onDownloadBtnClicked(unDraw: Illo)
        fun onImageTitleClicked(unDraw: Illo)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}

