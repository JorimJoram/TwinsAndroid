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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.BuildConfig
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.FragmentGalleryDetailBinding
import twins.fan.twinsandroid.fragment.main.answer.AnswerFragment
import twins.fan.twinsandroid.util.CacheClear
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
        gallId = arguments?.getLong("id")!!
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gallDetailCommentContainer.setOnClickListener(toComment)
    }

    override fun onResume() {
        super.onResume()
        getDetail()
        setComment()
    }
    private fun setComment(){
        lifecycleScope.launch {
            val cnt = gallViewModel.getAnswerCntByGallId(gallId)
            binding.gallDetailComment.text = "댓글 $cnt"
        }
    }

    private val toComment = OnClickListener {
        val bundle = Bundle()
        bundle.putLong("gallId", gallId)

        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, AnswerFragment().apply { arguments = bundle })
        transaction.addToBackStack("GALL_ANSWER")
        transaction.commitAllowingStateLoss()
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

            CacheClear(requireContext())

            if(accountViewModel.getAccountImage(data.account.username)!!.path.isNotBlank())
                Glide.with(requireContext())
                    .load(BuildConfig.myUrl+accountViewModel.getAccountImage(data.account.username)!!.path)
                    .into(binding.gallDetailAccountImage)

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