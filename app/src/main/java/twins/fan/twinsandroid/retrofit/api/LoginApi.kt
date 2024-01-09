package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApi {
    @FormUrlEncoded
    @POST("login/process")
    fun loginProcess(@Field("username") username:String, @Field("password")password:String): Call<HashMap<String, Any>>

    @GET("/logout")
    fun logoutProcess():Call<Void>
}