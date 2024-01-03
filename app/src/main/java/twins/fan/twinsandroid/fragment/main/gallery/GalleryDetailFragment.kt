package twins.fan.twinsandroid.fragment.main.gallery

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.FragmentGalleryDetailBinding
import twins.fan.twinsandroid.viewmodel.GallViewModel

class GalleryDetailFragment : Fragment() {
    private lateinit var binding: FragmentGalleryDetailBinding
    private var gallId: Long = -1L
    private val gallViewModel = GallViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        gallId = arguments?.getLong("id")!!
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery_detail, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getDetail()
    }

    private fun getDetail(){
        val loadingAnimation = binding.gallDetailLottieView
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

        lifecycleScope.launch {
            val data = gallViewModel.getById(gallId)!!
            Log.i(TAG, "getDetail: $data")
            if(data.id == -1L){
                Log.i(TAG, "getDetail: -1 dd")
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().remove(GalleryDetailFragment()).commit()
                fragmentManager.popBackStack()
            }
            val date = if(data.modifiedDate != null) data.modifiedDate+"(수정됨)" else data.createdDate
            binding.gallDetailAccountUsername.text = data.account.username
            binding.gallDetailDate.text = "${ date.split("T")[0].replace("-", ".") } ${ date.split("T")[1].split(".")[0] }"
            binding.gallDetailTitle.text = data.title
            binding.gallDetailContent.text = data.content

            loadingAnimation.cancelAnimation()
            loadingAnimation.visibility = View.GONE
        }

    }
}