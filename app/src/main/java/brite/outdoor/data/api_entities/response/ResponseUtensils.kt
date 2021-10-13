package brite.outdoor.data.api_entities.response

object ResponseUtensils {
    class Utensils : BaseApiResult()
    data class Response(
            val id:Int,
            val name:String,
            val description:String,
            val imageName:String?,
            val cIndex:Int
    )
}