package brite.outdoor.data.api_entities.response

object ResponseLogin {
    class LoginResult : BaseApiResult()
    data class Response(
        val id:Int,
        val email:String?,
        val name:String,
        val status:Boolean,
        val gender:String?,
        val birthday:String?,
        val device_type:String?,
        val register_device_uuid:String?,
        val current_device_uuid:String?,
        val version_data:String?,
        val avatar:String?,
        val token:String,
        val firebase_token:String?,
        val is_deleted:Int?,
        val created_time:String?,
        val modified_time:String?,
        val like_counts:Int,
        val share_counts:Int,
        val follow_counts:Int,
        val url_prefix_avatar:String?,
        val merge:Boolean
    )
}