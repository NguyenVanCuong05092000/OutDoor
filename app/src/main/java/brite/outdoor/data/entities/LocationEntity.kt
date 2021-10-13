package brite.outdoor.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class LocationEntity(
    var lat: Double = 0.0,
    var long: Double = 0.0,
    var locationId: String,
    var locationName: String
):Parcelable {

}