package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.utils.setImageToImageViewUsingGlide
import java.util.*

class PreviewImageViewPagerAdapter(val context: Context, private val imageList: List<String>) :
    PagerAdapter() {

    private var mLayoutInflator: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = imageList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean =
        view == `object` as LinearLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        // inflating the layout

        val itemView =
            mLayoutInflator.inflate(R.layout.preview_image_imageview_layout, container, false)

        val photoView = itemView.findViewById<PhotoView>(R.id.previewImageIV)

        setImageToImageViewUsingGlide(
            context,
            photoView,
            imageList[position],
            {},
            {}
        )

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as LinearLayout)
    }
}