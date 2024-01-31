package twins.fan.twinsandroid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.game.TotalDetailRecord
import twins.fan.twinsandroid.fragment.main.MainFragment

class BatterMainListAdapter(
    private val fragment:Fragment,
    private val batterDetailList:List<TotalDetailRecord>
):BaseAdapter() {
    override fun getCount(): Int = batterDetailList.size

    override fun getItem(p0: Int): Any = batterDetailList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(fragment.context).inflate(R.layout.main_batter_list_item, null)
        val record = batterDetailList[position]

        view.findViewById<TextView>(R.id.main_batter_item_name).text = record.name
        view.findViewById<TextView>(R.id.main_batter_item_record).text = "${record.game}경기 ${record.ab}타수 ${record.hit}안타 ${record.hr}홈런 ${record.rbi}타점\n타율: ${record.avg}, 출루율: ${record.obp} 장타율: ${record.slg} OPS: ${"%.${3}f".format(record.obp.toDouble() + record.slg.toDouble())}"

        return view
    }

}