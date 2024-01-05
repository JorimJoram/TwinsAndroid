package twins.fan.twinsandroid.data.account

import java.time.LocalDateTime

data class AccountImage(
    val id:Long? = null,
    val account:Account,
    val path:String,
    val createdDate:String? = LocalDateTime.now().toString()
)
