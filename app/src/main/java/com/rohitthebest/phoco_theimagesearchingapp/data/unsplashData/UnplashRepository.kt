package com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rohitthebest.phoco_theimagesearchingapp.api.UnsplashAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnplashRepository @Inject constructor(
        private val unsplashApi: UnsplashAPI
) {

    fun getSearchResultsFromUnsplash(query: String) = Pager(

            config = PagingConfig(
                    pageSize = 20,
                    maxSize = 100
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
    ).flow

}