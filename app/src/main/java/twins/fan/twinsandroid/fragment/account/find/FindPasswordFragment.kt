package twins.fan.twinsandroid.fragment.account.find

import android.os.Bundle
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
import twins.fan.twinsandroid.databinding.FragmentFindPasswordBinding
import twins.fan.twinsandroid.util.hideSoftKeyboard
import twins.fan.twinsandroid.viewmodel.AccountViewModel


class FindPasswordFragment : Fragment() {
    private lateinit var binding:FragmentFindPasswordBinding
    private lateinit var account: Account

    private val accountViewModel = AccountViewModel()
    private var tel = ""
    private var username = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tel = arguments?.getString("tel")!!
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_password, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.findPasswordUsernameCheckButton.setOnClickListener(checkUsernameListener)
        binding.findPasswordChangeButton.setOnClickListener(changePasswordListener)
    }

    private val checkUsernameListener = OnClickListener{
        username = binding.findPasswordUsername.text.toString()
        if(username.isBlank()) setUsernameCheckMsg("등록했던 아이디를 입력해주세요.") else checkUsername()
    }

    /**
     * 재사용 고려 필요 -> AccountAct 고려 바람
     */
    private val changePasswordListener = OnClickListener {
        val password = binding.findPasswordPasswordInput.text.toString()
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*[!@#\$%^*+=-])(?=.*[0-9])(?!.*\\s).{8,15}\$")

        if(!passwordRegex.matches(password)) setPasswordCheckMsg("영문, 숫자, 특수문자를 포함한 8자 이상을 입력해주세요\n(스페이스 제외)")

        if(!checkEqualPassword()) setPasswordCheckMsg("비밀번호가 서로 일치하지 않습니다.") else savePassword()
    }

    private fun savePassword(){
        account.password = binding.findPasswordPasswordInput.text.toString()
        lifecycleScope.launch {
            val result = accountViewModel.updateAccount(account)!!
            if(result.id!! > 0) toLogin() else setPasswordCheckMsg("다시 시도해주세요")
        }
    }

    private fun toLogin(){
        Toast.makeText(requireContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    private fun checkEqualPassword(): Boolean {
        return binding.findPasswordPasswordInput.text.toString() == binding.findPasswordPasswordConfirm.text.toString()
    }

    private fun checkUsername(){
        lifecycleScope.launch {
            account = accountViewModel.getAccountByUsernameAndTel(username, tel)!!
            if(account.username.isBlank())
                setUsernameCheckMsg("아이디를 다시 확인해주세요")
            else{
                hideSoftKeyboard(requireActivity())
                setUsernameCheckMsg("비밀번호를 변경해주세요")
                binding.findPasswordChangeButton.isEnabled = true
            }
        }
    }

    private fun setUsernameCheckMsg(msg:String){
        val result = binding.findPasswordUsernameMsg
        result.text = msg
    }

    private fun setPasswordCheckMsg(msg:String){
        val result = binding.findPasswordPasswordMsg
        result.text = msg
    }
}