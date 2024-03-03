package twins.fan.twinsandroid.fragment.main.game

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.GameListAdapter
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.FragmentGameSearchBinding
import twins.fan.twinsandroid.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class GameSearchFragment : Fragment() {
    private lateinit var binding:FragmentGameSearchBinding
    private val gameViewModel = GameViewModel()

    private var year = LocalDateTime.now().toString().split("T")[0].split("-")[0]
    private var month = LocalDateTime.now().toString().split("T")[0].split("-")[1]
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

        binding.gameSearchDate.setOnClickListener(showDatePicker)
        binding.gameSearchNextMonth.setOnClickListener(setNextMonth)
        binding.gameSearchPrevMonth.setOnClickListener(setPrevMonth)
    }

    private val setNextMonth = OnClickListener {
        if(month.toInt() + 1 > 12) {
            year = (year.toInt()+1).toString()
            month = "01"
        }
        else{
            month = String.format("%02d", (month.toInt() + 1))
        }
        binding.gameSearchDate.text = year + "." + month
        getGameList()
    }

    private val setPrevMonth = OnClickListener {
        if(month.toInt() - 1 < 1){
            month = "12"
            year = (year.toInt()-1).toString()
        }else{
            month = String.format("%02d", month.toInt() - 1)
        }
        binding.gameSearchDate.text = year + "." + month
        getGameList()
    }

    private val showDatePicker = OnClickListener{
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, _ ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, 1)
                val dateFormat = SimpleDateFormat("yyyy.MM", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                binding.gameSearchDate.text = formattedDate

                year = selectedYear.toString()
                month = String.format("%02d", selectedMonth+1)

                getGameList()
            }, year.toInt(), month.toInt(), 1
        )

        datePickerDialog.datePicker.init(year.toInt(), month.toInt()-1, 1, null)
        datePickerDialog.show()
    }

    private fun getNowDate(){
        binding.gameSearchDate.text = year + "." + month
    }

    private fun getGameList(){
        val listView = binding.gameSearchGameList

        val loadingAnimation = binding.gameSearchLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch {
            val gameList = gameViewModel.getGameListByMonth("${year.substring(2,4)}$month")!!
            val userVisitList = gameViewModel.getUserVisit(AuthenticationInfo.getInstance().username!!, "${year.substring(2,4)}$month")!!
            val visitDateList = mutableListOf<String>()

            userVisitList.forEach { visitDateList.add(it.visitDate) }

            val myAdapter = GameListAdapter(GameSearchFragment(),requireContext(), gameList, visitDateList)
            listView.adapter = myAdapter
            myAdapter.setOnItemClickListener(listView)

            loadingAnimation.cancelAnimation()
            loadingAnimation.visibility = View.GONE
        }
    }
}