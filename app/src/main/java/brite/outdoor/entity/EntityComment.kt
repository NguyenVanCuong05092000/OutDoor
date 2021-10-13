package brite.outdoor.entity

import java.util.*

class EntityComment {
    var urlAvatar: String? = null
    var name: String? = null
    var content: String? = null
    var date: String? = null
    var arrayListCommentReply: ArrayList<EntityCommentReply>? = null
    constructor()

    constructor(urlAvatar: String?,name: String?, content: String?, date: String?, arrayListCommentReply: ArrayList<EntityCommentReply>?){
        this.urlAvatar = urlAvatar
        this.name = name
        this.content = content
        this.date = date
        this.arrayListCommentReply = arrayListCommentReply
    }

}