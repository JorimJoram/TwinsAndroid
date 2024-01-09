package twins.fan.twinsandroid.viewmodel

import retrofit2.awaitResponse
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class GameViewModel {
    private val gameApi = RetrofitInstance.gameApi

    suspend fun getGameListByMonth(date:String) = gameApi.getGameListByMonth(date).awaitResponse().body()
}