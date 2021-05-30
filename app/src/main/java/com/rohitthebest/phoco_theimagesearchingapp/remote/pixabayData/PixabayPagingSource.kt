package com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohitthebest.phoco_theimagesearchingapp.api.PixabayAPI
import retrofit2.HttpException
import java.io.IOException

class PixabayPagingSource(
    private val pixabayApi: PixabayAPI,
    private val searchQuery: String
) : PagingSource<Int, PixabayPhoto>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PixabayPhoto> {

        val position = params.key ?: 1

        return try {

            val response = pixabayApi.searchWithPixabay(
                searchQuery = searchQuery,
                page = position,
                per_page = params.loadSize
            )

            val photo = response.hits

            LoadResult.Page(
                photo,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photo.isEmpty()) null else position + 1
            )

        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {

            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PixabayPhoto>): Int? {

        return state.anchorPosition?.let { anchorPosition ->

            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    }
}