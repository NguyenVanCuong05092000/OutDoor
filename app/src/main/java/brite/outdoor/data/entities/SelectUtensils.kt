package brite.outdoor.data.entities

import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import brite.outdoor.utils.TypeConvertUtils

@Entity(tableName = "selectUtensils")
@TypeConverters(TypeConvertUtils.SourceTypeConverter::class)
class SelectUtensils (
        @PrimaryKey(autoGenerate = false) val id: String,
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "listConTent") val listConTent: MutableLiveData<ArrayList<PostContentEntity>> = MutableLiveData(
                arrayListOf()
        ),
        @ColumnInfo(name = "avatar") var avatar:ImagePicker?=null,
        @ColumnInfo(name = "utensils") var utensils:UtensilsEntity?=null

)