package brite.outdoor.data.entities

object ObjectSearch {
    data class ResultSearchEntity(var type : Int, var content : Any) {
        companion object {
            const val LOCATE = 1
            const val TOOL = 2
            const val USER = 3
        }
    }
}