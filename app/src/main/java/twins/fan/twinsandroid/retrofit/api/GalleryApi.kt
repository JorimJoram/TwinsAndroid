package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.data.gall.GalleryResponse
import twins.fan.twinsandroid.data.gall.RequestGallery

interface GalleryApi {
    @GET("/gall/api/gall-list")
    fun getAllByPage(@Query("page") page:Int, @Query("page-size")pageSize:Int): Call<GalleryResponse>
    @GET("/gall/api/gall")
    fun getById(@Query("id") id:Long): Call<Gallery>
    @DELETE("/gall/api/delete")
    fun deleteById(@Query("id") id:Long): Call<Void>
    @POST("/gall/api/create")
    fun createGallery(@Body requestGallery: RequestGallery): Call<Gallery>

    @GET("/gall/api/answer/list")
    fun getAnswerByGallId(@Query("id") id: Long): Call<List<Answer>>
    @GET("/gall/api/answer/cnt")
    fun getAnswerCntByGallId(@Query("id")id: Long): Call<Int>
    @POST("/gall/api/answer/create")
    fun createAnswer(@Body answer: Answer):Call<List<Answer>>
    @DELETE("/gall/api/answer/delete")
    fun deleteAnswer(@Query("answerId")answerId:Long, @Query("gallId")gallId:Long): Call<List<Answer>>
}