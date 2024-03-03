package twins.fan.twinsandroid.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.Headers
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.AccountImage
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.ActivityLoginBinding
import twins.fan.twinsandroid.exception.BlankInputException
import twins.fan.twinsandroid.viewmodel.AccountViewModel
import twins.fan.twinsandroid.viewmodel.LoginViewModel
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.util.HashMap

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private val loginViewModel = LoginViewModel()
    private val accountViewModel = AccountViewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
    }

    override fun onStart() {
        super.onStart()
        loginBinding.loginLoginButton.setOnClickListener(loginListener)
        loginBinding.loginAccountButton.setOnClickListener {
            val its = Intent(this.applicationContext, AccountActivity::class.java)
            startActivity(its)
        }
        loginBinding.loginFindAccount.setOnClickListener{
            val its = Intent(this.applicationContext, FindActivity::class.java)
            startActivity(its)
        }
        Glide.with(this).load(R.raw.lg_twins).into(loginBinding.loginMainLogo)
    }

    private val loginListener = OnClickListener{
        val username = loginBinding.loginUsername.text.toString()
        val password = loginBinding.loginPassword.text.toString()
        if(username.isBlank() || password.isBlank()) showFailCode(BlankInputException("아이디나 비번 안 쓴 경우"))else sendLogin()
    }
    private fun sendLogin(){
        val loadingAnimation = loginBinding.loginLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch{
            try {
                val result = loginViewModel.loginProcess(loginBinding.loginUsername.text.toString(), loginBinding.loginPassword.text.toString())
                if(result.isSuccessful) { checkResult(result.headers(), result.body()!!) }
                else{ showFailCode(java.lang.Exception("isNotSuccessful")) }
            } catch (e: SocketTimeoutException){
                showFailCode(e)
            } catch (e: ProtocolException){
                showFailCode(e)
            } catch (e: ConnectException){
                showFailCode(e)
            } finally {
                loadingAnimation.cancelAnimation()
                loadingAnimation.visibility = View.GONE
            }
        }
    }
    private fun showFailCode(e:Exception) {
        when(e){
            is SocketTimeoutException, is ConnectException -> loginBinding.usernameMsg.text = "서버가 아직 열리지 않았습니다.\n잠시후에 다시 이용해주세요."
            is ProtocolException -> loginBinding.usernameMsg.text = "인터넷 연결을 다시 확인해주세요"
            is BlankInputException -> loginBinding.usernameMsg.text = "아이디와 비밀번호를 모두 입력해주세요"
            else -> loginBinding.usernameMsg.text = "다시 시도해주세요"
        }
    }
    private fun checkResult(headers: Headers, body: HashMap<String, Any>) {
        if(body.containsKey("exception")){
            val exceptionCode:String = when(body["exception"]){
                "BadInputException" -> "아이디와 비밀번호를 확인해주세요"
                else -> "계정을 다시 확인해주세요"
            }
            loginBinding.usernameMsg.text = exceptionCode
        }else{
            val cookieHeader = headers["Set-Cookie"]?.split(";")?.get(0)
            val authInstance = AuthenticationInfo.getInstance()
            Log.d(TAG, "authentication: $body")

            authInstance.username = body["username"].toString()
            authInstance.authorities = body["authorities"] as List<String>?
            authInstance.jSessionId = cookieHeader?.split("=")?.get(1)

            setAccountImage()

            val it = Intent(this, MainActivity::class.java)
            startActivity(it)
            finish()
        }
    }

    private fun setAccountImage(){
        lifecycleScope.launch {
            val accountImage = accountViewModel.getAccountImage(AuthenticationInfo.getInstance().username!!)!!
            AccountImage.getInstance().path = accountImage.path
        }
    }
}