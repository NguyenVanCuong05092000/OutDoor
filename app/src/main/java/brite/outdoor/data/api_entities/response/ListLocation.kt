package brite.outdoor.data.api_entities.response

 data class ListLocation (
         val id:Int,
         val name:String,
         val description:String,
         val imageName:String?,
         val lat:String,
         val lng:String
         )