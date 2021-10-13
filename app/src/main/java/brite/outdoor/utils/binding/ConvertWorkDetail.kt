package brite.outdoor.utils.binding

class ConvertWorkDetail {
    companion object {
        @JvmStatic
        fun convertNullToEmpty(value: String?): String {
            if (value.isNullOrEmpty() || value == "null") return ""
            return value
        }
    }
}