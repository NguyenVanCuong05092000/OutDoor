package brite.outdoor.data.api_entities.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

object ResponseLike {
    class LikesResult : BaseApiResult()
    @Parcelize
    data class LikeResponse(
        val liked:Boolean?,
        val post_id: Int?,
        val like_count: Int?,
        val share_count: Int?,
        val comment_count: Int?
    ): Parcelable
}