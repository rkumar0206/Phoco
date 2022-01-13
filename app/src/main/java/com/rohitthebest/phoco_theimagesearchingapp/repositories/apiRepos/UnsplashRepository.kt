package com.rohitthebest.phoco_theimagesearchingapp.repositories.apiRepos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NETWORK_PAGE_SIZE_UNSPLASH
import com.rohitthebest.phoco_theimagesearchingapp.api.UnsplashAPI
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(
        private val unsplashApi: UnsplashAPI
) {

    fun getSearchResultsFromUnsplash(query: String) = Pager(

            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE_UNSPLASH,
                maxSize = 100
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
    ).flow

    suspend fun getImageByID(id: String) = unsplashApi.getPhotoByID(id)

    suspend fun getRandomImages(count: Int = 30) = unsplashApi.getRandomPhotos(count)
}