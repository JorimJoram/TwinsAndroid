package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.data.account.AccountImage

interface AccountApi {
    @GET("/account/api/username-dup")
    fun checkDup(@Query("username") username:String): Call<Boolean>
    @GET("/account/api/sms-code")
    fun sendCode(@Query("tel")tel: String): Call<String>
    @POST("/account/api/create")
    fun createAccount(@Body account: Account): Call<Account>
    @GET("/account/api/image")
    fun getAccountImage(@Query("username") username:String): Call<AccountImage>

    @GET("/account/api/find/info")
    fun getAccountByTel(@Query("tel")tel: String): Call<Account>
    @GET("/account/api/find/check-account")
    fun getAccountByUsernameAndTel(@Query("username")username: String, @Query("tel")tel: String): Call<Account>
    @PATCH("/account/api/update/account")
    fun updateAccount(@Body account: Account): Call<Account>
}