package twins.fan.twinsandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import twins.fan.twinsandroid.R

class MainViewPagerAdapter(
    private val viewList:List<String>
):RecyclerView.Adapter<MainViewPagerAdapter.ViewHolder>() {
    inner class ViewHolder(val view:View): RecyclerView.ViewHolder(view){
        val textView = view.findViewById<TextView>(R.id.recycle_item_content)
    }

    override fun getItemViewType(position: Int): Int = position%viewList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = viewList[position%viewList.size]
        holder.textView.text = item
    }

}