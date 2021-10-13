package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseFollowUser {
    class FollowUserResult : BaseApiResult()
    @Parcelize
    data class FollowUserResponse(
        val data: @RawValue Any?,
        val total:Int?,
        val url_prefix_avatar: String?
    ):Parcelable{
        fun getListData():ArrayList<FollowUserData>?{
            try {
                val gson=GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<FollowUserData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
    @Parcelize
    data class FollowUserData(
        val id:Int?,
        val user_id:Int?,
        val username:String?,
        val avatar_user:String?,
        var followed_flag: Int?
    ):Parcelable{
    }
}