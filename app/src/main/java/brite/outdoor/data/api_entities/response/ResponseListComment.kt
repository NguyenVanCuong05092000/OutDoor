package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import android.util.Log
import brite.outdoor.entity.EntityCommentReply
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseListComment {

    class ListCommentResult:BaseApiResult()

    @Parcelize
    data class ListCommentResponse(
        val url_prefix_avatar:String?,
        val url_prefix_comment: String?,
        val total:Int?,
        val data: @RawValue Any?
    ): Parcelable {
        fun getListCommentData():ArrayList<ListCommentData>?{
            try {
                val gson= GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<ListCommentData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }

    data class ListCommentData(
        val id:Int,
        val post_id:Int?,
        val thread_id:String?,
        val parent_id:String?,
        val comment:String?,
        val created_id: Int = 0,
        val reply_id: Int = 0,
        val modified_time:String?,
        val user_name_created: String?,
        val avatar_user: String?,
        val image: String?,
        var total_children: Int = 0,
        val post_comments_children:ArrayList<ListContentReplyData>?,
        var isSending: Boolean = false
    )

    data class ListContentReplyData(
        val id:Int,
        val post_id:Int?,
        val thread_id:String?,
        val parent_id:String?,
        val created_id: Int = 0,
        val reply_id: Int = 0,
        val comment:String?,
        val user_name_created : String?,
        val avatar_user: String?,
        val image: String?,
        val modified_time:String?,
        val user_name_reply:String?,
        var isSending: Boolean = false
    )
}