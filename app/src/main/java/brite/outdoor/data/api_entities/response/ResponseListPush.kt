package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import android.util.Log
import brite.outdoor.entity.EntityCommentReply
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseListPush {

    class ListPushResult:BaseApiResult()

    @Parcelize
    data class ListPushResponse(
        val url_prefix_avatar:String?,
        val total:Int?,
        val data: @RawValue Any?
    ): Parcelable {
        fun getListPushData():ArrayList<ListPushData>?{
            try {
                val gson= GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<ListPushData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }

    data class ListPushData(
        val id:Int,
        val title:String?,
        val notice_details: String?,
        val modified_time:String?
        )
}