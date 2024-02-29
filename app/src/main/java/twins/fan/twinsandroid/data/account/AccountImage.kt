package twins.fan.twinsandroid.data.account

import java.time.LocalDateTime

data class AccountImage(
    val id:Long? = null,
    val account:Account,
    var path:String,
    val createdDate:String? = LocalDateTime.now().toString()
){
    companion object{
        private var instance:AccountImage? = null
        fun getInstance(): AccountImage {
            if(instance == null)
                instance = AccountImage(
                    account = Account(username="", password="", tel=""),
                    path = ""
                )
            return instance!!
        }
    }

}
