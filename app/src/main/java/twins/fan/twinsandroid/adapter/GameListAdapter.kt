package twins.fan.twinsandroid.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.data.game.UserVisit
import twins.fan.twinsandroid.fragment.main.game.GameDetailFragment
import twins.fan.twinsandroid.fragment.main.game.GameSearchFragment
import twins.fan.twinsandroid.util.checkLogo
import twins.fan.twinsandroid.util.pitchResult
import twins.fan.twinsandroid.util.scoreLocate
import twins.fan.twinsandroid.util.toFormattedDate
import twins.fan.twinsandroid.viewmodel.GameViewModel
class GameListAdapter(
    private val fragment:GameSearchFragment,
    private val context: Context,
    private val gameList: List<GameRecord>,
    private val userVisitList: List<String>
):BaseAdapter() {
    private val gameViewModel = GameViewModel()
    private val userInfo = AuthenticationInfo.getInstance()

    private fun checkSwitch(view: View, game: GameRecord){
        val switch = view.findViewById<SwitchCompat>(R.id.game_list_item_switch)
        val visitSet = HashSet(userVisitList)

        if(visitSet.contains(game.gameDate)){ switch.isChecked = true }
        switch.setOnCheckedChangeListener { _, isChecked ->
            fragment.lifecycleScope.launch {
                if(isChecked){
                    if(gameViewModel.createUserVisit(UserVisit(account=Account(username=userInfo.username!!, password="", tel=""), visitDate = game.gameDate))!!.id!!.toInt() != -1){
                        Toast.makeText(context, "등록되었습니다.", Toast.LENGTH_LONG).show()
                    }
                }else{
                    gameViewModel.deleteUserVisit(userInfo.username!!, game.gameDate)
                    Toast.makeText(context, "등록이 취소되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun getCount(): Int = gameList.size

    override fun getItem(position: Int): Any = gameList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.game_list_item, null)
        val game = gameList[position]

        val logo = checkLogo(game.versusTeam, game.stadium == "잠실종합운동장")
        val lgScoreSplit = game.lgScore.split(",")
        val versusScoreSplit = game.versusScore.split(",")
        val score = scoreLocate(game.stadium == "잠실종합운동장", lgScoreSplit, versusScoreSplit).split("_")
        val pitchGameResult = pitchResult(game.winner, game.loser, score[0].toInt(), score[1].toInt())

        val homeScoreView = view.findViewById<TextView>(R.id.game_list_item_homeScore)
        val visitScoreView = view.findViewById<TextView>(R.id.game_list_item_visitScore)
        homeScoreView.text = score[0]
        homeScoreView.typeface = if (score[0].toInt() > score[1].toInt()) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
        visitScoreView.text = score[1]
        visitScoreView.typeface = if (score[0].toInt() < score[1].toInt()) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT

        view.findViewById<TextView>(R.id.game_list_item_final).text = game.final.split("_").let {parts -> if (parts.size >= 2) "${parts[0]}\n${parts[1]}" else parts[0] }
        view.findViewById<TextView>(R.id.game_list_item_date).text = "${game.gameDate.substring(0, game.gameDate.length - 1).toFormattedDate()} ${game.startTime} ~ ${game.endTime}"

        Glide.with(context).load(logo[0])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view.findViewById(R.id.game_list_item_homeLogo))
        Glide.with(context).load(logo[1])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view.findViewById(R.id.game_list_item_visitLogo))

        val homePitchResultView = view.findViewById<TextView>(R.id.game_list_item_homePitchResult)
        val visitPitchResultView = view.findViewById<TextView>(R.id.game_list_item_visitPitchResult)
        homePitchResultView.text = pitchGameResult[0][0].toString()
        homePitchResultView.setTextColor(pitchGameResult[0][1] as Int)
        visitPitchResultView.text = pitchGameResult[1][0].toString()
        visitPitchResultView.setTextColor(pitchGameResult[1][1] as Int)

        checkSwitch(view, game)

        return view
    }

    fun setOnItemClickListener(listView: ListView){
        listView.setOnItemClickListener { parent, view, position, id ->
            val clickItem = getItem(position) as GameRecord

            val bundle = Bundle()
            bundle.putString("gameDate", clickItem.gameDate)

            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frameLayout, GameDetailFragment().apply { arguments = bundle })
            transaction.addToBackStack("GAME_DETAIL")
            transaction.commitAllowingStateLoss()

        }
    }
}