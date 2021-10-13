package brite.outdoor.utils

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter
import brite.outdoor.data.entities.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object TypeConvertUtils {
    class SourceTypeConverter {
        @TypeConverter
        fun listToJson(list: MutableLiveData<ArrayList<PostContentEntity>>): String {
            val listConTent = list.value
            val listConTentConvert = arrayListOf<PostContentEntityConvert>()
            listConTent?.let {
                for (content in it) {
                    listConTentConvert.add(PostContentEntityConvert(content.content,content.listImg.value!!))
                }
            }

            return Gson().toJson(listConTentConvert)
        }

        @TypeConverter
        fun jsonToList(source: String?): MutableLiveData<ArrayList<PostContentEntity>> {
            var listConTentConvert = arrayListOf<PostContentEntityConvert>()
            var listConTent = arrayListOf<PostContentEntity>()
            if (!source.isNullOrEmpty()) {
                val type: Type = object : TypeToken<ArrayList<PostContentEntityConvert>>() {}.type
                listConTentConvert = Gson().fromJson(source, type)
                for (content in listConTentConvert) {
                    listConTent.add(PostContentEntity(content.content,MutableLiveData(content.listImg)))
                }
            }

            return MutableLiveData(listConTent)
        }
        @TypeConverter
        fun objectToJson(data:ImagePicker?):String{
            return if (data==null) ""
            else Gson().toJson(data)

        }
        @TypeConverter
        fun jsonToObject(source: String?):ImagePicker?{
            var imagePicker:ImagePicker?=null
            if (!source.isNullOrEmpty()){
                val type: Type = object : TypeToken<ImagePicker>() {}.type
                imagePicker = Gson().fromJson(source,type)
            }
            return imagePicker
        }

        @TypeConverter
        fun objectUtensilsToJson(utensils:UtensilsEntity?):String{
            return if (utensils==null) ""
            else Gson().toJson(utensils)
        }
        @TypeConverter
        fun jsonToObjectUtensils(source: String?):UtensilsEntity?{
            var utensilsEntity:UtensilsEntity?=null
            if (!source.isNullOrEmpty()){
                val type: Type = object : TypeToken<UtensilsEntity>() {}.type
                utensilsEntity = Gson().fromJson(source,type)
            }
            return utensilsEntity
        }

        @TypeConverter
        fun objectLocationToJson(locationEntity: LocationEntity?):String{
            return if (locationEntity==null) ""
            else Gson().toJson(locationEntity)
        }
        @TypeConverter
        fun jsonToObjectLocation(source: String?):LocationEntity?{
            var locationEntity:LocationEntity?=null
            if (!source.isNullOrEmpty()){
                val type: Type = object : TypeToken<LocationEntity>() {}.type
                locationEntity = Gson().fromJson(source,type)
            }
            return locationEntity
        }
    }
}