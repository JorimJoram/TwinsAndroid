package twins.fan.twinsandroid.fragment.main.game

import android.content.ContentValues.TAG
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.BatterDetailAdapter
import twins.fan.twinsandroid.adapter.ScoreAdapter
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.game.BatterDetailRecord
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.data.game.UserVisit
import twins.fan.twinsandroid.databinding.FragmentGameDetailBinding
import twins.fan.twinsandroid.util.checkLogo
import twins.fan.twinsandroid.util.scoreLocate
import twins.fan.twinsandroid.util.toFormattedDate
import twins.fan.twinsandroid.viewmodel.GameViewModel
import kotlin.math.log

class GameDetailFragment : Fragment() {
    private lateinit var binding:FragmentGameDetailBinding
    private var gameDate = ""
    private val gameViewModel = GameViewModel()
    private val userInfo = AuthenticationInfo.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameDate = arguments?.getString("gameDate")!!
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_detail, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val gameRecord = gameViewModel.getGameRecordByDate(gameDate)
            val batterList = gameViewModel.getBatterDetailByGameRecordId(gameDate)
            val isVisit = gameViewModel.getUserVisitDate(userInfo.username!!, gameRecord.gameDate)?:false
            val visitCnt = gameViewModel.getUserVisitCntByDate(gameDate)!!

            val isHome = gameRecord.stadium == "잠실종합운동장"

            setScoreBoard(isHome, gameRecord.versusTeam,gameRecord.lgScore, gameRecord.versusScore)
            setBatterDetail(batterList)
            binding.gameDetailBatterComments.text = "* PH: 지명타자, Pinch Hitter\n* DS: 대수비, Defensive Substitution"
            setScoreContainer(gameRecord)
            binding.gameDetailSwitchCnt.text = visitCnt.toString()

            val visitSwitch = binding.gameDetailSwitch
            visitSwitch.isChecked =  isVisit
            visitSwitch.setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    if(isChecked){
                        if(gameViewModel.createUserVisit(UserVisit(account= Account(username=userInfo.username!!, password="", tel=""), visitDate = gameRecord.gameDate))!!.id!!.toInt() != -1){
                            Toast.makeText(context, "등록되었습니다.", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        gameViewModel.deleteUserVisit(userInfo.username!!, gameRecord.gameDate)
                        Toast.makeText(context, "등록이 취소되었습니다.", Toast.LENGTH_LONG).show()
                    }
                    binding.gameDetailSwitchCnt.text = gameViewModel.getUserVisitCntByDate(gameDate).toString()
                }
            }
        }
    }

    private fun setScoreContainer(game:GameRecord){
        binding.gameDetailDate.text = game.gameDate.substring(0, game.gameDate.length - 1).toFormattedDate()
        binding.gameDetailTime.text = "${game.stadium} ${game.startTime}~${game.endTime}"

        val logo = checkLogo(game.versusTeam, game.stadium=="잠실종합운동장")
        Glide.with(requireContext()).load(logo[0])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.gameDetailHomeLogo)
        Glide.with(requireContext()).load(logo[1])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.gameDetailVisitLogo)

        val lgScoreSplit = game.lgScore.split(",")
        val versusScoreSplit = game.versusScore.split(",")
        val score = scoreLocate(game.stadium == "잠실종합운동장", lgScoreSplit, versusScoreSplit).split("_")
        val homeScoreView = binding.gameDetailHomeScore
        val visitScoreView = binding.gameDetailVisitScore
        homeScoreView.text = score[0]
        homeScoreView.typeface = if (score[0].toInt() > score[1].toInt()) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
        visitScoreView.text = score[1]
        visitScoreView.typeface = if (score[0].toInt() < score[1].toInt()) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT

        binding.gameDetailDetailFinal.text = game.final.split("_").let {parts -> if (parts.size >= 2) "${parts[0]}\n${parts[1]}" else parts[0] }
    }

    private fun setBatterDetail(batterList: List<BatterDetailRecord>) {
        val batterIndex = listOf("타순", "이름", "타수", "안타", "홈런", "타점", "사사구", "삼진", "타율")

        val layoutManager = GridLayoutManager(requireContext(), 22)
        layoutManager.spanSizeLookup = object :GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(position%9 == 1) 4 else if(position%9 == 6) 3 else 2
            }
        }
        binding.gameDetailBatterDetail.layoutManager = layoutManager
        binding.gameDetailBatterDetail.adapter = BatterDetailAdapter(batterList, batterIndex)
    }

    private fun setScoreBoard(isHome:Boolean, versusTeam:String, lgScore:String, versusScore:String){
        val lgScoreList = processingScoreList(lgScore.split(",").toMutableList())
        val versusScoreList = processingScoreList(versusScore.split(",").toMutableList())
        val teamList = listOf("LG", versusTeam)

        val layoutManager =  GridLayoutManager(requireContext(), 23)
        layoutManager.spanSizeLookup = object:GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(position == 0) 3 else if(position in lgScoreList.size-4 ..lgScoreList.size) 2 else 1
            }
        } //그리드 레이아웃 크기 조절

        binding.gameDetailScoreBoard.layoutManager = layoutManager
        binding.gameDetailScoreBoard.adapter = ScoreAdapter(isHome, teamList, lgScoreList, versusScoreList)
    }

    private fun processingScoreList(scoreList:MutableList<String>): MutableList<String> {
        scoreList.removeAll{it.contains("연장)")}
        val resultScoreList = MutableList(14){"0"}
        Log.d(TAG, "processingScoreList: $scoreList")
        var extraScore = 0
        var extraCnt = 0
        scoreList.forEachIndexed {index, score ->
            if(score.contains("(")){
                extraScore += score.split("(")[0].toIntOrNull() ?: 0
                extraCnt += 1
                resultScoreList[9] = extraScore.toString()
            }else{
                if(index in 0 .. 8){
                    resultScoreList[index] = score
                }else{
                    resultScoreList[index-extraCnt+1] = score
                }
            }
        }
        Log.d(TAG, "processingScoreList: $resultScoreList")
        return resultScoreList
    }
}