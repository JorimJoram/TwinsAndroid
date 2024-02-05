package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AnswerApi {
    @GET("/game/api/answer/cnt")
    fun getAnswerCntByDate(@Query("date")gameDate: String): Call<Int>
}