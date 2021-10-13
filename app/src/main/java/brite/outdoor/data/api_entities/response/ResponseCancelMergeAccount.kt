package brite.outdoor.data.api_entities.response

object ResponseCancelMergeAccount {
    class CancelMergeAccountResult : BaseApiResult()
    data class CancelMergeAccountResponse(
            val response:Boolean
    )
}