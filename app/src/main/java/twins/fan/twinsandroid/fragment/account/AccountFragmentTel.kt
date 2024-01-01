package twins.fan.twinsandroid.fragment.account

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        sendCodeButton.setOnClickListener(isSend)
        checkCodeButton.setOnClickListener(checkCode)

        binding.telBackButton.setOnClickListener(backListener)
    }

    private val isSend = OnClickListener{
        showAlert()
    }

    private fun sendCode(){
        val loadingAnimation = binding.telLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch {
            try{
                code = accountViewModel.sendCode(binding.telTelInput.text.toString())!!
                when(code){
                    "-1" -> showMsg(-1)
                    "-2" -> showMsg(-4)
                    else -> {
                        countDown()
                        showMsg(2)
                        binding.telTelCodeCheck.isEnabled = true
                    }
                }
            }catch (e:SocketTimeoutException){
                showMsg(-3)
            }catch (e:Exception){
                Log.e(TAG, "sendCode: ${e.message}", )
                binding.telSendCodeButton.isEnabled = true
                showMsg(-100)
            }finally {
                loadingAnimation.cancelAnimation()
                loadingAnimation.visibility = View.GONE
            }
        }
    }

    private val checkCode = OnClickListener {
        if(binding.telTelCode.text.toString() != code)
            showMsg(-2)
        else{
            val accountInstance = Account.getInstance()
            accountInstance.tel = binding.telTelInput.text.toString()
            accountInstance.role = "USER"

            val loadingAnimation = binding.telLottieView
            loadingAnimation.visibility = View.VISIBLE
            loadingAnimation.playAnimation()

            lifecycleScope.launch{
                try{
                    val result = accountViewModel.createAccount(accountInstance)
                    if(result.isSuccessful){
                        if(result.body()!!.id == -1L){
                            showMsg(-4)
                        }else{
                            showMsg(1) // 성공한건데, 일단 임시로 이렇게 두겠습니다.
                            activity?.finish()
                        }
                    }else{
                        showMsg(-5) //다시 시도 해달라고 하는게 맞는듯
                    }
                }catch (e:SocketTimeoutException){
                    showMsg(-3)
                }catch (e:Exception){
                    Log.e(TAG, "error: $e")
                }finally {
                    loadingAnimation.cancelAnimation()
                    loadingAnimation.visibility = View.GONE
                }
            }
        }

    }

    private fun showMsg(code:Int){
        val msg:String = when(code){
            1 -> "회원가입이 완료되었습니다."
            2 -> "인증번호를 발송했습니다."
            -1 -> "전화번호를 다시 확인해주세요"
            -2 -> "인증번호를 다시 확인해주세요"
            -3 -> "인터넷 연결 확인 후 다시 시도해주세요"
            -4 -> "이미 등록된 전화번호입니다"
            else -> "다시 시도해주세요"
        }
        if(code > 1){
            binding.telMsg.text = ""
            Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
        }else{
            val msgView = binding.telMsg
            msgView.text = msg
        }
    }

    private fun countDown(){
        val time = 5L;
        val timer = object: CountDownTimer(time * 60 * 1000, 1000){
            override fun onTick(remainTime: Long) {
                binding.telSendCodeButton.isEnabled = false
                val min = (remainTime / 1000) / 60
                val second = (remainTime/ 1000) % 60
                val formattedTime = "%02d:%02d".format(min, second)
                binding.telSendCodeButton.text = formattedTime
            }

            override fun onFinish() {
                binding.telSendCodeButton.text = "인증"
                binding.telSendCodeButton.isEnabled = true
            }
        }.start()
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("전화번호 확인").setMessage("${binding.telTelInput.text}으로 인증번호를 전송합니다.\n전송한 번호는 3분 뒤에 다시 전송할 수 있습니다.")
        builder.setPositiveButton("예"){
                dialog, which -> sendCode()
        }
        builder.setNegativeButton("아니오"){
                dialog, which -> dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private val backListener = OnClickListener {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val passwordFragment = AccountFragmentPassword()
        transaction!!.replace(R.id.account_frameLayout, passwordFragment)
        transaction.commit()
    }
}