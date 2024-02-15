package twins.fan.twinsandroid.data.answer

import java.time.LocalDateTime

data class Answer(
    val id:Long? = null,
    val accountUsername: String,
    val accountImage:String,
    val gameRecordGameDate:String?="",
    val galleryId:Long? = -1L,
    val content:String,
    val createdDate:String? = LocalDateTime.now().toString()
)
