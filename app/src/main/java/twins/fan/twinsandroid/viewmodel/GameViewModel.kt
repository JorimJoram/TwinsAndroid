package twins.fan.twinsandroid.viewmodel

import retrofit2.awaitResponse
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.data.game.UserVisit
import twins.fan.twinsandroid.retrofit.RetrofitInstance

class GameViewModel {
    private val gameApi = RetrofitInstance.gameApi
    private val answerApi = RetrofitInstance.answerApi

    suspend fun getGameListByMonth(date:String) = gameApi.getGameListByMonth(date).awaitResponse().body()
    suspend fun getTotalDetailData(username:String) = gameApi.getUserTotalData(username).awaitResponse().body()
    suspend fun getUserVisit(username:String, yearMonth:String) = gameApi.getUserVisitList(username, yearMonth).awaitResponse().body()//TODO("월별로 받도록 다시 만들어야합니다.")
    suspend fun getUserGameResult(username:String) = gameApi.getUserGameResult(username).awaitResponse().body()
    suspend fun createUserVisit(userVisit: UserVisit) = gameApi.createUserVisit(userVisit).awaitResponse().body()
    suspend fun deleteUserVisit(username:String, gameDate:String) = gameApi.deleteUserVisit(username, gameDate).awaitResponse().body()
    suspend fun getRecentUserVisit(username: String) = gameApi.getRecentUserVisit(username).awaitResponse().body()
    suspend fun getGameRecordByDate(date:String) = gameApi.getGameRecordByDate(date).awaitResponse().body()!!
    suspend fun getBatterDetailByGameRecordId(date: String)= gameApi.getBatterDetailByGameRecordId(date).awaitResponse().body()!!
    suspend fun getUserVisitDate(username: String, date: String) = gameApi.getUserVisitByDate(username, date).awaitResponse().body()
    suspend fun getUserVisitCntByDate(date:String) = gameApi.getUserVisitCntByDate(date).awaitResponse().body()
    suspend fun getTeamTotalData(username: String) = gameApi.getTeamTotalData(username).awaitResponse().body()
    suspend fun getAnswerCntByDate(gameDate: String) = answerApi.getAnswerCntByDate(gameDate).awaitResponse().body()
    suspend fun getAnswerList(gameDate: String) = gameApi.getAnswerList(gameDate).awaitResponse().body()
    suspend fun deleteAnswerById(answerId:Long, gameDate:String) = answerApi.deleteAnswerById(answerId, gameDate).awaitResponse().body()
    suspend fun createAnswer(answer: Answer) = answerApi.createAnswer(answer).awaitResponse().body()
    suspend fun getMyGameRecord(username: String) = gameApi.getMyGameRecord(username).awaitResponse().body()
}