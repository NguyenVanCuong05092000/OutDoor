package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import android.util.Log
import brite.outdoor.entity.EntityCommentReply
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseListCommentLevel2 {

    class ListCommentLevel2Result:BaseApiResult()

    @Parcelize
    data class ListCommentLevel2Response(
        val url_prefix_avatar:String?,
        val url_prefix_comment: String?,
        val total:Int?,
        val data: @RawValue Any?
    ): Parcelable {
        fun getListCommentLevel2Data():ArrayList<ListCommentLevel2Data>?{
            try {
                val gson= GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<ListCommentLevel2Data>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }

    data class ListCommentLevel2Data(
        val id:Int,
        val post_id:Int?,
        val thread_id:String?,
        val parent_id:String?,
        val created_id:Int = 0,
        val reply_id:Int = 0,
        val comment:String?,
        val modified_time:String?,
        val user_name_created: String?,
        val avatar_user: String?,
        val image: String?,
        val user_name_reply: String?
    )

}