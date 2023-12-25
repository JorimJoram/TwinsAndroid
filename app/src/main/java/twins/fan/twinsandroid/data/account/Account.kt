package twins.fan.twinsandroid.data.account

data class Account(
    val id: Long? = null,
    val username: String,
    val password:String,
    val tel: String,
    val role:String,
    val createdDate: String
)
