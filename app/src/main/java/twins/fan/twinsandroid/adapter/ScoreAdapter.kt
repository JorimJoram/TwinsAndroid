package twins.fan.twinsandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import twins.fan.twinsandroid.databinding.GameScoreItemBinding

class ScoreAdapter(
    private val isHome:Boolean,
    private val teamList:List<String>,
    private val lgScoreList:List<String>,
    private val versusScoreList:List<String>
):RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {
    inner class ScoreViewHolder(
        private val binding: GameScoreItemBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(position:Int){
            val home: String
            val away: String
            val iningList = listOf("","1","2","3","4","5","6","7","8","9","연장","R","H","E","B")

            val selectTeam = if(isHome) teamList else teamList.reversed()
            val homeScoreList = if(isHome) lgScoreList else versusScoreList
            val awayScoreList = if(!isHome) lgScoreList else versusScoreList

            home = if (position == 0) selectTeam[0] else if (position in 1..9) decToHex(homeScoreList[position - 1]) else homeScoreList[position - 1]
            away = if (position == 0) selectTeam[1] else if (position in 1..9) decToHex(awayScoreList[position - 1]) else awayScoreList[position - 1]

            binding.gameScoreItemIningInfo.text = iningList[position]
            binding.gameScoreItemHomeScore.text = home
            binding.gameScoreItemAwayScore.text = away
        }
    }

    private fun decToHex(score:String): String {
        if(score.startsWith("-")) return score
        return if(score.toInt() > 9) ('A' + score.toInt()-10).toString() else score
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = GameScoreItemBinding.inflate(LayoutInflater.from(parent.context))
        return ScoreViewHolder(binding)
    }

    override fun getItemCount(): Int = lgScoreList.size + 1 //팀명 적을칸까지
}