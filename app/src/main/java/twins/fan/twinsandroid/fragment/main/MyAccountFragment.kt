package twins.fan.twinsandroid.fragment.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.BuildConfig
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.AccountImage
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.FragmentMyAccountBinding
import twins.fan.twinsandroid.fragment.main.game.MyGameListFragment
import twins.fan.twinsandroid.fragment.main.my.ChangeInfoFragment
import twins.fan.twinsandroid.fragment.main.my.MyAnswerFragment
import twins.fan.twinsandroid.util.CacheClear
import twins.fan.twinsandroid.util.setRecentDateFormat
import twins.fan.twinsandroid.util.toFormattedDate
import twins.fan.twinsandroid.viewmodel.AccountViewModel
import twins.fan.twinsandroid.viewmodel.GallViewModel
import twins.fan.twinsandroid.viewmodel.GameViewModel

class MyAccountFragment : Fragment() {
    private lateinit var binding:FragmentMyAccountBinding
    private lateinit var accountImage:AccountImage

    private val userInfo = AuthenticationInfo.getInstance()
    private val accountViewModel = AccountViewModel()
    private val gameViewModel = GameViewModel()
    private val gallViewModel = GallViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountImage = AccountImage.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "Image: ${accountImage.path}")

        CacheClear(requireContext())

        if(accountImage.path.isNotBlank())
            Glide.with(requireContext()).load(BuildConfig.myUrl+accountImage.path).into(binding.accountProfileAccountImage)

        binding.accountProfileChangeButton.setOnClickListener(toChangeProfile)
        binding.accountProfileGame.setOnClickListener(toGameList)
        binding.accountProfileGallAmount.setOnClickListener(toMyGallery)
        binding.accountProfileAnswerAmount.setOnClickListener(toMyAnswer)
    }

    private val toGameList = OnClickListener {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout,MyGameListFragment())
        transaction.addToBackStack("MY_GAME_LIST")
        transaction.commitAllowingStateLoss()
    }

    private val toChangeProfile = OnClickListener {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, ChangeInfoFragment())
        transaction.addToBackStack("ACCOUNT_EDIT")
        transaction.commit()
    }

    private val toMyAnswer = OnClickListener {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, MyAnswerFragment())
        transaction.addToBackStack("ACCOUNT_ANSWER")
        transaction.commit()
    }

    private val toMyGallery = OnClickListener {  }
    override fun onResume() {
        super.onResume()
        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomBar?.menu?.findItem(R.id.menu_account)?.isChecked = true

        setAccountInfo()
        setVisitGame()
        setGallAmount()
        setAnswerAmount()
    }

    private fun setAccountInfo(){
        lifecycleScope.launch {
            val account = accountViewModel.getAccountByUsername(userInfo.username!!)!!
            binding.accountProfileUsername.text = account.username
        }
    }

    private fun setVisitGame(){
        lifecycleScope.launch{
            val recentGameDate = gameViewModel.getRecentUserVisit(userInfo.username!!)!!
            binding.accountProfileGame.text = setRecentDateFormat(recentGameDate.gameDate)
        }
    }

    private fun setGallAmount(){
        lifecycleScope.launch {
            val cnt = gallViewModel.getMyGalleryAmount(userInfo.username!!)
            binding.accountProfileGallAmount.text = cnt.toString()
        }
    }

    private fun setAnswerAmount(){
        lifecycleScope.launch {
            val cnt = gallViewModel.getMyAnswerAmount(userInfo.username!!)
            binding.accountProfileAnswerAmount.text = cnt.toString()
        }
    }
}