package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.data.game.BatterDetailRecord
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.data.game.TotalDetailRecord
import twins.fan.twinsandroid.data.game.UserVisit

interface GameApi {
    @GET("/game/api/list")
    fun getGameListByMonth(@Query("date")date: String): Call<List<GameRecord>>
    @GET("/game/api/game")
    fun getGameRecordByDate(@Query("date")date: String): Call<GameRecord>
    @GET("/game/api/visit/list")
    fun getUserVisitList(@Query("username")username: String, @Query("date")yearMonth:String): Call<List<UserVisit>>
    @POST("/game/api/visit/create")
    fun createUserVisit(@Body userVisit: UserVisit): Call<UserVisit>
    @GET("/game/api/visit/recent") //확인
    fun getRecentUserVisit(@Query("username")username: String): Call<GameRecord>
    @GET("/game/api/visit/single")
    fun getUserVisitByDate(@Query("username") username: String, @Query("date")date: String): Call<Boolean>
    @GET("/game/api/visit/cnt")
    fun getUserVisitCntByDate(@Query("date")date: String): Call<Int>
    @DELETE("/game/api/visit/delete")
    fun deleteUserVisit(@Query("username") username:String, @Query("gameDate")gameDate:String):Call<Void>
    @GET("/game/api/batter/total") //해결
    fun getUserTotalData(@Query("username")username:String): Call<List<TotalDetailRecord>>
    @GET("/game/api/batter/detail")
    fun getBatterDetailByGameRecordId(@Query("date")date: String): Call<List<BatterDetailRecord>>
    @GET("/game/api/team/batter/total") //확인
    fun getTeamTotalData(@Query("username")username: String): Call<TotalDetailRecord>
    @GET("/game/api/answer/list")
    fun getAnswerList(@Query("date") gameDate: String): Call<List<Answer>>
    @GET("/game/api/result-list")
    fun getUserGameResult(@Query("username")username: String): Call<List<String>>
}