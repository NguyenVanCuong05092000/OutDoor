package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.SingleLiveEvent
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel() {
    var consumed = SingleLiveEvent<Boolean>().apply { value=false }
    val resultLoadMore = SingleLiveEvent<ListPostUserResponse>()
    val entityNew=SingleLiveEvent<ListPostUserData>()
    //follow User
    private val _requestFollowUser : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestFollowUser(requestFollowUser : HashMap<String,String>) {
        _requestFollowUser.value = requestFollowUser
    }
    private val _followUsersResult = _requestFollowUser.switchMap {
        performGetOperation(gson) { apiController.followUnFollowUser(it) }
    }
    val followUserResult : LiveData<ApiResult<ResponseFollow.FollowResult>> = _followUsersResult

    // list Post New
    private val _requestListPostNew : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestListPostNew(requestListPostNew : HashMap<String,String>) {
        _requestListPostNew.value = requestListPostNew
    }
    private val _listPostNewResult = _requestListPostNew.switchMap {
        performGetOperation(gson) { apiController.listPostNew(it) }
    }
    val listPostNewResult : LiveData<ApiResult<ResponseListPostUser.ListPostUserResult>> = _listPostNewResult

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