package brite.outdoor.data.entities

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverters
import brite.outdoor.utils.TypeConvertUtils
import java.io.Serializable


class PostContentEntity : Serializable {
    var id :Int=0
    var content: String = ""
    var listImg: MutableLiveData<ArrayList<ImagePicker>> = MutableLiveData(ArrayList())

    constructor()
    constructor(content: String, listImg: MutableLiveData<ArrayList<ImagePicker>>) {
        this.content = content
        this.listImg = listImg
    }
    constructor(id:Int,content: String, listImg: MutableLiveData<ArrayList<ImagePicker>>) {
        this.content = content
        this.listImg = listImg
        this.id = id
    }

}

