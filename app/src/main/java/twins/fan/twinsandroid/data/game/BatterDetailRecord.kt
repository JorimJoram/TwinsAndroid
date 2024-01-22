package twins.fan.twinsandroid.data.game

data class BatterDetailRecord(
    val id:Long?=null,
    val gameRecordDate:String, //경기 일자
    val name:String,
    val entry: String,
    val record:String,

    val ab:Int,
    val bb:Int,
    val hit:Int,
    val hr:Int,
    val rbi: Int,
    val avg:String,
    val obp:String
)