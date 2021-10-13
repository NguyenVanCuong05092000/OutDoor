package brite.outdoor.data.api_entities.response

object ResponseCreateNewAccount {
    class CreateNewAccountResult : BaseApiResult()
    data class CreateNewAccountResponse(
            val response:Boolean
    )
}