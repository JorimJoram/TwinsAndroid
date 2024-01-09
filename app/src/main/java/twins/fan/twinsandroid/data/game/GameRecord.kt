package twins.fan.twinsandroid.data.game

data class GameRecord(
    val id:Long? = null,
    val gameDate: String,
    val versusTeam:String,
    val stadium:String,
    val lgScore: String,
    val versusScore:String,
    val startTime:String,
    val endTime:String,
    val final:String,
    val winner:String,
    val loser:String
)