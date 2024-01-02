package twins.fan.twinsandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.databinding.ActivityMainBinding
import twins.fan.twinsandroid.fragment.main.MainFragment
import twins.fan.twinsandroid.fragment.main.MyAccountFragment
import twins.fan.twinsandroid.fragment.main.GalleryFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var fragmentTransaction:FragmentTransaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainBinding.root)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frameLayout, MainFragment())
                .addToBackStack("MAIN")
                .commit()
        }

        mainBinding.mainBottomNav.menu.findItem(R.id.menu_main).isChecked = true

        mainBinding.mainBottomNav.setOnItemSelectedListener{item ->
            fragmentTransaction = supportFragmentManager.beginTransaction()

            when(item.itemId){
                R.id.menu_main -> {
                    fragmentTransaction.replace(R.id.main_frameLayout, MainFragment())
                        .addToBackStack("MAIN")
                        .commitAllowingStateLoss()
                }
                R.id.menu_account -> {
                    fragmentTransaction.replace(R.id.main_frameLayout, MyAccountFragment())
                        .addToBackStack("ACCOUNT")
                        .commitAllowingStateLoss()
                }
                R.id.menu_gallery -> {
                    fragmentTransaction.replace(R.id.main_frameLayout, GalleryFragment())
                        .addToBackStack("TEST")
                        .commitAllowingStateLoss()
                }

            }
            return@setOnItemSelectedListener true
        }
    }
}