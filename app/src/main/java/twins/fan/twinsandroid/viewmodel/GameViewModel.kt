package twins.fan.twinsandroid.viewmodel

import retrofit2.awaitResponse
import twins.fan.twinsandroid.data.game.UserVisit
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class GameViewModel {
    private val gameApi = RetrofitInstance.gameApi

    suspend fun getGameListByMonth(date:String) = gameApi.getGameListByMonth(date).awaitResponse().body()
    suspend fun getTotalDetailData(username:String) = gameApi.getUserTotalData(username).awaitResponse().body()
    suspend fun getUserVisit(username:String) = gameApi.getUserVisitList(username).awaitResponse().body()//TODO("월별로 받도록 다시 만들어야합니다.")
    suspend fun createUserVisit(userVisit: UserVisit) = gameApi.createUserVisit(userVisit).awaitResponse().body()
    suspend fun deleteUserVisit(username:String, gameDate:String) = gameApi.deleteUserVisit(username, gameDate).awaitResponse().body()
}