package brite.outdoor.data.local.room

import brite.outdoor.data.api_entities.response.ResponseListLocation
import javax.inject.Inject

class SaveListLocationLocal @Inject constructor(private val localDataSource: AppDao) {
    fun saveListLocation( list:ArrayList<ResponseListLocation.LocationData>) {
        localDataSource.insertListLocation(list)
    }
    fun getListLocation() = localDataSource.getListLocation()
}