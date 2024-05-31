package twins.fan.twinsandroid.fragment.main.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.GameListAdapter
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.FragmentMyGameListBinding
import twins.fan.twinsandroid.viewmodel.GameViewModel

class MyGameListFragment : Fragment() {
    private lateinit var binding:FragmentMyGameListBinding

    private val userInfo = AuthenticationInfo.getInstance()
    private val gameViewModel = GameViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_game_list, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.gameMyGameHead.text = "직관 리스트" //TODO("진짜 제목 잘 지어줄 멋쟁이 없나 ㅠㅠ")
        setUserVisit()
    }

    private fun setUserVisit(){
        lifecycleScope.launch {
            val gameList = gameViewModel.getMyGameRecord(userInfo.username!!)
            val sortGameList = gameList!!.sortedBy { it.gameDate }
            val visitDateList = mutableListOf<String>()

            val gameListView = binding.gameMyGameGameList
            sortGameList.forEach { visitDateList.add(it.gameDate) }

            val adapter = GameListAdapter(MyGameListFragment(), requireContext(), sortGameList, visitDateList)
            gameListView.adapter = adapter
            adapter.setOnItemClickListener(gameListView)
        }
    }
}