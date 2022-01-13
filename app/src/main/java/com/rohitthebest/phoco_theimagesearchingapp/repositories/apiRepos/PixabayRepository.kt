package com.rohitthebest.phoco_theimagesearchingapp.repositories.apiRepos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NETWORK_PAGE_SIZE_PIXABAY
import com.rohitthebest.phoco_theimagesearchingapp.api.PixabayAPI
import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixabayRepository @Inject constructor(
        private val pixabayAPI: PixabayAPI
) {

    fun getSearchResultsFromPixabayAPI(searchQuery: String) = Pager(

            PagingConfig(
                pageSize = NETWORK_PAGE_SIZE_PIXABAY,
                maxSize = 100
            ),
            pagingSourceFactory = { PixabayPagingSource(pixabayAPI, searchQuery) }
    ).flow

    suspend fun getPhotoById(id: Int) = pixabayAPI.getPhotoById(id = id)

}