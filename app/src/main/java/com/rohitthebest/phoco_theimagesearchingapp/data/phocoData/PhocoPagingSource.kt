package com.rohitthebest.phoco_theimagesearchingapp.data.phocoData

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohitthebest.phoco_theimagesearchingapp.api.PhocoAPI
import retrofit2.HttpException
import java.io.IOException

class PhocoPagingSource(
    private val phocoImageAPI: PhocoAPI,
    private val accessToken: String,
    private val username: String
) : PagingSource<Int, PhocoImageItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhocoImageItem> {

        val position = params.key ?: 1

        return try {

            val response =
                phocoImageAPI.getUserPhocoImages(accessToken, username, position, params.loadSize)

            val photos =
                if (response.isSuccessful) response.body()?.results else throw HttpException(
                    response
                )

            LoadResult.Page(
                data = photos!!,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )

        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, PhocoImageItem>): Int? {

        return state.anchorPosition?.let { anchorPosition ->

            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    }
}
