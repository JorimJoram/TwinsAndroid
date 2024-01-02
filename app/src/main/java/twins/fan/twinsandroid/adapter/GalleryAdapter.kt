package twins.fan.twinsandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.gall.Gallery

class GalleryAdapter(
    private val context: Context,
    private val gallList: List<Gallery>
):BaseAdapter() {
    override fun getCount(): Int {
        return gallList.size
    }

    override fun getItem(position: Int): Any {
        return gallList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, converterView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_view, null)

        val gallTitle = view.findViewById<TextView>(R.id.recycle_item_title)
        val gallContent = view.findViewById<TextView>(R.id.recycle_item_content)

        val gall = gallList[position]

        gallTitle.text = gall.title
        gallContent.text = gall.content

        return view
    }

}