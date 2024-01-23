package twins.fan.twinsandroid.fragment.main.game

import android.content.ContentValues.TAG
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.BatterDetailAdapter
import twins.fan.twinsandroid.adapter.ScoreAdapter
import twins.fan.twinsandroid.data.game.BatterDetailRecord
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.databinding.FragmentGameDetailBinding
import twins.fan.twinsandroid.viewmodel.GameViewModel
import kotlin.math.log

class GameDetailFragment : Fragment() {
    private lateinit var binding:FragmentGameDetailBinding
    private var gameDate = ""
    private val gameViewModel = GameViewModel()

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

            val isHome = gameRecord.stadium == "잠실종합운동장"

            setScoreBoard(isHome, gameRecord.versusTeam,gameRecord.lgScore, gameRecord.versusScore)
            setBatterDetail(batterList)
        }
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