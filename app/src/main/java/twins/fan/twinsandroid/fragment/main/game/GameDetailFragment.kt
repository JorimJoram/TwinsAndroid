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
import twins.fan.twinsandroid.adapter.ScoreAdapter
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.databinding.FragmentGameDetailBinding
import twins.fan.twinsandroid.viewmodel.GameViewModel

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
        }
    }

    private fun setScoreBoard(isHome:Boolean, versusTeam:String, lgScore:String, versusScore:String){
        val lgScoreList = lgScore.split(",")
        val versusScoreList = versusScore.split(",")
        val teamList = listOf("LG", versusTeam)

        val layoutManager =  GridLayoutManager(requireContext(), 22)
        layoutManager.spanSizeLookup = object:GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(position == 0) 3 else if(position in lgScoreList.size-4 ..lgScoreList.size) 2 else 1
            }
        }
        binding.gameDetailScoreBoard.layoutManager = layoutManager
        binding.gameDetailScoreBoard.adapter = ScoreAdapter(isHome, teamList, lgScoreList, versusScoreList)
    }
}