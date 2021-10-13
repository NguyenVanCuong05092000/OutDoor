package brite.outdoor.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UtensilsEntity (
        var id:Int=0,
        var name:String?=null,
        var description:String?=null,
        var imageName:String?=null,
        var cIndex:Int=0,
):Parcelable