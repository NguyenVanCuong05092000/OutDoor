package brite.outdoor.entity

class EntityCommentReply {
    var urlAvatar: String? = null
    var id: String? = null
    var parent_id: String? = null
    var user_id: String? = null
    var comment: String? = null
    var name: String? = null
    var date: String? = null
    var post_id:String? = null
    val thread_id:String? = null
    val modified_time:String? = null
    constructor()

    constructor(urlAvatar: String?, name: String?, comment: String?, date: String?) {
        this.urlAvatar = urlAvatar
        this.name = name
        this.comment = comment
        this.date = date
    }

    constructor(id: String?,name: String?, post_id: String?, user_id: String?, comment: String?, thread_id:String?, modified_time:String?  ) {
        this.id = id
        this.name = name
        this.post_id = post_id
        this.user_id = user_id
        this.comment = comment
    }

}