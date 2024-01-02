package twins.fan.twinsandroid.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.adapter.GalleryRecyclerAdapter
import twins.fan.twinsandroid.databinding.FragmentGalleryBinding
import twins.fan.twinsandroid.viewmodel.GallViewModel

class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private val gallViewModel = GallViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        getAndPutData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val bottomBar = activity?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomBar?.menu?.findItem(R.id.menu_gallery)?.isChecked = true
    }

    private fun getAndPutData(){
        val adapter = GalleryRecyclerAdapter()
        binding.gallListRecyclerView.adapter = adapter
        binding.gallListRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        lifecycleScope.launch {
            gallViewModel.setPaging().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}