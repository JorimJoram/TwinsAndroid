package twins.fan.twinsandroid.fragment.account.find

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.FragmentFindMainBinding
import twins.fan.twinsandroid.exception.BlankInputException
import twins.fan.twinsandroid.util.hideSoftKeyboard
import twins.fan.twinsandroid.viewmodel.AccountViewModel
import java.net.SocketTimeoutException

class FindMainFragment : Fragment() {
    private lateinit var binding: FragmentFindMainBinding
    private val accountViewModel = AccountViewModel()
    private var code = "-1"
    private var tel = "-1"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_main, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.findMainSendCodeButton.setOnClickListener(sendListener)
        binding.findMainTelCodeCheck.setOnClickListener(checkCode)
        binding.findMainUsernameButton.setOnClickListener(toFindUsername)
        binding.findMainPasswordButton.setOnClickListener(toFindPassword)
    }

    private val toFindPassword = OnClickListener {
        val bundle = Bundle()
        bundle.putString("tel", tel)

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.find_frameLayout, FindPasswordFragment().apply { arguments = bundle })
        transaction.addToBackStack("FIND_Password")
        transaction.commitAllowingStateLoss()
    }

    private val toFindUsername = OnClickListener {
        val bundle = Bundle()
        bundle.putString("tel", tel)

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.find_frameLayout, FindUsernameFragment().apply { arguments = bundle })
        transaction.addToBackStack("FIND_USERNAME")
        transaction.commitAllowingStateLoss()
    }

    private val sendListener = OnClickListener{
        tel = binding.findMainTelInput.text.toString()
        if(tel.isBlank()) showFailMsg(BlankInputException("전화번호 미입력")) else showAlert()
    }

    private val checkCode = OnClickListener {
        val userCode = binding.findMainTelCode.text.toString()
        if(userCode == code){
            binding.findMainTelCodeCheck.isEnabled = false
            binding.findMainNextContainer.visibility = View.VISIBLE
            hideSoftKeyboard(requireActivity())
        } else showMsg(-5)
    }

    /**
     * 재사용 AccountFragmentTel
     */
    private fun sendCode(){
        val loadingAnimation = binding.findMainLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch {
            try{
                code = accountViewModel.sendCode("f$tel")!!
                when(code){
                    "-1" -> showMsg(-1)
                    "-3" -> showMsg(-5)
                    else -> {
                        countDown()
                        binding.findMainMsg.text = code //TODO("커밋하기 전에 지워라")
                        showMsg(2)
                        binding.findMainTelCodeCheck.isEnabled = true
                    }
                }
            }catch (e: SocketTimeoutException){
                showMsg(-3)
            }catch (e:Exception){
                Log.e(ContentValues.TAG, "sendCode: ${e.message}", )
                binding.findMainSendCodeButton.isEnabled = true
                showMsg(-100)
            }
        }

        loadingAnimation.cancelAnimation()
        loadingAnimation.visibility = View.GONE
    }

    /**
     * 재사용 AccountFragmentTel
     */
    private fun showMsg(code:Int){
        val msg:String = when(code){
            1 -> "회원가입이 완료되었습니다."
            2 -> "인증번호를 발송했습니다."
            -1 -> "전화번호를 다시 확인해주세요"
            -2 -> "인증번호를 다시 확인해주세요"
            -3 -> "인터넷 연결 확인 후 다시 시도해주세요"
            -4 -> "이미 등록된 전화번호입니다"
            -5 -> "등록되지 않은 전화번호입니다."
            else -> "다시 시도해주세요"
        }
        if(code > 1){
            // binding.findMainMsg.text = ""
            Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
        }else{
            val msgView = binding.findMainMsg
            msgView.text = msg
        }
    }

    /**
     * 재사용 AccountFragmentTel
     */
    private fun countDown(){
        val time = 5L;
        val timer = object: CountDownTimer(time * 60 * 1000, 1000){
            override fun onTick(remainTime: Long) {
                binding.findMainSendCodeButton.isEnabled = false
                val min = (remainTime / 1000) / 60
                val second = (remainTime/ 1000) % 60
                val formattedTime = "%02d:%02d".format(min, second)
                binding.findMainSendCodeButton.text = formattedTime
            }

            override fun onFinish() {
                binding.findMainSendCodeButton.text = "인증"
                binding.findMainSendCodeButton.isEnabled = true
            }
        }.start()
    }

    /**
     * 재사용 AccountFragmentTel
     */
    private fun showAlert(){
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("전화번호 확인").setMessage("${tel}으로 인증번호를 전송합니다.\n전송한 번호는 3분 뒤에 다시 전송할 수 있습니다.")
        builder.setPositiveButton("예"){
                dialog, which -> sendCode()
        }
        builder.setNegativeButton("아니오"){
                dialog, which -> dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showFailMsg(e:Exception){
        when(e){
            is BlankInputException -> binding.findMainMsg.text = "전화번호를 입력해주세요"
            else -> binding.findMainMsg.text = "다시 시도해주세요"
        }
    }

}