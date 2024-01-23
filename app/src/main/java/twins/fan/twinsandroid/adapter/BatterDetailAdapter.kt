package twins.fan.twinsandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import twins.fan.twinsandroid.data.game.BatterDetailRecord
import twins.fan.twinsandroid.databinding.GameBatterDetailItemBinding

class BatterDetailAdapter(private val batterList:List<BatterDetailRecord>, private val batterIndex:List<String>): RecyclerView.Adapter<BatterDetailAdapter.BatterDetailViewHolder>(){
    inner class BatterDetailViewHolder(
        private val binding: GameBatterDetailItemBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(position:Int){
            val batterInfo = batterList[position/batterIndex.size]
            val index = position % batterIndex.size

            binding.gameDetailBatterDetailItem.text = processingBatterDetail(batterInfo)[index]
        }
    }

    private fun processingBatterDetail(batterDetail:BatterDetailRecord): List<String> =listOf(batterDetail.entry, batterDetail.name, batterDetail.ab.toString(), batterDetail.hit.toString(), batterDetail.hr.toString(), batterDetail.rbi.toString(), batterDetail.bb.toString(), "0", batterDetail.avg.substring(1,5))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatterDetailViewHolder {
        val binding = GameBatterDetailItemBinding.inflate(LayoutInflater.from(parent.context))
        return BatterDetailViewHolder(binding)
    }

    override fun getItemCount(): Int = batterList.size * batterIndex.size//BatterDetailRecord::class.java.declaredFields.size-3

    override fun onBindViewHolder(holder: BatterDetailViewHolder, position: Int) {
        holder.bind(position)
    }
}