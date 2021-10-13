package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseSearchUser {
    class SearchUserResult : BaseApiResult()
    @Parcelize
    data class SearchUserResponse(
        val data: @RawValue Any?,
        val total:Int?
    ):Parcelable{
        fun getListData():ArrayList<SearchUserData>?{
            try {
                val gson=GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<SearchUserData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
    @Parcelize
    data class SearchUserData(
        val id:Int?,
        val email:String?,
        val name:String?,
        val status:Boolean?,
        val gender:String?,
        val birthday:String?,
        val device_type:Int?,
        val current_device_type:Int?,
        val register_device_uuid:String?,
        val current_device_uuid:String?,
        val version_data:Int?,
        val avatar:String?,
        val token:String?,
        val firebase_token:String?,
        val is_deleted:String?,
        val created_time:String?,
        val modified_time:String?,
        var followed_flag:String,
        val url_prefix_avatar:String?,
    ):Parcelable{
    }
}