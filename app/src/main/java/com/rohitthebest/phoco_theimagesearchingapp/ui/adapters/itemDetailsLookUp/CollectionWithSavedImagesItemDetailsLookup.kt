package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.itemDetailsLookUp

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionWithSavedImagesAdapter

class CollectionWithSavedImagesItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<String>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {

        val view = recyclerView.findChildViewUnder(e.x, e.y)

        if (view != null) {

            return (recyclerView.getChildViewHolder(view)
                    as CollectionWithSavedImagesAdapter.CollectionWithSavedImageViewHolder).getItemsDetails()
        }

        return null
    }

}