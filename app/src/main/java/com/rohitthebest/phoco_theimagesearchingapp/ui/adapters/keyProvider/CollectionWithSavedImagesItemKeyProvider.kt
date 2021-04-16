package com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.keyProvider

import androidx.recyclerview.selection.ItemKeyProvider
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.CollectionWithSavedImagesAdapter

class CollectionWithSavedImagesItemKeyProvider(
    private val adapter: CollectionWithSavedImagesAdapter
) : ItemKeyProvider<String>(SCOPE_CACHED) {

    override fun getKey(position: Int): String {

        return adapter.currentList[position].key
    }

    override fun getPosition(key: String): Int {

        return adapter.currentList.indexOfFirst { it.key == key }
    }

}