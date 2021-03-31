package com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rohitthebest.phoco_theimagesearchingapp.api.UnsplashAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(
        private val unsplashApi: UnsplashAPI
) {

    fun getSearchResultsFromUnsplash(query: String) = Pager(

            config = PagingConfig(
                    pageSize = 20,
                    maxSize = 100
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
    ).flow

    suspend fun getImageByID(id: String) = unsplashApi.getPhotoByID(id)

    suspend fun getRandomImages(count: Int = 30) = unsplashApi.getRandomPhotos(count).results
}