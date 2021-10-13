package brite.outdoor.data.api_entities.response

object ResponseBookMart {
    class BookMartResult : BaseApiResult()
    data class BookMartResponse(
           val bookMark:Boolean?,
           val post_id:Int?
   )
}