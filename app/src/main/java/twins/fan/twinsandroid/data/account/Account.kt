package twins.fan.twinsandroid.data.account

import java.time.LocalDateTime

data class Account(
    val id: Long? = null,
    var username: String,
    var password:String,
    var tel: String,
    val role:String? = "",
    val createdDate: String? = LocalDateTime.now().toString()
){
    companion object{
        private var instance: Account? = null
        fun getInstance(): Account {
            if (instance == null)
                instance = Account(
                    username = "",
                    password = "",
                    tel = ""
                )
            return instance!!
        }
    }
}
