package twins.fan.twinsandroid.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.awaitResponse
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.retrofit.api.GalleryApi

class PagingSource(
    private val galleryApi: GalleryApi
): PagingSource<Int, Gallery>(){
    override fun getRefreshKey(state: PagingState<Int, Gallery>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gallery> {
        return try{
            val pageIndex = params.key ?: 0
            val response = galleryApi.getAllByPage(pageIndex).awaitResponse().body()
            val data: List<Gallery> = response?.content ?: listOf()

            val nextKey = if (data.isEmpty()) { null } else { pageIndex + 1 }
            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = nextKey
            )
        }catch (e:Exception){
            LoadResult.Error(e)
        }
    }

}