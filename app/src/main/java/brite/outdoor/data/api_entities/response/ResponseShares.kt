package brite.outdoor.data.api_entities.response

object ResponseShares {
    class SharesResult : BaseApiResult()
    data class ShareResponse(
        val post_id:Int,
        val like_count:Int?,
        val share_count:Int?,
        val comment_count:Int?
    )
}