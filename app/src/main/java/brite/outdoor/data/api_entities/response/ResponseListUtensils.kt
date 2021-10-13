package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseListUtensils {
    class UtensilsResult : BaseApiResult()
    @Parcelize
    data class UtensilsResponse(
            val data:@RawValue Any?,
            val url_prefix:String?,
            val total:Int?
    ): Parcelable{
        fun getListData() : ArrayList<UtensilsData>?{
            try {
                val gson= GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<UtensilsData>>(){}.type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
    @Parcelize
    @Entity(tableName = "utensils")
    data class UtensilsData(
            @PrimaryKey(autoGenerate = false)
            val id:Int,
            val name:String?=null,
            val description:String?,
            val cIndex:Int,
            val imageName:String?,
            val key_search:String?,
            val modified_time:String?
    ):Parcelable{
        fun getListImage():ArrayList<String>?{
            try {
                val gson=GsonBuilder().create()
                return gson.fromJson(imageName,object :TypeToken<ArrayList<String>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
}