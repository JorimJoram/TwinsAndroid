package twins.fan.twinsandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import retrofit2.awaitResponse
import twins.fan.twinsandroid.adapter.PagingRepositoryImpl
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.data.gall.RequestGallery
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class GallViewModel: ViewModel() {
    private val gallApi = RetrofitInstance.gallApi

    fun setPaging(): Flow<PagingData<Gallery>> {
        return PagingRepositoryImpl(gallApi).getResultList().cachedIn(viewModelScope)
    }

    suspend fun getById(id:Long) = gallApi.getById(id).awaitResponse().body()

    suspend fun deleteById(id:Long) = gallApi.deleteById(id).awaitResponse().body()
    suspend fun createGallery(requestGallery: RequestGallery) = gallApi.createGallery(requestGallery).awaitResponse().body()
}