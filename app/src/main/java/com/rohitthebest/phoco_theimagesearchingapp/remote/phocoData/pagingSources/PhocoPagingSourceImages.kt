package com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NETWORK_PAGE_SIZE_PHOCO
import com.rohitthebest.phoco_theimagesearchingapp.api.PhocoAPI
import com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.PhocoImageItem
import retrofit2.HttpException
import java.io.IOException

enum class GettingImageListOptions {
    ALL_IMAGES,
    LIKED_IMAGES,
    FOLLOWING_IMAGES
}

class PhocoPagingSourceImages(
    private val phocoImageAPI: PhocoAPI,
    private val accessToken: String,
    private val user_pk: Int,
    private val imageOption: GettingImageListOptions
) : PagingSource<Int, PhocoImageItem>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhocoImageItem> {

        val position = params.key ?: 1

        return try {

            val response = when (imageOption) {

                GettingImageListOptions.ALL_IMAGES ->
                    phocoImageAPI.getUserPhocoImages(
                        accessToken = accessToken,
                        user_pk = user_pk,
                        page = position,
                        per_page = params.loadSize
                    )

                GettingImageListOptions.LIKED_IMAGES ->
                    phocoImageAPI.getUserLikedImages(
                        accessToken = accessToken,
                        user_pk = user_pk,
                        page = position,
                        per_page = params.loadSize
                    )

                GettingImageListOptions.FOLLOWING_IMAGES ->
                    phocoImageAPI.getImagesBasedOnUserFollowing(
                        accessToken = accessToken,
                        user_pk = user_pk,
                        page = position,
                        per_page = params.loadSize
                    )
            }


            val photos =
                if (response.isSuccessful) response.body()?.results else throw HttpException(
                    response
                )

            LoadResult.Page(
                data = photos!!,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + (params.loadSize / NETWORK_PAGE_SIZE_PHOCO)
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
