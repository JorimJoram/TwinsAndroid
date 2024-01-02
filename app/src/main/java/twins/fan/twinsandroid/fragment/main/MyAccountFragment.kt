package twins.fan.twinsandroid.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.FragmentMyAccountBinding

class MyAccountFragment : Fragment() {
    private lateinit var binding:FragmentMyAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainAccountTestButton.setOnClickListener(testListener)
    }

    private val testListener = OnClickListener{
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val myAccountFragment = GalleryFragment()
        transaction!!.replace(R.id.main_frameLayout, myAccountFragment)
        transaction.addToBackStack("account")
        transaction.commitAllowingStateLoss()
    }

    override fun onResume() {
        super.onResume()
        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomBar?.menu?.findItem(R.id.menu_account)?.isChecked = true
    }
}