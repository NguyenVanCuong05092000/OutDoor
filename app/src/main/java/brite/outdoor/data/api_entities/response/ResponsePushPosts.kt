package brite.outdoor.data.api_entities.response

import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.data.entities.PostContentEntity
import brite.outdoor.utils.TypeConvertUtils


object ResponsePushPosts {
    class PushPostResult : BaseApiResult()
    @Entity(tableName = "pushPostsResponse")
    @TypeConverters(TypeConvertUtils.SourceTypeConverter::class)
    data class PushPostsResponse(

        @PrimaryKey(autoGenerate = false) val id: String,
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "listConTent") val listConTent: MutableLiveData<ArrayList<PostContentEntity>> = MutableLiveData(
            arrayListOf()
        ),
        @ColumnInfo(name = "avatar") var avatar:ImagePicker?=null
    )
    data class PushPostsResponseApi(

        @PrimaryKey(autoGenerate = false)  @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "listConTent") val content: MutableLiveData<ArrayList<PostContentEntity>> = MutableLiveData(
            arrayListOf()
        ),
        @ColumnInfo(name = "location_id") var location_id: String = "",
        @ColumnInfo(name = "hashtag_id") var hashtag_id: String = "",
        @ColumnInfo(name = "utensil_id") var utensil_id: String = "",
        @ColumnInfo(name = "token") var token: String = "",
        @ColumnInfo(name = "created_id") var created_id: String = "",
        @ColumnInfo(name = "created_time") var created_time: String = "",
        @ColumnInfo(name = "modified_time") var modified_time: String = ""
    )
}