package brite.outdoor.data.api_entities.response



object ResponseListPostUser {
    class ListPostUserResult : BaseApiResult()

    data class ResponseContent (
            val content:String?,
            var listImg:ArrayList<String>?)
}