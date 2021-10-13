package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

object ResponseSearchUtensils {
    class SearchUtensilsResult : BaseApiResult()
    @Parcelize
    data class SearchUtensilsResponse(
        val data: @RawValue Any?,
        val url_prefix:String?,
        val total:Int?
    ):Parcelable{
        fun getListData():ArrayList<SearchUtensilsData>?{
            try {
                val gson=GsonBuilder().create()
                val json=gson.toJson(data)
                return gson.fromJson(json,object : TypeToken<ArrayList<SearchUtensilsData>>(){}.type)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
    @Parcelize
    data class SearchUtensilsData(
            val id:Int,
            val name:String,
            val description:String?,
            val imageName:String?,
            val cIndex:Int,
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