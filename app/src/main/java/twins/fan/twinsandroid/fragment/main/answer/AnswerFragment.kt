package twins.fan.twinsandroid.fragment.main.answer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.activity.MainActivity
import twins.fan.twinsandroid.adapter.AnswerRecyclerAdapter
import twins.fan.twinsandroid.adapter.GalleryDeleteListener
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.answer.Answer
import twins.fan.twinsandroid.databinding.FragmentAnswerBinding
import twins.fan.twinsandroid.viewmodel.GallViewModel
import twins.fan.twinsandroid.viewmodel.GameViewModel

/**
 * 아마도 답변 부분은 재사용될 가능성이 높아서 따로 패키지 파서 작성합니다
 * 리팩토링하면서 변경점 있다면 삭제해주시면 됩니다.
 */
class AnswerFragment() : Fragment(), GalleryDeleteListener {
    private lateinit var binding:FragmentAnswerBinding

    private var gameDate = ""
    private var gallId = -1L
    private var isGameDetail = false

    private val gameViewModel = GameViewModel()
    private val gallViewModel = GallViewModel()
    private val mainActivity = MainActivity()

    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
        gameDate = arguments?.getString("gameDate") ?: ""
        gallId = arguments?.getLong("gallId") ?: -1L
        isGameDetail = gameDate.isNotBlank() //게임 종합 프레그먼트인지 확인
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_answer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.answerSaveButton.setOnClickListener(createAnswer)
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            setAnswerList(if(isGameDetail) gameViewModel.getAnswerList(gameDate)!! else gallViewModel.getAnswerByGallId(gallId)!!)
        }
    }

    private val createAnswer = OnClickListener{
        val answer = if(isGameDetail) Answer(accountUsername = AuthenticationInfo.getInstance().username!!, accountImage = "", gameRecordGameDate = gameDate, content = binding.answerEditText.text.toString()) else Answer(accountUsername = AuthenticationInfo.getInstance().username!!, accountImage = "", galleryId = gallId, content = binding.answerEditText.text.toString())
        lifecycleScope.launch{
            val answerList = if(isGameDetail) gameViewModel.createAnswer(answer)!! else gallViewModel.createAnswer(answer)!!
            binding.answerEditText.setText("")
            setAnswerList(answerList)
        }
    }

    /**
     * 어댑터에 댓글 붙이기
     */
    private fun setAnswerList(answerList:List<Answer>){
        binding.answerList.layoutManager = LinearLayoutManager(context) //이거 꼭 있어야 한답니다
        val answerAdapter = AnswerRecyclerAdapter(answerList, requireContext(), this)

        binding.answerList.adapter = answerAdapter
    }

    override fun onDelete(answer: Answer) {
        lifecycleScope.launch {
            val answerList = if(isGameDetail) gameViewModel.deleteAnswerById(answer.id!!, answer.gameRecordGameDate!!) else gallViewModel.deleteAnswer(answer.id!!, answer.galleryId!!)
            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_LONG).show()
            setAnswerList(answerList!!)
        }
    }
}