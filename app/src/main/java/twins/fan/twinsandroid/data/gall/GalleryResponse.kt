package twins.fan.twinsandroid.data.gall

import com.google.gson.annotations.SerializedName

data class GalleryResponse(
    @SerializedName("content") val content: List<Gallery>,
    @SerializedName("pageable") val pageable: Pageable,
    @SerializedName("last") val last:Boolean,
    @SerializedName("totalPages") val totalPages:Int,
    @SerializedName("size") val size:Int,
    @SerializedName("number") val number:Int,
    @SerializedName("sort") val sort: Sort,
    @SerializedName("first") val first:Boolean,
    @SerializedName("numberOrElements") val numberOrElements: Int,
    @SerializedName("empty") val empty: Boolean
)