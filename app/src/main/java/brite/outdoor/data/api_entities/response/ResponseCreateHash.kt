package brite.outdoor.data.api_entities.response

object ResponseCreateHash {
    class CreateHashResult : BaseApiResult()
    data class CreateHashResponse(
            val code_hash:String,
            val merging:Boolean
    )
}