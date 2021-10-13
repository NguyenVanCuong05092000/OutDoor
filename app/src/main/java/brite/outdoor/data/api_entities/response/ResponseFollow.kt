package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

object ResponseFollow {

    class FollowResult:BaseApiResult()

    @Parcelize
    data class FollowResponse(
            val follows:Boolean?,
            val follower_id: Int?
    ): Parcelable
}