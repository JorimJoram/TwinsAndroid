package twins.fan.twinsandroid.fragment.main.gallery

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.data.gall.RequestGallery
import twins.fan.twinsandroid.databinding.FragmentGalleryCreateBinding
import twins.fan.twinsandroid.viewmodel.GallViewModel


class GalleryCreateFragment : Fragment() {
    private lateinit var binding: FragmentGalleryCreateBinding
    private val gallViewModel = GallViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery_create, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.gallFormSaveButton.setOnClickListener(saveGallery)
    }

    private val saveGallery = OnClickListener{
        val title = binding.gallFormTitle.text.toString()
        val content = binding.gallFormContent.text.toString()
        if(title.isBlank() || content.isBlank()){
            Toast.makeText(context, "제목과 글을 모두 작성해주세요", Toast.LENGTH_LONG).show()
        }else{
            createProcess(title, content)
        }
    }

    private fun createProcess(title:String, content:String){
        lifecycleScope.launch{
            Log.i(TAG, "createProcess: $title, $content, ${AuthenticationInfo.getInstance().username}")
            val response = gallViewModel.createGallery(RequestGallery(title, content, AuthenticationInfo.getInstance().username!!))!!
            if(response.id == -1L){
                Toast.makeText(context,"다시 시도해주세요", Toast.LENGTH_LONG).show()
            }else{
                val bundle = Bundle()
                bundle.putLong("id", response.id)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.remove(GalleryCreateFragment())
                transaction.replace(R.id.main_frameLayout, GalleryDetailFragment().apply { arguments = bundle })
                transaction.addToBackStack("GALLERY_DETAIL")
                transaction.commitAllowingStateLoss()
            }
        }
    }
}