package twins.fan.twinsandroid.util

import android.app.Activity
import com.google.android.material.bottomnavigation.BottomNavigationView
import twins.fan.twinsandroid.R

fun setMainClicked(activity:Activity){
    val bottomBar = activity.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
    bottomBar?.menu?.findItem(R.id.menu_main)?.isChecked = true
}