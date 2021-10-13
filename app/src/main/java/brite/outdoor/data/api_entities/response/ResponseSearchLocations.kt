package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseSearchLocations {
    class SearchLocationsResult:BaseApiResult()
    @Parcelize
    data class SearchLocationsResponse(
          val url_prefix:String?,
          val total:Int?,
          val data: @RawValue Any?,
    ):Parcelable{
        fun getListData():ArrayList<SearchLocationData>?{
            try {
                val gson=GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<SearchLocationData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
    @Parcelize
    data class SearchLocationData(
            val id:Int,
            val name:String,
            val description:String?=null,
            val imageName:String?,
            val cIndex:Int?,
            val lat:String?,
            val lng:String?,
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