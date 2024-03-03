package twins.fan.twinsandroid.fragment.main.my

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import twins.fan.twinsandroid.BuildConfig
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.account.Account
import twins.fan.twinsandroid.data.account.AccountImage
import twins.fan.twinsandroid.data.account.AuthenticationInfo
import twins.fan.twinsandroid.databinding.FragmentChangeInfoBinding
import twins.fan.twinsandroid.viewmodel.AccountViewModel
import java.io.File
import java.net.SocketTimeoutException

class ChangeInfoFragment : Fragment() {
    private lateinit var binding:FragmentChangeInfoBinding

    private lateinit var account:Account
    private lateinit var accountImage: AccountImage
    private lateinit var image: MultipartBody.Part

    private val userInfo = AuthenticationInfo.getInstance()
    private val accountViewModel = AccountViewModel()
    private var code = "0"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountImage = AccountImage.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_info, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            account = accountViewModel.getAccountByUsername(userInfo.username!!)!!
            binding.accountChangeUsername.text = account.username
            binding.accountChangeTelInput.setText(account.tel)
        }

        val accountImage = AccountImage.getInstance()
        if(accountImage.path.isNotBlank())
            Glide.with(requireContext()).load(BuildConfig.myUrl+accountImage.path).into(binding.accountChangeAccountImage)

        binding.accountChangeSendCodeButton.setOnClickListener(sendListener)
        binding.accountChangeCodeCheckButton.setOnClickListener(checkCode)

        binding.accountChangePasswordCheckButton.setOnClickListener(checkPassword)

        binding.accountChangeImageChangeButton.setOnClickListener(imageChange)

        binding.accountChangeSaveButton.setOnClickListener(saveEdit)
    }

    /**
     * 여기서부터 인증번호 재사용이 필요한 코드입니다.
     */
    private val sendListener = OnClickListener{
        showAlert()
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("전화번호 확인").setMessage("${binding.accountChangeTelInput.text}으로 인증번호를 전송합니다.\n전송한 번호는 3분 뒤에 다시 전송할 수 있습니다.")
        builder.setPositiveButton("예"){
                dialog, which -> sendCode()
        }
        builder.setNegativeButton("아니오"){
                dialog, which -> dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun sendCode(){
        lifecycleScope.launch {
            try{
                code = accountViewModel.sendCode(binding.accountChangeTelInput.text.toString())!!
                when(code){
                    "-1" -> showMsg(-1)
                    "-2" -> showMsg(-4)
                    else -> {
                        countDown()
                        showMsg(2)
                        binding.accountChangeCodeCheckButton.isEnabled = true
                        binding.accountChangeTelMsg.text = code//TODO("마찬가지로 다음에 지워주십쇼")
                    }
                }
            }catch (e: SocketTimeoutException){
                showMsg(-3)
            }catch (e:Exception){
                Log.e(TAG, "sendCode: ${e.message}")
                binding.accountChangeSendCodeButton.isEnabled = true
                showMsg(-100)
            }
        }
    }
    private fun countDown(){
        val time = 5L
        val timer = object: CountDownTimer(time * 60 * 1000, 1000){
            override fun onTick(remainTime: Long) {
                binding.accountChangeSendCodeButton.isEnabled = false
                val min = (remainTime / 1000) / 60
                val second = (remainTime/ 1000) % 60
                val formattedTime = "%02d:%02d".format(min, second)
                binding.accountChangeSendCodeButton.text = formattedTime
            }

            override fun onFinish() {
                binding.accountChangeSendCodeButton.text = "인증"
                binding.accountChangeSendCodeButton.isEnabled = true
            }
        }.start()
    }
    private fun showMsg(code:Int){
        val msg:String = when(code){
            1 -> "회원가입이 완료되었습니다."
            2 -> "인증번호를 발송했습니다."
            -1 -> "전화번호를 다시 확인해주세요"
            -2 -> "인증번호를 다시 확인해주세요"
            -3 -> "인터넷 연결 확인 후 다시 시도해주세요"
            -4 -> "이미 등록된 전화번호입니다"
            else -> "다시 시도해주세요"
        }
        if(code > 1){
            binding.accountChangeTelMsg.text = ""
            Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
        }else{
            val msgView = binding.accountChangeTelMsg
            msgView.text = msg
        }
    }

    /**
     * 전화번호 인증 코드 확인하는 부분입니다.
     */
    private val checkCode = OnClickListener {
        if(binding.accountChangeTelCodeInput.text.toString() != code)
            showMsg(-2)
        else{
            account.tel = binding.accountChangeTelInput.text.toString()
            binding.accountChangeSaveButton.isEnabled = true
            binding.accountChangeTelConfirmMsg.text = "변경 버튼을 눌러 프로필 변경을 완료하세요!"
        }
    }

    /**
     * 비밀번호 확인
     */
    private val checkPassword = OnClickListener{
        val password = binding.accountChangePasswordInput.text.toString()
        val confirmPassword = binding.accountChangePasswordConfirmInput.text.toString()
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*[!@#\$%^*+=-])(?=.*[0-9])(?!.*\\s).{8,15}\$")

        if(!passwordRegex.matches(password)){
            binding.accountChangePasswordMsg.text = "영문, 숫자, 특수문자를 포함한 8자 이상을 입력해주세요\n(스페이스 제외)"
        }

        if( password == confirmPassword){
            account.password = password
            binding.accountChangePasswordCheckButton.isEnabled = false
            binding.accountChangeSaveButton.isEnabled = true
            binding.accountChangePasswordMsg.text = "사용가능한 비밀번호입니다.\n변경 버튼을 눌러 프로필 변경을 완료하세요!"
        }else{
            binding.accountChangePasswordMsg.text = "비밀번호가 서로 일치하지 않습니다."
        }
    }

    /**
     * 이미지 변경
     */
    private val imageChange = OnClickListener {
        Toast.makeText(requireContext(), "Click", Toast.LENGTH_LONG).show()
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
        Log.d(TAG, "permission: $readPermission / ${PackageManager.PERMISSION_DENIED}")

        if(readPermission == PackageManager.PERMISSION_DENIED){
            Log.d(TAG, "permission in condition: $readPermission / ${PackageManager.PERMISSION_DENIED}")
            requireActivity().requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }else{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

            imageResult.launch(intent)
        }
    }

    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == Activity.RESULT_OK){
            val imageUri = result.data?.data ?: return@registerForActivityResult

            val file = File(absolutePath(imageUri, requireContext()))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            image = MultipartBody.Part.createFormData("profile", file.name, requestFile)

            binding.accountChangeAccountImage.setImageURI(imageUri)
            binding.accountChangeSaveButton.isEnabled = true
        }
    }

    private fun absolutePath(path:Uri?, context:Context): String {
        val proj:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)

        return result!!
    }

    /**
     * 최종 저장
     */
    private val saveEdit = OnClickListener {
        lifecycleScope.launch{
            val result = accountViewModel.updateAccount(account)!!
            val imageResult = accountViewModel.updateImage(account.username, image)!!
            if(result.id == -1L || imageResult.id == -1L)
                binding.accountChangeTelConfirmMsg.text = "다시 시도해주세요"
            else{
                accountImage.path = imageResult.path

                Toast.makeText(requireContext(), "회원정보가 정상적으로 변경되었습니다.", Toast.LENGTH_LONG).show()

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.remove(this@ChangeInfoFragment)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
}