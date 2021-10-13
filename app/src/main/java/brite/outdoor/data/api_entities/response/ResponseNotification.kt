package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseNotification {
    class NotificationResult : BaseApiResult()

    @Parcelize
    data class NotificationResponse(
            val total: Int,
            val data: @RawValue Any?,
            val url_prefix_avatar: String?
    ) : Parcelable{
        fun getListNotification(): ArrayList<ListNotificationData>?{
            try {
                val gson = GsonBuilder().create()
                val json = gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<ListNotificationData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
    class ListNotificationData(
            val id: Int,
            val user_id: Int,
            val user_created_id:Int,
            val type: Int,
            val p_id: Int?,
            val c_id: Int?,
            val c_comment: String?=null,
            val created_time: String,
            val user_name_created: String,
            val avatar_user: String,
            val thread_id:Int?,
    )


}
