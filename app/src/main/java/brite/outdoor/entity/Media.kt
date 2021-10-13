package brite.outdoor.entity

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(
    val uri: Uri,
    val isVideo: Boolean,
    val path:String =""
): Parcelable