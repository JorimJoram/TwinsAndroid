package twins.fan.twinsandroid.fragment.main

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.BatterMainListAdapter
import twins.fan.twinsandroid.adapter.MainViewPagerAdapter
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.data.game.TotalDetailRecord
import twins.fan.twinsandroid.data.game.UserVisit
import twins.fan.twinsandroid.databinding.FragmentMainBinding
import twins.fan.twinsandroid.fragment.main.game.GameSearchFragment
import twins.fan.twinsandroid.viewmodel.GameViewModel
import twins.fan.twinsandroid.viewmodel.LoginViewModel


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var isClicked = false
    private val loginViewModel=LoginViewModel()
    private val gameViewModel = GameViewModel()

    private lateinit var batterDetailDataList:List<TotalDetailRecord>
    private lateinit var userVisitResultList:List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        Glide.with(requireContext())
            .load(R.raw.twins)
            .into(binding.mainWinRateTeamImage)

        return binding.root
    }

    private val testListener = OnClickListener{
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val gameSearchFragment = GameSearchFragment()
        transaction!!.replace(R.id.main_frameLayout, gameSearchFragment)
        transaction.addToBackStack("GAME_SEARCH")
        transaction.commitAllowingStateLoss()
    }

    private val backButtonEvent = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            if(isClicked){
                requireActivity().finishAffinity()
                lifecycleScope.launch { loginViewModel.logoutProcess() }
            }
            isClicked = true
            Toast.makeText(requireContext(), "종료하시려면 한 번 더 눌러주세요", Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainWinRateContainer.setOnClickListener(testListener)
    }

    override fun onResume() {
        super.onResume()

        isClicked = false
        requireActivity().onBackPressedDispatcher.addCallback(this, backButtonEvent)

        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomBar?.menu?.findItem(R.id.menu_main)?.isChecked = true

        getMyData()

        //binding.mainViewPager.adapter = MainViewPagerAdapter(getViewPagerItem())
        //binding.mainViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun getViewPagerItem(): List<View> {
        return listOf<View>(
            LayoutInflater.from(requireContext()).inflate(R.layout.view_pager_item1, null),
            LayoutInflater.from(requireContext()).inflate(R.layout.view_pager_item2, null),
        )
    }

    private fun getMyData(){
        val userInfo = AuthenticationInfo.getInstance()
        lifecycleScope.launch{
            batterDetailDataList = gameViewModel.getTotalDetailData(userInfo.username!!)!!
            userVisitResultList = gameViewModel.getUserGameResult(userInfo.username!!)!!

            if(batterDetailDataList.size > 3) {
                val sortedByPoint = batterDetailDataList.sortedByDescending { it.point }.subList(0, 3)
                binding.mainRecordList.adapter =
                    BatterMainListAdapter(this@MainFragment, sortedByPoint)
            }
            putGameResult()
        }
    }

    private fun putGameResult(){
        val resultList = mutableListOf<Int>(0,0,0)
        userVisitResultList.forEach {
            when(it){
                "승" -> resultList[0] += 1
                "패" -> resultList[1] += 1
                "무" -> resultList[2] += 1
            }
        }
        var resultText = "${resultList[0]}승 ${resultList[1]}패 "
        if(resultList[2] > 0) resultText += "${resultList[2]}무 "
        resultText += "${"%.2f".format((resultList[0].toDouble()/userVisitResultList.size.toDouble()* 100))}%"

        binding.mainWinRateResult.text = resultText
    }
}