package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlin.Exception

object ResponseListBookMark {
    class ListBookMarkResult():BaseApiResult()

    @Parcelize
    data class ListBookMarkResponse(
           val total:Int?,
           val data: @RawValue Any?

    ):Parcelable{
        fun getListData() : ArrayList<ListBookMarkData>?{
            try {
                val gson= GsonBuilder().create()
                val json = gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<ListBookMarkData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null

        }
    }

    @Parcelize
    data class ListBookMarkData(
            val id:Int,
            var title:String,
            val content:String?,
            val location_id:Int?,
            val hashtag_id:Int?,
            val utensil_id:Int?,
            val like_count:Int?,
            val comment_count:Int?,
            val cIndex:Int?,
            val is_sticky:Int?,
            val share_count:Int?,
            val created_id:Int,
            val modified_time:String?,
            val created_time: String?,
            val id_bookmark:Int?,
            var state_follow: Int?,
            val user_name_created:String?,
            val avatar_user:String?,
            val name_locations: String?,
            val name_utensils: String?,
            val name_hashtags: String?,
            var url_prefix:String?,
            val url_prefix_avatar:String?,
            var post_shares:Boolean?
    ):Parcelable{

        @Expose
        var isShowLoading = false

        fun getListContent() : ArrayList<ResponseListPostUser.ResponseContent>?{
            try {
                val gson= GsonBuilder().create()
                return gson.fromJson(content,object : TypeToken<ArrayList<ResponseListPostUser.ResponseContent>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null

        }
    }
}