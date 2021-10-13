package brite.outdoor.data.api_entities.response

object ResponseHidePost {
    class HidePostResult : BaseApiResult()
    data class HidePostResponse(
            val response: Boolean
    )
}