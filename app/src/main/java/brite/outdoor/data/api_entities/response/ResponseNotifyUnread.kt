package brite.outdoor.data.api_entities.response

object ResponseNotifyUnread {
    class NotifyUnreadResult : BaseApiResult()
    data class NotifyUnreadResponse(
        val count:Int
    )
}