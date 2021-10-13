package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponsePostComment {
    class PostCommentResult : BaseApiResult()
    @Parcelize
    data class ListCommentResponse(
        val id:Int,
        val post_id:Int?,
        val thread_id:String?,
        val parent_id:String?,
        val created_id: Int = 0,
        val reply_id: Int = 0,
        val comment:String?,
        val modified_time:String?,
        val user_name_created: String?,
        val avatar_user: String?,
        val user_name_reply: String?,
        val like_count: Int?,
        val url_prefix_comment: String?,
        val image: String?,
        val share_count: Int?,
        val comment_count: Int?,
        val total_children: Int = 0,
        val post_comments_children:@RawValue ArrayList<ResponseListComment.ListContentReplyData>?
    ): Parcelable {
        fun getListCommentReply():ArrayList<ResponseListComment.ListContentReplyData>?{
            try {
                val gson= GsonBuilder().create()
                val json=gson.toJson(post_comments_children)
                return gson.fromJson(json,object : TypeToken<ArrayList<ResponseListComment.ListContentReplyData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }

}