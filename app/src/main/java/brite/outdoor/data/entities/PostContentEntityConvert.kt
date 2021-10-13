package brite.outdoor.data.entities

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverters
import brite.outdoor.utils.TypeConvertUtils
import java.io.Serializable


class PostContentEntityConvert : Serializable {
    var content: String = ""
    var listImg: ArrayList<ImagePicker> = ArrayList()

    constructor()
    constructor(content: String, listImg: ArrayList<ImagePicker>) {
        this.content = content
        this.listImg = listImg
    }
}

