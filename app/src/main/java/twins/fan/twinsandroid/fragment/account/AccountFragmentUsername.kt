package twins.fan.twinsandroid.fragment.account

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.databinding.FragmentAccountUsernameBinding
import twins.fan.twinsandroid.viewmodel.AccountViewModel
import java.net.SocketTimeoutException

class AccountFragmentUsername : Fragment() {
    private lateinit var binding: FragmentAccountUsernameBinding
    private val accountViewModel = AccountViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_username, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usernameCheckButton.setOnClickListener(checkUsername)
        binding.usernameBackButton.setOnClickListener(backListener)
    }

    private val backListener = OnClickListener {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("회원가입 취소").setMessage("다시 로그인화면으로 돌아가시겠습니까?")
        builder.setPositiveButton("예"){
                dialog, which -> activity?.finish()
        }
        builder.setNegativeButton("아니오"){
                dialog, which -> dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private val checkUsername = OnClickListener {
        val username = binding.usernameUsernameInput.text.toString()
        //TODO("정규식으로 띄어쓰기 쓰지 않고 4자 이상의 영문 + 숫자 인지 확인")
        val regex = Regex("^[a-zA-Z0-9]{4,}\$")
        if(!regex.matches(username)){
            showFailure(-1)
        }else{
            checkDup(username)
        }
    }

    private fun showFailure(code: Int) {
        val msg:String = when(code){
            -1 -> "아이디를 다시 입력해주세요"
            -2 -> "이미 사용 중인 아이디입니다."
            -3 -> "인터넷 연결 확인 후 다시 시도해주세요."
            else -> "다시 시도해주세요"
        }
        binding.usernameMsg.text = msg
    }

    private fun checkDup(username:String){
        val loadingAnimation = binding.usernameLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        var isDup = false //false == 중복
        lifecycleScope.launch {
            try{
                isDup = accountViewModel.isUsernameDup(username)!!
                if(!isDup)
                    showFailure(-2)
                else
                    nextFragment(username) //성공했다면 다음 페이지로
            }catch (e:SocketTimeoutException){
                showFailure(-3)
            }finally {
                loadingAnimation.cancelAnimation()
                loadingAnimation.visibility = View.GONE
            }
        }
    }

    private fun nextFragment(username:String){
        val accountInstance = Account.getInstance()
        accountInstance.username = username

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val passwordFragment = AccountFragmentPassword()
        transaction!!.replace(R.id.account_frameLayout, passwordFragment)
        transaction.commit()
    }
}