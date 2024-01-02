package twins.fan.twinsandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.databinding.ItemViewBinding

class GalleryRecyclerAdapter: PagingDataAdapter<Gallery, GalleryRecyclerAdapter.MyViewHolder>(
    diffCallBack
) {
    inner class MyViewHolder(
        private val binding: ItemViewBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Gallery){
            binding.recycleItemTitle.text = data.title
            binding.recycleItemContent.text = data.content

            itemView.setOnClickListener{
                val context = itemView.context
                //TODO("어디로 이동할지는 여기서 결정된다! Detail로 이동")
            }
        }
    }

    companion object{
        val diffCallBack = object: DiffUtil.ItemCallback<Gallery>(){
            override fun areItemsTheSame(oldItem: Gallery, newItem: Gallery): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Gallery, newItem: Gallery): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context))
        return MyViewHolder(binding)
    }
}