package brite.outdoor.data.local.room

import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseListUtensils
import javax.inject.Inject

class SaveListUtensilsLocal @Inject constructor(private val localDataSource: AppDao) {
    fun saveListUtensils( list:ArrayList<ResponseListUtensils.UtensilsData>) {
        localDataSource.insertListUtensils(list)
    }
    fun getListUtensils() = localDataSource.getListUtensils()
}