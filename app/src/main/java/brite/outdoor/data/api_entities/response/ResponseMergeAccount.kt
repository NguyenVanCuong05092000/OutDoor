package brite.outdoor.data.api_entities.response

object ResponseMergeAccount {
    class MergerAccountResult : BaseApiResult()
    data class MergeAccountResponse(
            val user_id:Int,
            val name:String?,
            val email: String?,
            val gender:String?,
            val birthday:String?,
            val avatar:String?,
            val url_prefix_avatar:String?,
            val token:String?
    )
}