package twins.fan.twinsandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.RecyclerView
import twins.fan.twinsandroid.data.game.BatterDetailRecord
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.databinding.GameScoreItemBinding
import twins.fan.twinsandroid.databinding.ItemViewBinding

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
            var home = ""
            var away = ""
            if(isHome){
                home = if(position == 0) teamList[0] else lgScoreList[position-1]
                away = if(position == 0) teamList[1] else versusScoreList[position-1]
            }else{
                home = if(position == 0) teamList[1] else versusScoreList[position-1]
                away = if(position == 0) teamList[0] else lgScoreList[position-1]
            }

            binding.gameScoreItemHomeScore.text = home
            binding.gameScoreItemAwayScore.text = away
        }
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