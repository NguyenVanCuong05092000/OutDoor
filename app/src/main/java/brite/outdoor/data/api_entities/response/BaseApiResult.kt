package brite.outdoor.data.api_entities.response

open class BaseApiResult() {
    var message: String? = null
    var status: Int = 0
    var response: Any? = null

    constructor(message: String?) : this() {
        this.message = message
    }
}