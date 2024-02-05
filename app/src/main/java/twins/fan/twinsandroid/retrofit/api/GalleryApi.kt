package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.data.gall.GalleryResponse
import twins.fan.twinsandroid.data.gall.RequestGallery

interface GalleryApi {
    @GET("/gall/api/gall-list")
    fun getAllByPage(@Query("page") page:Int): Call<GalleryResponse>
    @GET("/gall/api/gall")
    fun getById(@Query("id") id:Long): Call<Gallery>
    @DELETE("/gall/api/delete")
    fun deleteById(@Query("id") id:Long): Call<Void>
    @POST("/gall/api/create")
    fun createGallery(@Body requestGallery: RequestGallery): Call<Gallery>
}