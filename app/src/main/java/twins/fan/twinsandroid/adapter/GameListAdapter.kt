package twins.fan.twinsandroid.adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.game.GameRecord

class GameListAdapter(
    private val context: Context,
    private val gameList: List<GameRecord>?
):BaseAdapter() {
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

        final.text = game.final.replace("-", "\n")
        putLogo(view, game.versusTeam, game.stadium == "잠실종합운동장")
        //Log.d(TAG, "putScore: ${game.lgScore.split(",")}")
        putScore(view, game.stadium == "잠실종합운동장", game.lgScore.split(","), game.versusScore.split(","))

        return view
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
}