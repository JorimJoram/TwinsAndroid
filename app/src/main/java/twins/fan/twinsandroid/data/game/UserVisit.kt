package twins.fan.twinsandroid.data.game

import twins.fan.twinsandroid.data.account.Account

data class UserVisit(
    val id:Long?=null,
    val account: Account,
    val visitDate:String
)
