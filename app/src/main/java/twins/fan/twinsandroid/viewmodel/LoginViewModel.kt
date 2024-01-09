package twins.fan.twinsandroid.viewmodel

import retrofit2.awaitResponse
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class LoginViewModel {
    private val loginApi = RetrofitInstance.loginApi

    suspend fun loginProcess(username:String, password:String) = loginApi.loginProcess(username, password).awaitResponse()
    suspend fun logoutProcess() = loginApi.logoutProcess().awaitResponse().body()
}