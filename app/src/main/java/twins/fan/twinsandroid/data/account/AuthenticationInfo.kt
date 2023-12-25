package twins.fan.twinsandroid.data.account

data class AuthenticationInfo(
    var username: String? = null,
    var authorities: List<String>? = null,
    var jSessionId: String? = null
){
    companion object{
        private var instance: AuthenticationInfo? = null
        fun getInstance(): AuthenticationInfo {
            if(instance == null)
                instance = AuthenticationInfo()
            return instance!!
        }
    }
}