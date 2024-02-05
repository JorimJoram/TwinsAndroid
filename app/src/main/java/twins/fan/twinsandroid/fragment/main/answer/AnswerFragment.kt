package twins.fan.twinsandroid.fragment.main.answer

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
import twins.fan.twinsandroid.databinding.FragmentAnswerBinding
import twins.fan.twinsandroid.viewmodel.GameViewModel

/**
 * 아마도 답변 부분은 재사용될 가능성이 높아서 따로 패키지 파서 작성합니다
 * 리팩토링하면서 변경점 있다면 삭제해주시면 됩니다.
 */
class AnswerFragment() : Fragment() {
    private lateinit var binding:FragmentAnswerBinding
    private var gameDate = ""
    private val gameViewModel = GameViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameDate = arguments?.getString("gameDate")!!
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answer, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val answerList = gameViewModel.getAnswerList(gameDate)!!
            binding.answerList.layoutManager = LinearLayoutManager(context) //이거 꼭 있어야 한답니다
            binding.answerList.adapter = AnswerRecyclerAdapter(answerList, requireContext())
        }
    }
}