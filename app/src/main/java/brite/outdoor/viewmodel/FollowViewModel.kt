package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.SingleLiveEvent
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel() {

    var consumed = SingleLiveEvent<Boolean>().apply { value=false }
    //entity
    val entityNew = SingleLiveEvent<ListPostUserData>()

    //follow User
    private val _requestFollowUser : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestFollowUser(requestFollowUser : HashMap<String,String>) {
        _requestFollowUser.value = requestFollowUser
    }
    private val _followUsersResult = _requestFollowUser.switchMap {
        performGetOperation(gson) { apiController.followUnFollowUser(it) }
    }
    val followUserResult : LiveData<ApiResult<ResponseFollow.FollowResult>> = _followUsersResult

    // list Post User Follow
    private val _requestListPostUserFollow : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestListPostUserFollow(requestListPostUserFollow : HashMap<String,String>) {
        _requestListPostUserFollow.value = requestListPostUserFollow
    }
    private val _listPostUserFollowResult = _requestListPostUserFollow.switchMap {
        performGetOperation(gson) { apiController.listPostUserFollow(it) }
    }
    val listPostUserFollowResult : LiveData<ApiResult<ResponseListPostUser.ListPostUserResult>> = _listPostUserFollowResult

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