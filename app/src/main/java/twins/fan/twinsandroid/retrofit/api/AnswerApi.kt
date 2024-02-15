package twins.fan.twinsandroid.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import twins.fan.twinsandroid.data.answer.Answer

interface AnswerApi {
    @GET("/game/api/answer/cnt")
    fun getAnswerCntByDate(@Query("date")gameDate: String): Call<Int>
    @POST("/game/api/answer/create")
    fun createAnswer(@Body answer:Answer):Call<List<Answer>>
    @DELETE("/game/api/answer/del")
    fun deleteAnswerById(@Query("id")answerId:Long, @Query("date")gameDate:String): Call<List<Answer>>
}