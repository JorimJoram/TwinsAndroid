package twins.fan.twinsandroid.fragment.main

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import twins.fan.twinsandroid.fragment.main.game.GameDetailFragment
import twins.fan.twinsandroid.fragment.main.game.GameSearchFragment
import twins.fan.twinsandroid.util.checkLogo
import twins.fan.twinsandroid.util.pitchResult
import twins.fan.twinsandroid.util.scoreLocate
import twins.fan.twinsandroid.util.toFormattedDate
import twins.fan.twinsandroid.viewmodel.GameViewModel
import twins.fan.twinsandroid.viewmodel.LoginViewModel
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var isClicked = false
    private val loginViewModel=LoginViewModel()
    private val gameViewModel = GameViewModel()

    private lateinit var batterDetailDataList:List<TotalDetailRecord>
    private lateinit var userVisitResultList:List<String>
    private lateinit var recentGameRecord:GameRecord

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

    private fun getMyData(){
        val userInfo = AuthenticationInfo.getInstance()
        lifecycleScope.launch{
            batterDetailDataList = gameViewModel.getTotalDetailData(userInfo.username!!)!!
            userVisitResultList = gameViewModel.getUserGameResult(userInfo.username!!)!!
            recentGameRecord = gameViewModel.getRecentUserVisit(userInfo.username!!)!!

            if(batterDetailDataList.size > 3) {
                val sortedByPoint = batterDetailDataList.sortedByDescending { it.point }.subList(0, 3)
                binding.mainRecordList.adapter =
                    BatterMainListAdapter(this@MainFragment, sortedByPoint)
            }
            putGameResult()
            if(recentGameRecord.id != -1L) {
                putRecentGameRecord(recentGameRecord)
                binding.mainRecentGameRecord.gameListItemSwitch.visibility = View.VISIBLE
            }else
                binding.mainRecentGameRecord.gameListItemSwitch.visibility = View.GONE
        }

        binding.mainRecentGameRecord.gameListItemMatchContainer.setOnClickListener(goDetail)
    }

    private val goDetail = OnClickListener {
        val bundle = Bundle()
        bundle.putString("gameDate", recentGameRecord.gameDate)

        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, GameDetailFragment().apply { arguments = bundle })
        transaction.addToBackStack("GAME_DETAIL")
        transaction.commitAllowingStateLoss()
    }

    private fun putRecentGameRecord(recentGameRecord: GameRecord) {
        val lgScoreSplit = recentGameRecord.lgScore.split(",")
        val versusScoreSplit = recentGameRecord.versusScore.split(",")
        val score = scoreLocate(recentGameRecord.stadium == "잠실종합운동장", lgScoreSplit, versusScoreSplit).split("_")
        val logo = checkLogo(recentGameRecord.versusTeam, recentGameRecord.stadium == "잠실종합운동장")
        val pitchGameResult = pitchResult(recentGameRecord.winner, recentGameRecord.loser, score[0].toInt(), score[1].toInt())
        val dateString = "${recentGameRecord.gameDate.substring(0,recentGameRecord.gameDate.length-1).toFormattedDate() } ${recentGameRecord.startTime}~${recentGameRecord.endTime}"

        binding.mainRecentGameRecord.gameListItemDate.text = dateString
        binding.mainRecentGameRecord.gameListItemFinal.text = recentGameRecord.final.split("_").let {parts -> if (parts.size >= 2) "${parts[0]}\n${parts[1]}" else parts[0] }

        binding.mainRecentGameRecord.gameListItemSwitch.isChecked=true
        binding.mainRecentGameRecord.gameListItemSwitch.isEnabled=false

        binding.mainRecentGameRecord.gameListItemHomeScore.text = score[0]
        binding.mainRecentGameRecord.gameListItemHomeScore.typeface = if (score[0] > score[1]) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
        binding.mainRecentGameRecord.gameListItemVisitScore.text = score[1]
        binding.mainRecentGameRecord.gameListItemVisitScore.typeface = if (score[0] < score[1]) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT

        Glide.with(requireContext()).load(logo[0])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.mainRecentGameRecord.gameListItemHomeLogo)
        Glide.with(requireContext()).load(logo[1])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.mainRecentGameRecord.gameListItemVisitLogo)

        binding.mainRecentGameRecord.gameListItemHomePitchResult.text = pitchGameResult[0][0].toString()
        binding.mainRecentGameRecord.gameListItemHomePitchResult.setTextColor(pitchGameResult[0][1] as Int)
        binding.mainRecentGameRecord.gameListItemVisitPitchResult.text = pitchGameResult[1][0].toString()
        binding.mainRecentGameRecord.gameListItemVisitPitchResult.setTextColor(pitchGameResult[1][1] as Int)
    }
    private fun putGameResult(){
        if(userVisitResultList.isEmpty()){
            binding.mainWinRateResult.text = "경기를 기록해보세요!"
            return ;
        }
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