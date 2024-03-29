package twins.fan.twinsandroid.viewmodel

import okhttp3.MultipartBody
import retrofit2.awaitResponse
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class AccountViewModel {
    private val accountApi = RetrofitInstance.accountApi

    suspend fun isUsernameDup(username:String) = accountApi.checkDup(username).awaitResponse().body()
    suspend fun sendCode(tel:String) = accountApi.sendCode(tel).awaitResponse().body()
    suspend fun createAccount(account: Account) = accountApi.createAccount(account).awaitResponse()
    suspend fun getAccountImage(username: String) = accountApi.getAccountImage(username).awaitResponse().body()

    suspend fun getAccountByTel(tel:String) = accountApi.getAccountByTel(tel).awaitResponse().body()
    suspend fun getAccountByUsername(username: String) = accountApi.getAccountByUsername(username).awaitResponse().body()
    suspend fun getAccountByUsernameAndTel(username:String, tel:String) = accountApi.getAccountByUsernameAndTel(username, tel).awaitResponse().body()
    suspend fun updateAccount(account: Account) = accountApi.updateAccount(account).awaitResponse().body()
    suspend fun updateImage(username: String, image: MultipartBody.Part) = accountApi.updateImage(username, image).awaitResponse().body()
}