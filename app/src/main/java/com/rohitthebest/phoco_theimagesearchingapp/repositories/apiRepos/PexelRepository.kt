package com.rohitthebest.phoco_theimagesearchingapp.repositories.apiRepos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NETWORK_PAGE_SIZE_PEXEL
import com.rohitthebest.phoco_theimagesearchingapp.api.PexelAPI
import com.rohitthebest.phoco_theimagesearchingapp.remote.pexelsData.PexelPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelRepository @Inject constructor(
    private val pexelAPI: PexelAPI
) {

    fun getSearchResultFromThePexelApi(searchString: String) = Pager(
        PagingConfig(
            pageSize = NETWORK_PAGE_SIZE_PEXEL,
            maxSize = 100
        ),
        pagingSourceFactory = { PexelPagingSource(searchString, pexelAPI) }
    ).flow

}