package twins.fan.twinsandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import twins.fan.twinsandroid.BuildConfig
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.databinding.ItemAnswerBinding
import twins.fan.twinsandroid.fragment.main.answer.AnswerFragment
import twins.fan.twinsandroid.util.setCreateDateForm
import kotlin.properties.Delegates

class AnswerRecyclerAdapter(
    private val answerList:List<Answer>,
    private val context:Context,
    private val deleteListener: AnswerFragment?
): RecyclerView.Adapter<AnswerRecyclerAdapter.AnswerViewHolder>() {
    inner class AnswerViewHolder(val binding:ItemAnswerBinding):RecyclerView.ViewHolder(binding.root){
        private val userInfo = AuthenticationInfo.getInstance()
        private var answerId by Delegates.notNull<Long>()
        fun bind(answer: Answer){
            answerId = answer.id!!
            binding.answerItemUsername.text = answer.accountUsername
            binding.answerItemCreateDate.text = setCreateDateForm(answer.createdDate!!)
            binding.answerItemContent.text = answer.content

            setAccountImage(answer)
            setDeleteButton(answer)
        }
        private fun setDeleteButton(answer: Answer) {
            if(answer.accountUsername == userInfo.username){
                binding.answerItemDeleteButton.visibility = View.VISIBLE
            }
            else
                binding.answerItemDeleteButton.visibility = View.GONE
        }
        private fun setAccountImage(answer:Answer){
            if(answer.accountImage.isNotBlank())
                Glide.with(context).load(BuildConfig.myUrl+answer.accountImage).into(binding.answerItemImage)
            else
                Glide.with(context).load(R.drawable.baseline_account_circle_24).into(binding.answerItemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemAnswerBinding.inflate(LayoutInflater.from(parent.context))
        return AnswerViewHolder(binding)
    }

    override fun getItemCount(): Int = answerList.size

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(answerList[position])
        holder.binding.answerItemDeleteButton.setOnClickListener {
            val beDeleteAnswer = answerList[position]
            deleteListener?.onDelete(beDeleteAnswer)
        }
    }
}