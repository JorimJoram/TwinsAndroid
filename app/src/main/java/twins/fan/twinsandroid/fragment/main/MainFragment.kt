package twins.fan.twinsandroid.fragment.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.FragmentMainBinding
import twins.fan.twinsandroid.fragment.main.game.GameSearchFragment
import twins.fan.twinsandroid.viewmodel.LoginViewModel


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var isClicked = false
    private val loginViewModel=LoginViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.testButton.setOnClickListener(testListener)
    }

    private val testListener = OnClickListener{
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val gameSearchFragment = GameSearchFragment()
        transaction!!.replace(R.id.main_frameLayout, gameSearchFragment)
        transaction.addToBackStack("GAME_SEARCH")
        transaction.commitAllowingStateLoss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isClicked = false
        requireActivity().onBackPressedDispatcher.addCallback(this, backButtonEvent)

    }

    private val backButtonEvent = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            if(isClicked){
                requireActivity().finishAffinity()
            }
            isClicked = true
            Toast.makeText(requireContext(), "종료하시려면 한 번 더 눌러주세요", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 진짜 최후의 순간에 나오는 부분
     *  == 여기서 로그아웃을 시키는거
     */
    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            loginViewModel.logoutProcess() //TODO("로그아웃은 여기서 진행해야합니다")
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomBar?.menu?.findItem(R.id.menu_main)?.isChecked = true
    }
}