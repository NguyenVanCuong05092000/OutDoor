package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseListLocation {
    class LocationResult: BaseApiResult()
    @Parcelize
    data class LocationResponse(
            val data: @RawValue Any?,
            val url_prefix:String?,
            val total:Int?
    ):Parcelable{
         fun getListData() : ArrayList<LocationData>?{
            try {
                val gson= GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<LocationData>>(){}.type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
    @Parcelize
    @Entity(tableName = "location")
    data class LocationData(
            @PrimaryKey(autoGenerate = false)
            val id:Int,
            val name:String?=null,
            val imageName:String?,
            val description:String?=null,
            val cIndex:Int,
            val lat:String?=null,
            val lng:String?=null,
            val key_search:String?=null,
            val modified_time: String
    ):Parcelable{
        fun getListImage():ArrayList<String>?{
            try {
                val gson=GsonBuilder().create()
                return gson.fromJson(imageName,object : TypeToken<ArrayList<String>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null

        }
    }
}