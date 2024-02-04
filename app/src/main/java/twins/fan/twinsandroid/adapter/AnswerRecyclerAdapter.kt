package twins.fan.twinsandroid.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import twins.fan.twinsandroid.BuildConfig
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.databinding.ItemAnswerBinding
import twins.fan.twinsandroid.fragment.main.answer.AnswerFragment

class AnswerRecyclerAdapter(
    private val answerList:List<Answer>,
    private val context:Context
): RecyclerView.Adapter<AnswerRecyclerAdapter.AnswerViewHolder>() {
    inner class AnswerViewHolder(private val binding:ItemAnswerBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.answerItemUsername.text = answerList[position].accountUsername
            Log.d(TAG, "adapter-bind: ${answerList[position].accountImage}")
            Glide.with(context).load(BuildConfig.myUrl+answerList[position].accountImage).into(binding.answerItemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemAnswerBinding.inflate(LayoutInflater.from(parent.context))
        return AnswerViewHolder(binding)
    }

    override fun getItemCount(): Int = answerList.size

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(position)
    }
}