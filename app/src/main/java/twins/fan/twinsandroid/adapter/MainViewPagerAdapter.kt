package twins.fan.twinsandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import twins.fan.twinsandroid.R

class MainViewPagerAdapter(
    private val viewList:List<View>
):RecyclerView.Adapter<MainViewPagerAdapter.ViewHolder>() {
    inner class ViewHolder(val view:View): RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int = position%viewList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(viewList[viewType])

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

}