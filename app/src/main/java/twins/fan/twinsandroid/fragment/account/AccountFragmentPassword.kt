package twins.fan.twinsandroid.fragment.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.databinding.FragmentAccountPasswordBinding
import twins.fan.twinsandroid.viewmodel.AccountViewModel

class AccountFragmentPassword : Fragment() {
    private lateinit var binding: FragmentAccountPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.passwordPasswordCheck.setOnClickListener(checkPassword)
        binding.passwordBackButton.setOnClickListener(backListener)
    }

    private val checkPassword = OnClickListener{
        val password = binding.passwordPasswordInput.text.toString()
        val confirm = binding.passwordPasswordConfirm.text.toString()
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*[!@#\$%^*+=-])(?=.*[0-9])(?!.*\\s).{8,15}\$")
        if(!passwordRegex.matches(password))
            showFailure(-1)
        else{
            if(password == confirm)
                nextFragment(password)
            else
                showFailure(-2)
        }
    }

    private fun showFailure(code: Int){
        val msg:String = when(code){
            -1 -> "영문, 숫자, 특수문자를 포함한 8자 이상을 입력해주세요\n(스페이스 제외)"
            -2 -> "비밀번호가 서로 일치하지 않습니다."
            else -> "다시 시도해주세요"
        }
        binding.passwordMsg.text = msg
    }

    private fun nextFragment(password:String){
        val accountInstance = Account.getInstance()
        accountInstance.password = password

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val telFragment = AccountFragmentTel()
        transaction!!.replace(R.id.account_frameLayout, telFragment)
        transaction.commit()
    }

    private val backListener = OnClickListener {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val usernameFragment = AccountFragmentUsername()
        transaction!!.replace(R.id.account_frameLayout, usernameFragment)
        transaction.commit()
    }
}