package twins.fan.twinsandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.ActivityAccountBinding
import twins.fan.twinsandroid.fragment.account.AccountFragmentPassword
import twins.fan.twinsandroid.fragment.account.AccountFragmentUsername

class AccountActivity : AppCompatActivity() {
    private lateinit var accountBinding: ActivityAccountBinding
    private lateinit var fragmentManager:FragmentManager

    private lateinit var transaction:FragmentTransaction

    private val usernameFragment = AccountFragmentUsername()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountBinding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(accountBinding.root)

        fragmentManager = supportFragmentManager
        transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.account_frameLayout, usernameFragment).commitAllowingStateLoss()

        this.onBackPressedDispatcher.addCallback(this, callBack)
    }

    private val callBack = object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            val builder = AlertDialog.Builder(this@AccountActivity)
            builder.setTitle("회원가입 종료").setMessage("지금까지 작성하신 내용이 모두 삭제됩니다.\n계속 진행하시겠습니까?")
            builder.setPositiveButton("예"){
                dialog, which -> finish()
            }
            builder.setNegativeButton("아니오"){
                dialog, which -> dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }
}