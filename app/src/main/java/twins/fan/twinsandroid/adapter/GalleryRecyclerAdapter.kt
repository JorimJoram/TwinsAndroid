package twins.fan.twinsandroid.adapter

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import twins.fan.twinsandroid.R
import twins.fan.twinsandroid.data.gall.Gallery
import twins.fan.twinsandroid.databinding.FragmentGalleryDetailBinding
import twins.fan.twinsandroid.databinding.ItemViewBinding
import twins.fan.twinsandroid.fragment.main.gallery.GalleryDetailFragment

class GalleryRecyclerAdapter: PagingDataAdapter<Gallery, GalleryRecyclerAdapter.MyViewHolder>(
    diffCallBack
) {
    inner class MyViewHolder(
        private val binding: ItemViewBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Gallery){
            binding.recycleItemTitle.text = data.title
            binding.recycleItemContent.text = data.content
            val date = if(data.modifiedDate != null) data.modifiedDate+"(수정됨)" else data.createdDate
            binding.recycleItemDate.text = "${ date.split("T")[0].replace("-", ".") } ${ date.split("T")[1].split(".")[0] }"


            itemView.setOnClickListener{
                Log.i(TAG, "bind: $data")
                val context = itemView.context

                //TODO("어디로 이동할지는 여기서 결정된다! Detail로 이동")
                val bundle = Bundle()
                bundle.putLong("id", data.id)

                val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.main_frameLayout, GalleryDetailFragment().apply { arguments = bundle })
                transaction.addToBackStack("GALLERY_DETAIL")
                transaction.commitAllowingStateLoss()
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