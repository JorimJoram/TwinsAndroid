package twins.fan.twinsandroid.fragment.main.my

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.AnswerRecyclerAdapter
import twins.fan.twinsandroid.adapter.GalleryDeleteListener
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.databinding.FragmentMyAnswerBinding
import twins.fan.twinsandroid.viewmodel.GallViewModel

class MyAnswerFragment : Fragment(), GalleryDeleteListener {
    private lateinit var binding: FragmentMyAnswerBinding

    private val userInfo = AuthenticationInfo.getInstance()
    private val gallViewModel = GallViewModel()
    private var isGameDetail = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_answer, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setMyAnswer()
    }

    private fun setMyAnswer(){
        lifecycleScope.launch {
            val answerList = gallViewModel.getMyAnswerList(userInfo.username!!)!!

            binding.myAnswerAnswerList.layoutManager = LinearLayoutManager(context)
            val answerAdapter = AnswerRecyclerAdapter(answerList, requireContext(), null)
            binding.myAnswerAnswerList.adapter = answerAdapter
        }
    }

    override fun onDelete(answer: Answer) {
        return ;
    }
}