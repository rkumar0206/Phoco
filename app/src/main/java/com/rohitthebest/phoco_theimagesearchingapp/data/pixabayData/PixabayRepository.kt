package com.rohitthebest.phoco_theimagesearchingapp.data.pixabayData

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rohitthebest.phoco_theimagesearchingapp.api.PixabayAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixabayRepository @Inject constructor(
        private val pixabayAPI: PixabayAPI
) {

    fun getSearchResultsFromPixabayAPI(searchQuery: String) = Pager(

            PagingConfig(
                    pageSize = 20,
                    maxSize = 100
            ),
            pagingSourceFactory = { PixabayPagingSource(pixabayAPI, searchQuery) }
    ).flow

    suspend fun getPhotoById(id: Int) = pixabayAPI.getPhotoById(id = id)

}