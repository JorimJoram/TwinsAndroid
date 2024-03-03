package twins.fan.twinsandroid.fragment.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.BatterMainListAdapter
import twins.fan.twinsandroid.adapter.MainViewPagerAdapter
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.game.GameRecord
import twins.fan.twinsandroid.data.game.TotalDetailRecord
import twins.fan.twinsandroid.databinding.FragmentMainBinding
import twins.fan.twinsandroid.fragment.main.game.GameDetailFragment
import twins.fan.twinsandroid.fragment.main.game.GameSearchFragment
import twins.fan.twinsandroid.fragment.main.game.TeamRecordFragment
import twins.fan.twinsandroid.util.checkLogo
import twins.fan.twinsandroid.util.pitchResult
import twins.fan.twinsandroid.util.scoreLocate
import twins.fan.twinsandroid.util.setMainClicked
import twins.fan.twinsandroid.util.toFormattedDate
import twins.fan.twinsandroid.viewmodel.GallViewModel
import twins.fan.twinsandroid.viewmodel.GameViewModel
import twins.fan.twinsandroid.viewmodel.LoginViewModel

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val loginViewModel=LoginViewModel()
    private val gameViewModel = GameViewModel()
    private val gallViewModel = GallViewModel()

    private lateinit var recentGameRecord:GameRecord

    private val userInfo:AuthenticationInfo = AuthenticationInfo.getInstance()
    private var isBackButtonClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainWinRateContainer.setOnClickListener(goSearch)
        binding.mainGameToRecord.setOnClickListener(toGameDetail)
        Glide.with(requireContext())
            .load(R.raw.twins)
            .into(binding.mainWinRateTeamImage) //TODO("라이프사이클 확인하고 충분한 근거를 가지고 배치해주시기 바랍니다.222222")
    }
    override fun onResume() {
        super.onResume()

        isBackButtonClicked = false
        requireActivity().onBackPressedDispatcher.addCallback(this, backButtonEvent)

        setMainClicked(requireActivity())

        setHeadViewPager()

        setTeamBatterTotal()
        setTeamGameTotal()
        setBatterDetail()
        setGalleryList()

        getMyData()

        removeOpenedFragment()
    }
    private fun setHeadViewPager(){
        binding.mainViewPager.adapter = MainViewPagerAdapter(listOf("test1", "test2"))
        binding.mainViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    /**
     * 데이터 가져오기 성공
     */
    private fun setTeamBatterTotal(){
        lifecycleScope.launch {
            val teamTotalRecord = gameViewModel.getTeamTotalData(userInfo.username!!)!!
            binding.mainRecordAvg.text = teamTotalRecord.avg
            binding.mainRecordObp.text = teamTotalRecord.obp
            binding.mainRecordSlg.text = teamTotalRecord.slg
        }
    }
    private fun setTeamGameTotal(){
        lifecycleScope.launch {
            val userVisitResult = gameViewModel.getUserGameResult(userInfo.username!!)!!
            putGameResult(userVisitResult)
            Log.i(TAG, "userVisitResult: $userVisitResult")
        }
    }
    private fun setBatterDetail(){
        lifecycleScope.launch {
            val batterResult = gameViewModel.getTotalDetailData(userInfo.username!!)!!
            if(batterResult.size > 3) {
                val sortedByPoint = batterResult.sortedByDescending { it.point }.subList(0, 3)
                binding.mainRecordList.adapter =
                    BatterMainListAdapter(this@MainFragment, sortedByPoint)
            }
        }
    }
    private fun setGalleryList(){
        lifecycleScope.launch {
            val gallList = gallViewModel.getFewGallery(0, 3)
            Log.i(TAG, "GalleryList: $gallList")
        }
    }

    private fun removeOpenedFragment(){
        val fragmentManager = requireActivity().supportFragmentManager
        val backStackCount = fragmentManager.backStackEntryCount

        if(backStackCount > 2){
            val mainFragment = fragmentManager.getBackStackEntryAt(1) //어차피 메인 빼곤 다 닫으면 되는거 아닌가?
            fragmentManager.popBackStack(mainFragment.id, FragmentManager.POP_BACK_STACK_INCLUSIVE) //앞에 적힌 id값의 프레그먼트를 포함하여 후의 모든 프레그먼트 pop
        }
    }

    private fun getMyData(){
        val userInfo = AuthenticationInfo.getInstance()

        val loadingAnimation = binding.mainLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch{
            //batterDetailDataList = gameViewModel.getTotalDetailData(userInfo.username!!)!!
            recentGameRecord = gameViewModel.getRecentUserVisit(userInfo.username!!)!!


            //putGameResult()
            if(recentGameRecord.id != -1L) {
                putRecentGameRecord(recentGameRecord)
                binding.mainRecentGameRecord.gameListItemSwitch.visibility = View.VISIBLE
            }else
                binding.mainRecentGameRecord.gameListItemSwitch.visibility = View.GONE
        }

        binding.mainRecentGameRecord.gameListItemMatchContainer.setOnClickListener(goDetail)

        loadingAnimation.cancelAnimation()
        loadingAnimation.visibility = View.GONE
    }


    private fun putRecentGameRecord(recentGameRecord: GameRecord) {
        val lgScoreSplit = recentGameRecord.lgScore.split(",")
        val versusScoreSplit = recentGameRecord.versusScore.split(",")
        val score = scoreLocate(recentGameRecord.stadium == "잠실종합운동장", lgScoreSplit, versusScoreSplit).split("_")
        val logo = checkLogo(recentGameRecord.versusTeam, recentGameRecord.stadium == "잠실종합운동장")
        val pitchGameResult = pitchResult(recentGameRecord.winner, recentGameRecord.loser, score[0].toInt(), score[1].toInt())
        val dateString = "${recentGameRecord.gameDate.substring(0,recentGameRecord.gameDate.length-1).toFormattedDate() } ${recentGameRecord.startTime}~${recentGameRecord.endTime}"

        binding.mainRecentGameRecord.gameListItemDate.text = dateString
        binding.mainRecentGameRecord.gameListItemFinal.text = recentGameRecord.final.split("_").let {parts -> if (parts.size >= 2) "${parts[0]}\n${parts[1]}" else parts[0] }

        binding.mainRecentGameRecord.gameListItemSwitch.isChecked=true
        binding.mainRecentGameRecord.gameListItemSwitch.isEnabled=false

        binding.mainRecentGameRecord.gameListItemHomeScore.text = score[0]
        binding.mainRecentGameRecord.gameListItemHomeScore.typeface = if (score[0] > score[1]) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
        binding.mainRecentGameRecord.gameListItemVisitScore.text = score[1]
        binding.mainRecentGameRecord.gameListItemVisitScore.typeface = if (score[0] < score[1]) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT

        Glide.with(requireContext()).load(logo[0])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.mainRecentGameRecord.gameListItemHomeLogo)
        Glide.with(requireContext()).load(logo[1])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.mainRecentGameRecord.gameListItemVisitLogo)

        binding.mainRecentGameRecord.gameListItemHomePitchResult.text = pitchGameResult[0][0].toString()
        binding.mainRecentGameRecord.gameListItemHomePitchResult.setTextColor(pitchGameResult[0][1] as Int)
        binding.mainRecentGameRecord.gameListItemVisitPitchResult.text = pitchGameResult[1][0].toString()
        binding.mainRecentGameRecord.gameListItemVisitPitchResult.setTextColor(pitchGameResult[1][1] as Int)
    }
    private fun putGameResult(teamGameResult:List<String>){
        val resultList = mutableListOf(0,0,0)
        teamGameResult.forEach {
            when(it){
                "승" -> resultList[0] += 1
                "패" -> resultList[1] += 1
                "무" -> resultList[2] += 1
            }
        }
        binding.mainRecordWin.text = resultList[0].toString()
        binding.mainRecordLose.text = resultList[1].toString()
        binding.mainRecordDraw.text = resultList[2].toString()
        binding.mainRecordWinRate.text = "${"%.2f".format((resultList[0].toDouble()/teamGameResult.size.toDouble()*100))}%"
        binding.mainRecordGameAmount.text = "${teamGameResult.size} G"
    }


    private val toGameDetail = OnClickListener {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, TeamRecordFragment())
        transaction.addToBackStack("GAME_RECORD")
        transaction.commitAllowingStateLoss()
    }
    private val goDetail = OnClickListener {
        val bundle = Bundle()
        bundle.putString("gameDate", recentGameRecord.gameDate)

        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, GameDetailFragment().apply { arguments = bundle })
        transaction.addToBackStack("GAME_DETAIL")
        transaction.commitAllowingStateLoss()
    }
    private val goSearch = OnClickListener{
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val gameSearchFragment = GameSearchFragment()
        transaction!!.replace(R.id.main_frameLayout, gameSearchFragment)
        transaction.addToBackStack("GAME_SEARCH")
        transaction.commitAllowingStateLoss()
    }

    private val backButtonEvent = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            if(isBackButtonClicked){
                requireActivity().finishAffinity()
                lifecycleScope.launch { loginViewModel.logoutProcess() }
            }
            isBackButtonClicked = true
            Toast.makeText(requireContext(), "종료하시려면 한 번 더 눌러주세요", Toast.LENGTH_LONG).show()
        }
    }
}