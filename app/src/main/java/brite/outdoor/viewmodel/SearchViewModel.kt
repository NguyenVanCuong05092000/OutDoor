package brite.outdoor.viewmodel

import androidx.lifecycle.*
import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseSearchLocations
import brite.outdoor.data.api_entities.response.ResponseSearchUser
import brite.outdoor.data.api_entities.response.ResponseSearchUtensils
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val gson : Gson, apiController: ApiController): ViewModel() {
    val searchType = MutableLiveData(ObjectSearch.ResultSearchEntity.LOCATE)
    val isSelectedSearLocate = MutableLiveData(false)
    val isSelectedSearTool = MutableLiveData(false)
    val isSelectedSearUser = MutableLiveData(false)
    fun setSearchType(type: Int) {
        searchType.value = type
    }


    private val _listResult = searchType.switchMap {
        liveData {
            when (it) {
                ObjectSearch.ResultSearchEntity.LOCATE -> {
                    isSelectedSearLocate.value = true
                    isSelectedSearTool.value = false
                    isSelectedSearUser.value = false
                    emit(listSearchLocate)
                }
                ObjectSearch.ResultSearchEntity.TOOL -> {
                    isSelectedSearLocate.value = false
                    isSelectedSearTool.value = true
                    isSelectedSearUser.value = false
                    emit(listSearchTool)
                }
                ObjectSearch.ResultSearchEntity.USER -> {
                    isSelectedSearLocate.value = false
                    isSelectedSearTool.value = false
                    isSelectedSearUser.value = true
                    emit(listSearchUser)
                }
            }
        }
    }
    val listResult: LiveData<ArrayList<ObjectSearch.ResultSearchEntity>> = _listResult

    //Test
   private val listSearchLocate: ArrayList<ObjectSearch.ResultSearchEntity> = arrayListOf()

    private val listSearchTool: ArrayList<ObjectSearch.ResultSearchEntity> = arrayListOf()

    private val listSearchUser: ArrayList<ObjectSearch.ResultSearchEntity> = arrayListOf()

    //search locations
    private val _requestSearchLocations : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestSearchLocations(requestSearchLocations : HashMap<String,String>) {
        _requestSearchLocations.value = requestSearchLocations
    }
    private val _searchLocationsResult = _requestSearchLocations.switchMap {
        performGetOperation(gson) { apiController.searchLocations(it) }
    }
    val searchLocationsResult : LiveData<ApiResult<ResponseSearchLocations.SearchLocationsResult>> = _searchLocationsResult

    //search utensils
    private val _requestSearchUtensils : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestSearchUtensils(requestSearchUtensils : HashMap<String,String>) {
        _requestSearchUtensils.value = requestSearchUtensils
    }
    private val _searchUtensilsResult = _requestSearchUtensils.switchMap {
        performGetOperation(gson) { apiController.searchUtensils(it) }
    }
    val searchUtensilsResult : LiveData<ApiResult<ResponseSearchUtensils.SearchUtensilsResult>> = _searchUtensilsResult

    //search user

    private val _requestSearchUser : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestSearchUser(requestSearchUser : HashMap<String,String>) {
        _requestSearchUser.value = requestSearchUser
    }
    private val _searchUserResult = _requestSearchUser.switchMap {
        performGetOperation(gson) { apiController.searchUser(it) }
    }
    val searchUserResult : LiveData<ApiResult<ResponseSearchUser.SearchUserResult>> = _searchUserResult
}