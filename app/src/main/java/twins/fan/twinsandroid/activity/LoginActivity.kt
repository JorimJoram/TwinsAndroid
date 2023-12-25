package twins.fan.twinsandroid.activity

import android.animation.Animator
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation.AnimationListener
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.ResponseBody
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.ActivityLoginBinding
import twins.fan.twinsandroid.viewmodel.LoginViewModel
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.util.HashMap

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private val loginViewModel = LoginViewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
    }

    override fun onStart() {
        super.onStart()
        loginBinding.loginLoginButton.setOnClickListener(loginListener)
    }

    private val loginListener = OnClickListener{
        val loadingAnimation = loginBinding.loginLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch{
            val username = loginBinding.loginUsername.text.toString()
            Log.i(TAG, "username:$username")
            try {
                val result = loginViewModel.loginProcess(
                    username,
                    loginBinding.loginPassword.text.toString()
                )
                if(result.isSuccessful) {
                    checkResult(result.headers(), result.body()!!)
                }else{
                    showFailCode()
                }
            }catch (e: SocketTimeoutException){
                showFailCode()
            }catch (e: ProtocolException){
                showFailCode()
            }finally {
                loadingAnimation.cancelAnimation()
                loadingAnimation.visibility = View.GONE
            }
        }
    }

    private fun showFailCode() {
        Toast.makeText(this.applicationContext, "Server connection failed", Toast.LENGTH_LONG).show()
    }

    private fun checkResult(headers: Headers, body: HashMap<String, Any>) {
        if(body.containsKey("exception")){
            val exceptionCode:String = when(body["exception"]){
                "BandInputException" -> "아이디와 비밀번호를 확인해주세요"
                else -> "계정을 다시 확인해주세요"
            }
            Toast.makeText(this.applicationContext, exceptionCode, Toast.LENGTH_LONG).show()
        }else{
            val cookieHeader = headers["Set-Cookie"]?.split(";")?.get(0)
            val authInstance = AuthenticationInfo.getInstance()
            authInstance.username = body["username"].toString()
            authInstance.authorities = body["authorities"] as List<String>?
            authInstance.jSessionId = cookieHeader?.split("=")?.get(1)

            Toast.makeText(this.applicationContext, "LoginSuccess\n$authInstance", Toast.LENGTH_LONG).show()
        }
    }
}