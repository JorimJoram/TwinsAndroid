package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import twins.fan.twinsandroid.data.account.Account

interface AccountApi {
    @GET("/account/api/username-dup")
    fun checkDup(@Query("username") username:String): Call<Boolean>
    @GET("/account/api/sms-code")
    fun sendCode(@Query("tel")tel: String): Call<String>
    @POST("/account/api/create")
    fun createAccount(@Body account: Account): Call<Account>
}