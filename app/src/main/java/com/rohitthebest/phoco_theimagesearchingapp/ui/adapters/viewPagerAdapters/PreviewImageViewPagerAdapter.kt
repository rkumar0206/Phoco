package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.viewPagerAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.databinding.PreviewImageImageviewLayoutBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.setSvgImageUrlToImageViewUsingGlide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreviewImageViewPagerAdapter(
    private val imageUrlList: List<String>,
    private val tag: String = ""
) :
    RecyclerView.Adapter<PreviewImageViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: PreviewImageImageviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(imageUrl: String) {

            setUpAndShowImageInImageView(imageUrl)
        }

        //displaying the actual image
        private fun setUpAndShowImageInImageView(imageUrl: String) {

            if (tag != Constants.SEARCH_FRAGMENT_TAG_UNDRAW) {

                setImageToImageViewUsingGlide(
                    binding.root.context,
                    binding.previewImageIV,
                    imageUrl,
                    {
                        showReloadBtn()
                    },
                    {
                        hideReloadBtn()
                    }
                )

            } else {

                CoroutineScope(Dispatchers.Main).launch {

                    setSvgImageUrlToImageViewUsingGlide(
                        binding.root.context,
                        binding.previewImageIV,
                        imageUrl
                    )
                }
            }
        }

        private fun showReloadBtn() {

            binding.reloadBackgroundPreview.show()
            binding.reloadFABPreview.visibility = View.VISIBLE
        }

        private fun hideReloadBtn() {

            binding.reloadBackgroundPreview.hide()
            binding.reloadFABPreview.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int = imageUrlList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = PreviewImageImageviewLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

        holder.setData(imageUrlList[position])
    }

}
