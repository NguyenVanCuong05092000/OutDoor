package brite.outdoor.entity

class EntityMyPage {

    var title: String? = null
    var month: String? = null
    var day: Int? = null
    var numberLike: Int? = null
    var numberComment: Int? = null
    var numberShare: Int? = null
    var urlPost: String? = null

    constructor()
    constructor(  title: String?, month: String?, day: Int?, numberLike: Int?, numberComment: Int?, numberShare: Int?,urlPost: String? ){
        this.title = title
        this.month = month
        this.day = day
        this.numberLike = numberLike
        this.numberComment = numberComment
        this.numberShare = numberShare
        this.urlPost = urlPost
    }

}