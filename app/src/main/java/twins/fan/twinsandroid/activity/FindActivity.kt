package twins.fan.twinsandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.ActivityFindBinding
import twins.fan.twinsandroid.fragment.account.find.FindMainFragment

class FindActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.find_frameLayout, FindMainFragment()).commitAllowingStateLoss()
    }
}