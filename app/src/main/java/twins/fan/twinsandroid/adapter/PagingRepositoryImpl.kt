package twins.fan.twinsandroid.adapter

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.retrofit.api.GalleryApi


class PagingRepositoryImpl(
    private val gallApi:GalleryApi
){
    fun getResultList(): Flow<PagingData<Gallery>> {
        return Pager(PagingConfig(pageSize = 20)){
            PagingSource(gallApi)
        }.flow
    }
}