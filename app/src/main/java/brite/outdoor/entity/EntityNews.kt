package brite.outdoor.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class EntityNews (
    var urlAvatar: String? = null,
    var name: String? = null,
    var time: Int? = null,
    var title: String? = null,
    var month: String? = null,
    var day: Int? = null,
    var numberLike: Int? = null,
    var numberComment: Int? = null,
    var numberShare: Int? = null,
    var urlPost: String? = null,
    var userId :String?=null,
    var followerId:String?=null,
    var postId:String?=null
): Parcelable
