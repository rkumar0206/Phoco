package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.rohitthebest.phoco_theimagesearchingapp.R

class SpinnerSearchIconAdapter(context: Context, websiteImageList: ArrayList<Int>)
    : ArrayAdapter<Int>(context, 0, websiteImageList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view: View? = convertView

        if (convertView == null) {

            view = LayoutInflater.from(context).inflate(R.layout.adapter_spinner_search_icons, parent, false)
        }

        val imageView = view?.findViewById<ImageView>(R.id.spinner_imageView)

        val currentItem = getItem(position)

        currentItem?.let {

            imageView?.setImageResource(currentItem)
        }

        return view!!
    }
}