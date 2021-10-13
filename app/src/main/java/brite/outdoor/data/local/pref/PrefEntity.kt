package brite.outdoor.data.local.pref

data class PrefEntity(val value: Any)
data class PrefInt(val key: String, val value: Int)
data class PrefStr(val key: String, val value: String)
data class PrefBool(val key: String, val value: Boolean)
data class PrefLng(val key: String, val value: Long)
data class PrefFlt(val key: String, val value: Float)