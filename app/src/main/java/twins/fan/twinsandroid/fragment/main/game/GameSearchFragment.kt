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
import twins.fan.twinsandroid.databinding.FragmentGameSearchBinding
import twins.fan.twinsandroid.viewmodel.GameViewModel
import java.time.LocalDateTime

class GameSearchFragment : Fragment() {
    private lateinit var binding:FragmentGameSearchBinding
    private val gameViewModel = GameViewModel()

    private val year = LocalDateTime.now().toString().split("T")[0].split("-")[0]
    private val month = LocalDateTime.now().toString().split("T")[0].split("-")[1]
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_game_search, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getNowDate()
        getGameList()
    }

    private fun getNowDate(){
        binding.gameSearchDate.text = year + "." + month
    }

    private fun getGameList(){
        val listView = binding.gameSearchGameList

        lifecycleScope.launch {
            //val gameList = gameViewModel.getGameListByMonth("${year.substring(2,4)}$month")
            val gameList = gameViewModel.getGameListByMonth("2308")
            val myAdapter = GameListAdapter(requireContext(), gameList)
            listView.adapter = myAdapter
        }
    }
}