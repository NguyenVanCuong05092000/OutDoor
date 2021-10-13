package brite.outdoor.data.api_entities.response

object ResponseDeletePost {
    class DeletePostResult : BaseApiResult()
    data class DeletePostResponse(
            val response : Boolean
    )
}