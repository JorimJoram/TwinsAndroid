package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import twins.fan.twinsandroid.data.game.BatterDetailRecord
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.data.game.TotalDetailRecord
import twins.fan.twinsandroid.data.game.UserVisit

interface GameApi {
    @GET("/game/api/list")
    fun getGameListByMonth(@Query("date")date: String): Call<List<GameRecord>>
    @GET("/game/api/visit/list")
    fun getUserVisitList(@Query("username")username: String, @Query("date")yearMonth:String): Call<List<UserVisit>>
    @GET("/game/api/total")
    fun getUserTotalData(@Query("username")username:String): Call<List<TotalDetailRecord>>
    @POST("/game/api/visit/create")
    fun createUserVisit(@Body userVisit: UserVisit): Call<UserVisit>
    @DELETE("/game/api/visit/delete")
    fun deleteUserVisit(@Query("username") username:String, @Query("gameDate")gameDate:String):Call<Void>
    @GET("/game/api/result-list")
    fun getUserGameResult(@Query("username")username: String): Call<List<String>>
    @GET("/game/api/visit/last")
    fun getRecentUserVisit(@Query("username")username: String): Call<GameRecord> //TODO("밑에 일자별로 찾아내는 메서드 활용할 수 있어보임")
    @GET("/game/api/game")
    fun getGameRecordByDate(@Query("date")date: String): Call<GameRecord>
    @GET("/game/api/batter/detail")
    fun getBatterDetailByGameRecordId(@Query("date")date: String): Call<List<BatterDetailRecord>>
    @GET("/game/api/visit/single")
    fun getUserVisitByDate(@Query("username") username: String, @Query("date")date: String): Call<Boolean>
}