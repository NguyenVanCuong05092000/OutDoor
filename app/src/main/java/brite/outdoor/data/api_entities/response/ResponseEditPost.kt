package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlin.Exception

object ResponseEditPost {
    class EditPostResult():BaseApiResult()

    @Parcelize
    data class EditPostResponse(
        val data:@RawValue Any?,
    ):Parcelable{
        fun getDataEditPost():ListPostUserData?{
            try {
                val gson= GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ListPostUserData>(){}.type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}