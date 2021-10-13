package brite.outdoor.entity

class EntitySavedPost {
    var title: String? = null
    var date: String? = null
    var name: String? = null
    var urlPost: String? = null

    constructor()
    constructor(title: String?, date: String?, name: String?, urlPost: String? ){
        this.title = title
        this.date = date
        this.name = name
        this.urlPost = urlPost
    }

}