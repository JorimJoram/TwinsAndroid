package twins.fan.twinsandroid.fragment.main.gallery

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.FragmentGalleryDetailBinding
import twins.fan.twinsandroid.viewmodel.AccountViewModel
import twins.fan.twinsandroid.viewmodel.GallViewModel

class GalleryDetailFragment : Fragment() {
    private lateinit var binding: FragmentGalleryDetailBinding
    private var gallId: Long = -1L
    private val gallViewModel = GallViewModel()
    private val accountViewModel = AccountViewModel()
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
                backToList()
            }
            val date = if(data.modifiedDate != null) data.modifiedDate+"(수정됨)" else data.createdDate
            binding.gallDetailAccountUsername.text = data.account.username
            binding.gallDetailDate.text = "${ date.split("T")[0].replace("-", ".") } ${ date.split("T")[1].split(".")[0] }"
            binding.gallDetailTitle.text = data.title
            binding.gallDetailContent.text = data.content

            Glide.with(requireContext())
                .load("http://49.173.81.98:8080"+accountViewModel.getAccountImage(data.account.username)!!.path)
                .into(binding.gallDetailAccountImage) //TODO("첫술에 배부를 리가...")

            if(AuthenticationInfo.getInstance().username == data.account.username){
                val delete = binding.gallDetailDelete
                delete.visibility = View.VISIBLE
                delete.setOnClickListener(deleteGallery)
            }

            loadingAnimation.cancelAnimation()
            loadingAnimation.visibility = View.GONE
        }
    }

    private val deleteGallery = OnClickListener{
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("게시글을 삭제하시겠습니까?").setMessage("작성하셨던 게시글이 삭제됩니다.")
        builder.setPositiveButton("예"){
            _, _ -> deleteProcess()
        }
        builder.setNegativeButton("아니오"){
            dialog, _ -> dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun backToList(){
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().remove(GalleryDetailFragment()).commit()
        fragmentManager.popBackStack()
    }

    private fun deleteProcess(){
        lifecycleScope.launch {
            gallViewModel.deleteById(gallId)
            Toast.makeText(requireContext(), "게시글을 삭제하였습니다.", Toast.LENGTH_LONG).show()

            backToList()
        }
    }
}