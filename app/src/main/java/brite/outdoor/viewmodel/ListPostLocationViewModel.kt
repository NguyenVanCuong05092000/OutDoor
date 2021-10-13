package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.ResponseDetailPost
import brite.outdoor.data.api_entities.response.ResponseListPostLocation
import brite.outdoor.data.api_entities.response.ResponseListPostUser
import brite.outdoor.data.api_entities.response.ResponseSearchLocations
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListPostLocationViewModel @Inject constructor(private val gson : Gson, apiController: ApiController): ViewModel() {

    // list Post Location
    private var _locationId:String = ""
    private val _requestListPostLocation : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListPostLocation(requestListLocationPost : HashMap<String,String>,locationId:String) {
        _requestListPostLocation.value = requestListLocationPost
        _locationId=locationId
    }
    private val _listPostLocationResult = _requestListPostLocation.switchMap {
        performGetOperation(gson) { apiController.listPostLocation(it,_locationId) }
    }
    val listPostLocationResult : LiveData<ApiResult<ResponseListPostUser.ListPostUserResult>> = _listPostLocationResult

    // list Post Utensils
    private var _utensilsId:String = ""
    private val _requestListPostUtensils : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListPostUtensils(requestListPostUtensils : HashMap<String,String>,utensilsId:String) {
        _requestListPostUtensils.value = requestListPostUtensils
        _utensilsId=utensilsId
    }
    private val _listPostUtensilsResult = _requestListPostUtensils.switchMap {
        performGetOperation(gson) { apiController.listPostUtensils(it,_utensilsId) }
    }
    val listPostUtensilsResult : LiveData<ApiResult<ResponseListPostUser.ListPostUserResult>> = _listPostUtensilsResult

    //delete Post
    private var _postId: String = ""
    private val _requestDeletePost : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestDeletePost(requestDeletePost : HashMap<String,String>,postId:String) {
        _requestDeletePost.value = requestDeletePost
        _postId=postId
    }
    private val _deletePostResult = _requestDeletePost.switchMap {
        performGetOperation(gson) { apiController.deletePost(it,_postId) }
    }
    val deletePostResult : LiveData<ApiResult<ResponseDetailPost.DetailPostResult>> = _deletePostResult
}