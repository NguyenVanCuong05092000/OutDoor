package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseListFollower {
    class ListFollowerResult : BaseApiResult()
    @Parcelize
    data class ListFollowerResponse(
        val data: @RawValue Any?,
        val total:Int?,
        val url_prefix_avatar: String?
    ):Parcelable{
        fun getListData():ArrayList<ListFollowerData>?{
            try {
                val gson=GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<ListFollowerData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
    @Parcelize
    data class ListFollowerData(
        val id:Int?,
        val user_id:Int?,
        val username:String?,
        val avatar_user:String?
    ):Parcelable{
    }
}