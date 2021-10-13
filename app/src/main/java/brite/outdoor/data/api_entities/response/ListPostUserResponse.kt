package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import android.util.Log
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ListPostUserResponse(
       val data:@RawValue Any?,
       val total:Int?

): Parcelable {
    companion object {
        const val STATE_USER = 1
        const val STATE_UNFOLLOW = 2
        const val STATE_FOLLOW = 3
        const val STATE_UNLIKE= false
        const val STATE_LIKE = true
    }

    fun getListData() : ArrayList<ListPostUserData>?{
        try {
            val gson= GsonBuilder().create()
            val json=gson.toJson(data)
            return gson.fromJson(json,object : TypeToken<ArrayList<ListPostUserData>>(){}.type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
@Parcelize
data class ListPostUserData(
    val id:Int,
    var title:String,
    val content:String?,
    val location_id:Int?,
    val name_locations: String?,
    val name_utensils: String?,
    val name_hashtags: String?,
    val hashtag_id:Int?,
    val utensil_id:Int?,
    var like_count:Int?,
    var comment_count:Int?,
    var share_count:Int?,
    val is_deleted:Boolean,
    val created_id:Int,
    val created_time:String?,
    val modified_time:String?,
    val url_prefix:String?,
    val avatar_user:String?,
    var post_likes: Boolean?,
    var state_follow:Int?,
    val url_prefix_avatar:String?,
    val user_name_created:String?,
    val cIndex:Int=0,
    val type:Int?=1,
    var post_shares:Boolean?=null,

):Parcelable{

    @Expose
    var isShowLoading = false

//    @Transient
//    var simpleExoPlayer : SimpleExoPlayer? = null

    fun getListContent():ArrayList<ResponseListPostUser.ResponseContent>?{
        try {
            val gson= GsonBuilder().create()
            return gson.fromJson(content,object : TypeToken<ArrayList<ResponseListPostUser.ResponseContent>>(){}.type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
