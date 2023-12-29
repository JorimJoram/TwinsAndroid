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
    private val accountViewModel = AccountViewModel()
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
    }

    private val checkPassword = OnClickListener{
        val password = binding.passwordPasswordInput.text.toString()
        val confirm = binding.passwordPasswordConfirm.text.toString()
        //TODO("정규식을 이용해서 영문 + 숫자 + 특수문자 -> 8자 이상 들어갔는지 확인")
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*[!@#\$%^*+=-])(?=.*[0-9]).{8,15}\$")
        if(!passwordRegex.matches(password))
            showFailure(-1)
        //TODO("비밀번호 확인이 되었는지 확인")
        else{
            if(password == confirm)
                nextFragment(password)
            else
                showFailure(-2)
        }
    }

    private fun showFailure(code: Int){
        val msg:String = when(code){
            -1 -> "영문, 숫자, 특수문자를 모두 포함한 8자 이상을 입력해주세요"
            -2 -> "비밀번호가 서로 일치하지 않습니다."
            else -> "다시 시도해주세요"
        }
        Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show() //TODO("내부 글로 바꿔주세요")
    }

    private fun nextFragment(password:String){
        val accountInstance = Account.getInstance()
        accountInstance.password = password

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val telFragment = AccountFragmentTel()
        transaction!!.replace(R.id.account_frameLayout, telFragment)
        transaction.commit()
    }
}