package brite.outdoor.data.api_entities.response

object ResponseLogout {
    class LogoutResult : BaseApiResult()
    data class LogoutResponse(
            val response:Boolean
    )
}