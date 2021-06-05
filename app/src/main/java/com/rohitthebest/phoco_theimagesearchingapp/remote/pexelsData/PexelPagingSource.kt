package com.rohitthebest.phoco_theimagesearchingapp.remote.pexelsData

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NETWORK_PAGE_SIZE_PEXEL
import com.rohitthebest.phoco_theimagesearchingapp.api.PexelAPI
import retrofit2.HttpException
import java.io.IOException

class PexelPagingSource(
    private val searchString: String,
    private val pexelAPI: PexelAPI
) : PagingSource<Int, PexelPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PexelPhoto> {

        val position = params.key ?: 1

        return try {

            val response =
                pexelAPI.searchPhoto(searchString, page = position, per_page = params.loadSize)
            val photo = response.photos

            LoadResult.Page(
                data = photo,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photo.isEmpty()) null else position + (params.loadSize / NETWORK_PAGE_SIZE_PEXEL)
            )

        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PexelPhoto>): Int? {

        return state.anchorPosition?.let { anchorPosition ->

            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    }
}