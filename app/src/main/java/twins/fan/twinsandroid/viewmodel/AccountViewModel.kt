package twins.fan.twinsandroid.viewmodel

import retrofit2.awaitResponse
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class AccountViewModel {
    private val accountApi = RetrofitInstance.accountApi
    suspend fun isUsernameDup(username:String) = accountApi.checkDup(username).awaitResponse().body()
    suspend fun sendCode(tel:String) = accountApi.sendCode(tel).awaitResponse().body()
    suspend fun createAccount(account: Account) = accountApi.createAccount(account).awaitResponse()
    suspend fun getAccountImage(username: String) = accountApi.getAccountImage(username).awaitResponse().body()
}