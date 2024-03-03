package twins.fan.twinsandroid.data.gall

import twins.fan.twinsandroid.data.account.Account

data class Gallery(
    val id:Long,
    val title: String,
    val content:String,
    val account:Account,
    val createdDate:String,
    val modifiedDate:String? = null
)