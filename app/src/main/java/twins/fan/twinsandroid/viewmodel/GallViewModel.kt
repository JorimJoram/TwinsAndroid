package twins.fan.twinsandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import retrofit2.awaitResponse
import twins.fan.twinsandroid.adapter.PagingRepositoryImpl
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.data.gall.RequestGallery
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class GallViewModel: ViewModel() {
    private val gallApi = RetrofitInstance.gallApi
    fun setPaging(): Flow<PagingData<Gallery>> = PagingRepositoryImpl(gallApi).getResultList().cachedIn(viewModelScope)
    suspend fun getFewGallery(page:Int, pageSize:Int) = gallApi.getAllByPage(page, pageSize).awaitResponse().body()
    suspend fun getById(id:Long) = gallApi.getById(id).awaitResponse().body()
    suspend fun deleteById(id:Long) = gallApi.deleteById(id).awaitResponse().body()
    suspend fun createGallery(requestGallery: RequestGallery) = gallApi.createGallery(requestGallery).awaitResponse().body()
    //내가 작성한 글의 수
    suspend fun getMyGalleryAmount(username:String) = gallApi.getMyGalleryAmount(username).awaitResponse().body()
    //내가 작성한 글의 리스트
    suspend fun getMyGalleryList(username:String) = gallApi.getMyGalleryList(username).awaitResponse().body()

    suspend fun getAnswerByGallId(id:Long) = gallApi.getAnswerByGallId(id).awaitResponse().body()
    suspend fun getAnswerCntByGallId(id: Long) = gallApi.getAnswerCntByGallId(id).awaitResponse().body()
    suspend fun createAnswer(answer: Answer) = gallApi.createAnswer(answer).awaitResponse().body()
    suspend fun deleteAnswer(answerId:Long, gallId:Long) = gallApi.deleteAnswer(answerId, gallId).awaitResponse().body()
    suspend fun getMyAnswerAmount(username:String) = gallApi.getMyAnswerAmount(username).awaitResponse().body()
    suspend fun getMyAnswerList(username: String) = gallApi.getMyAnswerList(username).awaitResponse().body()
}