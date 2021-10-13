package brite.outdoor.data.entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


@Parcelize
data class ImagePicker(
        var uri: String="",
        var isChecked: Boolean = false,
        var numberSelect: Int = 0,
        var position: Int = -1,
        var isVideo:Boolean = false,
        var id:Int=0,
        var thumbnail:String?=null,
        var isCamera: Boolean=false,
        var path:String=""
): Parcelable {
    override fun toString(): String {
        return "uri: $uri - isChecked: $isChecked - numberSelect: $numberSelect - position: $position - isVideo: $isVideo - id: $id - thumbnail: $thumbnail - isCamera:$isCamera - path:$path"
    }
}

@Parcelize
class ListImagePicked : ArrayList<ImagePicker>(), Parcelable {
    fun init(list : List<ImagePicker>?): ListImagePicked {
        clear()
        list?.let {
            addAll(list)
        }
        return this
    }
}