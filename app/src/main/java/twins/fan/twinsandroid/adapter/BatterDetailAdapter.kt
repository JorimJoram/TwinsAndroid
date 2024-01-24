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
            if(position / batterIndex.size == 0)
                setIndex(position, binding)
            else{
                val index = (position) % batterIndex.size
                val batterInfo = batterList[(position/batterIndex.size) - 1]
                binding.gameDetailBatterDetailItem.text = processingBatterDetail(batterInfo)[index]
            }
        }

        private fun setIndex(position: Int,binding: GameBatterDetailItemBinding) {
            binding.gameDetailBatterDetailItem.text = batterIndex[position%9]
        }
    }

    private fun processingBatterDetail(batterDetail:BatterDetailRecord): List<String> =listOf(if(batterDetail.entry.contains("-") && batterDetail.record != "") "PH" else if(batterDetail.entry.contains("-") && batterDetail.record == "") "DS" else batterDetail.entry, batterDetail.name, batterDetail.ab.toString(), batterDetail.hit.toString(), batterDetail.hr.toString(), batterDetail.rbi.toString(), batterDetail.bb.toString(), "0", if(batterDetail.avg == "NaN") ".000" else batterDetail.avg.substring(1,5))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatterDetailViewHolder {
        val binding = GameBatterDetailItemBinding.inflate(LayoutInflater.from(parent.context))
        return BatterDetailViewHolder(binding)
    }

    override fun getItemCount(): Int = (batterList.size+1) * batterIndex.size

    override fun onBindViewHolder(holder: BatterDetailViewHolder, position: Int) {
        holder.bind(position)
    }
}