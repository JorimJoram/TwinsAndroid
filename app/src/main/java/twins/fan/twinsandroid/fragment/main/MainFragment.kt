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
import twins.fan.twinsandroid.fragment.main.game.GameSearchFragment
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
            val recentUserVisit = gameViewModel.getRecentUserVisit(userInfo.username!!)!!

            if(batterDetailDataList.size > 3) {
                val sortedByPoint = batterDetailDataList.sortedByDescending { it.point }.subList(0, 3)
                binding.mainRecordList.adapter =
                    BatterMainListAdapter(this@MainFragment, sortedByPoint)
            }
            putGameResult()
            putRecentVisit(recentUserVisit)
        }
    }

    private fun putRecentVisit(recentUserVisit: GameRecord) {
        binding.mainRecentGameRecord.gameListItemDate.text = "${dateFormat(recentUserVisit.gameDate.substring(0,recentUserVisit.gameDate.length-1)) } ${recentUserVisit.startTime}~${recentUserVisit.endTime}"
        binding.mainRecentGameRecord.gameListItemFinal.text = recentUserVisit.final.split("_").let {parts -> if (parts.size >= 2) "${parts[0]}\n${parts[1]}" else parts[0] }
        binding.mainRecentGameRecord.gameListItemSwitch.isChecked=true
        binding.mainRecentGameRecord.gameListItemSwitch.isEnabled=false

        val lgScoreSplit = recentUserVisit.lgScore.split(",")
        val versusScoreSplit = recentUserVisit.versusScore.split(",")

        putScore(recentUserVisit.stadium == "잠실종합운동장", lgScoreSplit, versusScoreSplit)
        putLogo(recentUserVisit.versusTeam, recentUserVisit.stadium == "잠실종합운동장")
        putPitchResult(recentUserVisit.winner, recentUserVisit.loser)
    }

    private fun putScore(isLGHome: Boolean, lgScore: List<String>, versusScore: List<String>) {
        val visitScore:String
        val homeScore:String
        when(isLGHome){
            true -> {
                visitScore = versusScore[versusScore.size-4]
                homeScore = lgScore[lgScore.size-4]
            }
            else ->{
                visitScore = lgScore[lgScore.size-4]
                homeScore = versusScore[versusScore.size-4]
            }
        }
        binding.mainRecentGameRecord.gameListItemHomeScore.text = homeScore
        binding.mainRecentGameRecord.gameListItemVisitScore.text = visitScore
    }

    private fun putLogo(versusTeam:String, isLGHome:Boolean) {
        var imgPath = 0
        val visitLogo:Int
        val homeLogo:Int
        when(versusTeam){
            "삼성" -> imgPath = R.raw.lions
            "키움" -> imgPath = R.raw.heroes
            "롯데" -> imgPath = R.raw.giants
            "두산" -> imgPath = R.raw.bears
            "KT"   -> imgPath = R.raw.wiz
            "SSG"  -> imgPath = R.raw.landers
            "한화" -> imgPath = R.raw.eagles
            "기아" -> imgPath = R.raw.tigers
            "NC"   -> imgPath = R.raw.dinos
        }
        when(isLGHome){
            true -> {
                visitLogo = imgPath
                homeLogo = R.raw.twins
            }
            else -> {
                visitLogo = R.raw.twins
                homeLogo = imgPath
            }
        }

        Glide.with(requireContext()).load(visitLogo)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.mainRecentGameRecord.gameListItemVisitLogo)
        Glide.with(requireContext()).load(homeLogo)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.mainRecentGameRecord.gameListItemHomeLogo)
    }

    private fun dateFormat(date:String): StringBuilder {
        val result = StringBuilder()
        for(i in date.indices step 2){
            result.append(date.substring(i, minOf(i+2, date.length)))
            if(i + 2 < date.length){
                result.append(".")
            }
        }

        val localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyMMdd"))
        val dayOfWeekKorean = localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ko-KR"))

        result.append("(${dayOfWeekKorean.substring(0,1)})")

        return result
    }

    private fun putPitchResult(winner: String, loser: String) {
        val homeScoreView = binding.mainRecentGameRecord.gameListItemHomeScore
        val visitScoreView = binding.mainRecentGameRecord.gameListItemVisitScore

        val homeScore = homeScoreView.text.toString().toInt()
        val visitScore = visitScoreView.text.toString().toInt()

        setPitchResult(
            binding.mainRecentGameRecord.gameListItemHomePitchResult,
            getPitchText(homeScore, visitScore, winner, loser),
            getPitchColor(homeScore, visitScore, "#204B9B", "#B32653")
        )

        setPitchResult(
            binding.mainRecentGameRecord.gameListItemVisitPitchResult,
            getPitchText(visitScore, homeScore, winner, loser),
            getPitchColor(visitScore, homeScore, "#204B9B", "#B32653")
        )

        homeScoreView.typeface = if (homeScore > visitScore) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
        visitScoreView.typeface = if (homeScore > visitScore) android.graphics.Typeface.DEFAULT else android.graphics.Typeface.DEFAULT_BOLD
    }

    private fun getPitchText(score1: Int, score2: Int, winner: String, loser: String): String {
        return when {
            score1 > score2 -> "승: $winner"
            score1 < score2 -> "패: $loser"
            else -> "무"
        }
    }

    private fun getPitchColor(score1: Int, score2: Int, winColor: String, loseColor: String): Int {
        return when {
            score1 > score2 -> Color.parseColor(winColor)
            score1 < score2 -> Color.parseColor(loseColor)
            else -> Color.BLACK
        }
    }

    private fun setPitchResult(textView: TextView, resultText: String, color: Int) {
        textView.text = resultText
        textView.setTextColor(color)
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