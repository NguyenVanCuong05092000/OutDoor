package brite.outdoor.data.entities

import java.io.Serializable
import java.util.*

class BackStackData : Serializable {
    var fromFragment: Int
    var mapData: HashMap<String, Any>? = null

    constructor(from: Int) {
        fromFragment = from
    }

    constructor(from: Int, mapData: HashMap<String, Any>?) {
        fromFragment = from
        this.mapData = mapData
    }
}