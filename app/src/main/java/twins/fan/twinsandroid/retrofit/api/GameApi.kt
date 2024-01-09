package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import twins.fan.twinsandroid.data.game.GameRecord

interface GameApi {
    @GET("/game/api/list")
    fun getGameListByMonth(@Query("date")date: String): Call<List<GameRecord>>
}