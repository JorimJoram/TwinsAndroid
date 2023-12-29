package twins.fan.twinsandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
    }
}