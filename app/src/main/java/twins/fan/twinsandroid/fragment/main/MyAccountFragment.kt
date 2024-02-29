package twins.fan.twinsandroid.fragment.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideExtension
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.BuildConfig
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.AccountImage
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.FragmentMyAccountBinding
import twins.fan.twinsandroid.fragment.main.my.ChangeInfoFragment
import twins.fan.twinsandroid.util.CacheClear
import twins.fan.twinsandroid.viewmodel.AccountViewModel

class MyAccountFragment : Fragment() {
    private lateinit var binding:FragmentMyAccountBinding
    private lateinit var accountImage:AccountImage

    private val userInfo = AuthenticationInfo.getInstance()
    private val accountViewModel = AccountViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountImage = AccountImage.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAccountInfo()
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "Image: ${accountImage.path}")

        CacheClear(requireContext())

        if(accountImage.path.isNotBlank())
            Glide.with(requireContext()).load(BuildConfig.myUrl+accountImage.path).into(binding.accountProfileAccountImage)

        binding.accountProfileChangeButton.setOnClickListener(toChangeProfile)
    }

    private val toChangeProfile = OnClickListener {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, ChangeInfoFragment())
        transaction.addToBackStack("ACCOUNT_EDIT")
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()
        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomBar?.menu?.findItem(R.id.menu_account)?.isChecked = true
    }

    private fun setAccountInfo(){
        lifecycleScope.launch {
            val account = accountViewModel.getAccountByUsername(userInfo.username!!)!!
            binding.accountProfileUsername.text = account.username
        }
    }
}