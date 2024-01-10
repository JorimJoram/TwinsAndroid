package twins.fan.twinsandroid.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
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
import twins.fan.twinsandroid.fragment.main.game.GameSearchFragment
import twins.fan.twinsandroid.viewmodel.GameViewModel
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class GameListAdapter(
    private val fragment:GameSearchFragment,
    private val context: Context,
    private val gameList: List<GameRecord>?,
    private val userVisitList: List<String>
):BaseAdapter() {
    private val gameViewModel = GameViewModel()
    private val userInfo = AuthenticationInfo.getInstance()
    override fun getCount(): Int {
        return gameList!!.size
    }

    override fun getItem(p0: Int): Any {
        return gameList!![p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.game_list_item, null)
        val game = gameList!![position]

        val final = view.findViewById<TextView>(R.id.game_list_item_final)
        val lgScoreSplit = game.lgScore.split(",")
        val versusScoreSplit = game.versusScore.split(",")

        final.text = game.final.replace("-", "\n")
        putLogo(view, game.versusTeam, game.stadium == "잠실종합운동장")
        putScore(view, game.stadium == "잠실종합운동장", lgScoreSplit, versusScoreSplit)
        putPitchResult(view, game.winner, game.loser)

        view.findViewById<TextView>(R.id.game_list_item_date).text = "${ dateFormat(game.gameDate.substring(0, game.gameDate.length - 1)) } ${game.startTime} ~ ${game.endTime}"

        Log.d(TAG, "visitList: $game")

        val switch = view.findViewById<SwitchCompat>(R.id.game_list_item_switch)
        val visitSet = HashSet(userVisitList)
        if(visitSet.contains(game.gameDate)){
            switch.isChecked = true
        }
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            fragment.lifecycleScope.launch {
                if(isChecked){
                    if(gameViewModel.createUserVisit(UserVisit(account=Account(username=userInfo.username!!, password="", tel=""), visitDate = game.gameDate))!!.id!!.toInt() != -1){
                        Toast.makeText(context, "성공", Toast.LENGTH_LONG).show()
                    }
                }else{
                    gameViewModel.deleteUserVisit(userInfo.username!!, game.gameDate)
                    Toast.makeText(context, "id:${game.id} username:${userInfo.username} / ${game.gameDate}의 관람이 삭제되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }

    private fun putPitchResult(view: View, winner: String, loser: String) {
        val homeScore = view.findViewById<TextView>(R.id.game_list_item_homeScore).text.toString().toInt()
        val visitScore = view.findViewById<TextView>(R.id.game_list_item_visitScore).text.toString().toInt()
        if(homeScore > visitScore){
            view.findViewById<TextView>(R.id.game_list_item_homePitchResult).text = "승: $winner"
            view.findViewById<TextView>(R.id.game_list_item_homePitchResult).setTextColor(Color.parseColor("#204B9B"))
            view.findViewById<TextView>(R.id.game_list_item_visitPitchResult).text = "패: $loser"
            view.findViewById<TextView>(R.id.game_list_item_visitPitchResult).setTextColor(Color.parseColor("#B32653"))
        }
    }

    private fun putScore(view: View, isLGHome: Boolean, lgScore: List<String>, versusScore: List<String>) {
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
        view.findViewById<TextView>(R.id.game_list_item_homeScore).text = homeScore
        view.findViewById<TextView>(R.id.game_list_item_visitScore).text = visitScore
    }

    private fun putLogo(view: View, versusTeam:String, isLGHome:Boolean) {
        var imgPath:Int = 0
        val visitLogo:Int
        val homeLogo:Int
        when(versusTeam){
            "삼성" -> imgPath = R.raw.lions
            "키움" -> imgPath = R.raw.heroes
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

        Glide.with(context).load(visitLogo)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view.findViewById(R.id.game_list_item_visitLogo))
        Glide.with(context).load(homeLogo)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view.findViewById(R.id.game_list_item_homeLogo))
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
}