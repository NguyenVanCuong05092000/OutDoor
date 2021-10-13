package brite.outdoor.data.api_entities.response


object ResponseDeleteComment {
    class DeleteCommentResult : BaseApiResult()
    data class DeleteCommentResponse(val data : Data?)
    data class Data(val comment_count: Int?)
}