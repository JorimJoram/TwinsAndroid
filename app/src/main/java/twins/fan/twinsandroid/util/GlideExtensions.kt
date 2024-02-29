package twins.fan.twinsandroid.util

import android.content.Context
import com.bumptech.glide.Glide

fun CacheClear(context: Context){
    Glide.get(context).clearMemory()
    Thread{
        Glide.get(context).clearDiskCache()
    }.start()
}