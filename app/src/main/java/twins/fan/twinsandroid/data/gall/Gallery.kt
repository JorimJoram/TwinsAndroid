package twins.fan.twinsandroid.data.gall

import androidx.recyclerview.widget.DiffUtil
import twins.fan.twinsandroid.data.account.Account
import java.time.LocalDateTime

data class Gallery(
    val id:Long,
    val title: String,
    val content:String,
    val account:Account,
    val createdDate:String,
    val modifiedDate:String? = null
)