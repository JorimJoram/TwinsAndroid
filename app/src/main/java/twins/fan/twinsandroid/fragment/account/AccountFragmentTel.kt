package twins.fan.twinsandroid.fragment.account

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.databinding.FragmentAccountTelBinding
import twins.fan.twinsandroid.viewmodel.AccountViewModel
import java.net.SocketTimeoutException

class AccountFragmentTel : Fragment() {
    private lateinit var binding: FragmentAccountTelBinding
    private val accountViewModel = AccountViewModel()
    private var code = "-1"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_tel, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sendCodeButton = binding.telSendCodeButton
        val checkCodeButton = binding.telTelCodeCheck

        sendCodeButton.isEnabled = true
        checkCodeButton.isEnabled = false

        sendCodeButton.setOnClickListener(sendCode)
        checkCodeButton.setOnClickListener(checkCode)

    }

    private val sendCode = OnClickListener{
        val loadingAnimation = binding.telLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch {
            try{
                code = accountViewModel.sendCode(binding.telTelInput.text.toString())!!
                if(code == "-1")
                    showFailure(-1)
                else{
                    binding.telSendCodeButton.isEnabled = false //TODO("일정 시간 후에 다시 실행할 수 있도록 해야합니다.")
                    binding.telTelCodeCheck.isEnabled = true
                }
            }catch (e:SocketTimeoutException){
                showFailure(-3)
            }finally {
                loadingAnimation.cancelAnimation()
                loadingAnimation.visibility = View.GONE
            }
        }
    }

    private val checkCode = OnClickListener {
        if(binding.telTelCode.text.toString() != code)
            showFailure(-2)
        else{
            val accountInstance = Account.getInstance()
            accountInstance.tel = binding.telTelInput.text.toString()

            val loadingAnimation = binding.telLottieView
            loadingAnimation.visibility = View.VISIBLE
            loadingAnimation.playAnimation()

            lifecycleScope.launch{
                try{
                    val result = accountViewModel.createAccount(accountInstance)
                    if(result.isSuccessful){
                        showFailure(1) // 성공한건데, 일단 임시로 이렇게 두겠습니다.
                        activity?.finish()

                    }else{
                        showFailure(-5) //다시 시도 해달라고 하는게 맞는듯
                    }
                }catch (e:SocketTimeoutException){
                    showFailure(-3)
                }catch (e:Exception){
                    Log.e(TAG, "error: $e")
                }finally {
                    loadingAnimation.cancelAnimation()
                    loadingAnimation.visibility = View.GONE
                }
            }
        }

    }

    private fun showFailure(code:Int){
        val msg:String = when(code){
            1 -> "회원가입이 완료되었습니다."
            -1 -> "전화번호를 다시 확인해주세요"
            -2 -> "인증번호를 다시 확인해주세요"
            -3 -> "인터넷 연결 확인 후 다시 시도해주세요"
            else -> "다시 시도해주세요"
        }
        Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show() //TODO("내부 글로 바꿔주세요")
    }
}