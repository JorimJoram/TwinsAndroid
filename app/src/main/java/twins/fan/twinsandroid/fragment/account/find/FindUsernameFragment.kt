package twins.fan.twinsandroid.fragment.account.find

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.FragmentFindUsernameBinding
import twins.fan.twinsandroid.viewmodel.AccountViewModel

class FindUsernameFragment : Fragment() {
    private lateinit var binding:FragmentFindUsernameBinding
    private val accountViewModel = AccountViewModel()
    private var tel = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tel = arguments?.getString("tel")!!
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_username, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            val userInfo = accountViewModel.getAccountByTel(tel)!!
            binding.findUsernameUsername.text = userInfo.username
            binding.findUsernameCreatedDate.text = "가입: ${userInfo.createdDate}"
        }

        binding.findUsernameToLogin.setOnClickListener(toLogin)
        binding.findUsernameToPassword.setOnClickListener(toPassword)
    }

    private val toLogin = OnClickListener{
        requireActivity().finish()
    }

    private val toPassword = OnClickListener {
        val bundle = Bundle()
        bundle.putString("tel", tel)

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.find_frameLayout, FindPasswordFragment().apply { arguments = bundle })
        transaction.addToBackStack("FIND_Password")
        transaction.commitAllowingStateLoss()
    }
}