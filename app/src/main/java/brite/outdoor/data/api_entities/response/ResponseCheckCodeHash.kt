package brite.outdoor.data.api_entities.response

object ResponseCheckCodeHash {
    class CheckCodeHashResult : BaseApiResult()
    data class CheckCodeHashResponse(
            val user_id:Int
    )
}