package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import twins.fan.twinsandroid.data.gall.GalleryResponse

interface GalleryApi {
    @GET("/gall/api/gall-list")
    fun getAllByPage(@Query("page") page:Int): Call<GalleryResponse>
}