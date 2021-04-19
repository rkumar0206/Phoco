package com.rohitthebest.phoco_theimagesearchingapp.data.pexelsData

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rohitthebest.phoco_theimagesearchingapp.api.PexelAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelRepository @Inject constructor(
    private val pexelAPI: PexelAPI
) {

    fun getSearchResultFromThePexelApi(searchString: String) = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 100
        ),
        pagingSourceFactory = { PexelPagingSource(searchString, pexelAPI) }
    ).flow

}