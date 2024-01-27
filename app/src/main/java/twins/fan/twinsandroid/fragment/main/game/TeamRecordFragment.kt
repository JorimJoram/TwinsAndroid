package twins.fan.twinsandroid.fragment.main.game

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.BatterMainListAdapter
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.game.TotalDetailRecord
import twins.fan.twinsandroid.databinding.FragmentTeamRecordBinding
import twins.fan.twinsandroid.viewmodel.GameViewModel

class TeamRecordFragment : Fragment() {
    private lateinit var binding:FragmentTeamRecordBinding
    private lateinit var userInfo:AuthenticationInfo
    private val gameViewModel = GameViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userInfo = AuthenticationInfo.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_team_record, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        setTitle()
        getMyAllData()
    }
    private fun setTitle(){
        val head = "${userInfo.username}님 \n 직관정보를 확인해보세요!"
        val title = SpannableString(head)

        val start = 0
        val end = start + userInfo.username!!.length
        title.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.twins_red)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.teamRecordHead.text = title
    }
    private fun getMyAllData(){
        val loadingAnimation = binding.teamRecordLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch {
            val batterTotalDataList = gameViewModel.getTotalDetailData(userInfo.username!!)
            val teamTotalData = gameViewModel.getTeamTotalData(userInfo.username!!)
            val teamRecord = gameViewModel.getUserGameResult(userInfo.username!!)

            setWinRateChart(teamRecord!!)
            setTeamTotalData(teamTotalData!!)
            setBatterDetailData(batterTotalDataList!!)

            loadingAnimation.cancelAnimation()
            loadingAnimation.visibility = View.GONE
        }
    }
    private fun setBatterDetailData(batterList: List<TotalDetailRecord>) {
        var sortedByGamesAndOPS = batterList.sortedWith(
            compareByDescending<TotalDetailRecord> { it.game }
                .thenByDescending{it.slg.toDouble() + it.obp.toDouble()}
        )
        Log.d(TAG, "setBatterDetailData: $sortedByGamesAndOPS")
        binding.teamRecordBatterDetail.adapter = BatterMainListAdapter(this@TeamRecordFragment, sortedByGamesAndOPS)
        val params = binding.teamRecordBatterDetail.layoutParams
        params.height = sortedByGamesAndOPS.size * 150
        binding.teamRecordBatterDetail.layoutParams = params
    }
    private fun setTeamTotalData(teamData: TotalDetailRecord) {
        val teamChartDetailList = listOf(BarEntry(1f, teamData.avg.toFloat()), BarEntry(2f, teamData.obp.toFloat()), BarEntry(3f, teamData.slg.toFloat()), BarEntry(4f, teamData.slg.toFloat()+teamData.obp.toFloat()))
        val teamChartLabel = listOf("","타율","출루율","장타율","OPS")
        val barDataSet = BarDataSet(teamChartDetailList, "DataSet")
        barDataSet.color = resources.getColor(R.color.twins_red, null)
        val dataSet = BarData(barDataSet)
        dataSet.barWidth = 0.2f
        dataSet.setValueTextSize(15f)
        binding.teamRecordTeamDetailChart.run {
            data = dataSet

            setPinchZoom(false)
            setMaxVisibleValueCount(5)
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setTouchEnabled(false)
            animateY(1200)
            description.isEnabled=false
            legend.isEnabled = false
            axisRight.isEnabled = false

            axisLeft.run{
                axisMaximum = 1.0f
                axisMinimum = 0.0f
                granularity = 0.250f
                textSize = 14f
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.3f", value) // 소수점 한 자리까지 표시
                    }
                }
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textSize = 11f
                valueFormatter = IndexAxisValueFormatter(teamChartLabel)
                setDrawAxisLine(true)
                setDrawGridLines(false)
            }
        }
        binding.teamRecordTeamDetail.text = "${teamData.ab}타수 ${teamData.hit}안타 ${teamData.hr}홈런 ${teamData.rbi}타점 ${teamData.bb}사사구\n" +
                "${teamData.avg} / ${teamData.obp} / ${teamData.slg} / ${"%.3f".format(teamData.slg.toFloat()+teamData.obp.toFloat())}"
    }
    private fun setWinRateChart(visitGameList:List<String>){
        val resultList = mutableListOf(0.0,0.0,0.0)
        visitGameList.forEach {
            when(it){
                "승" -> resultList[0] += 1.0
                "패" -> resultList[1] += 1.0
                "무" -> resultList[2] += 1.0
            }
        }
        val dividedList = resultList.map { PieEntry((it/visitGameList.size).toFloat() ) }
        val dataSet = PieDataSet(dividedList, "")
        dataSet.colors = listOf(resources.getColor(R.color.twins_red, null), resources.getColor(R.color.white, null), resources.getColor(R.color.white, null),)
        dataSet.setDrawValues(false)

        binding.teamRecordWinRateChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = true
            holeRadius = 75f
            setTouchEnabled(false)
            animateY(1200, Easing.EaseInOutCubic)

            animate()
        }

        binding.teamRecordWinRate.text = "%.2f".format(resultList[0]/resultList.sum() * 100) + "%"
        binding.teamRecordWinRecord.text = "${resultList.sum().toInt()}경기\t${resultList[0].toInt()}승\t${resultList[2].toInt()}무\t${resultList[1].toInt()}패"
        binding.teamRecordWinRecordRecommend.text = recommendText(resultList[0]/resultList.sum() * 100)
    }
    private fun recommendText(winRate:Double): String {
        return when{
            winRate > 60.0 -> "승리요정 그 자체 ㄷㄷ"
            winRate in 40.0 .. 60.0 -> "자주 오셔야 합니다 ㅠㅠ"
            else -> "이젠 이길 때 됐다!"
        }
    }
}