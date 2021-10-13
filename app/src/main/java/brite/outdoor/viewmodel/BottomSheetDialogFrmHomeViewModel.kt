package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.ResponseDetailPost
import brite.outdoor.data.api_entities.response.ResponseFollow
import brite.outdoor.data.api_entities.response.ResponseHidePost
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BottomSheetDialogFrmHomeViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel()  {

    //follow User
    private val _requestFollowUser : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestFollowUser(requestFollowUser : HashMap<String,String>) {
        _requestFollowUser.value = requestFollowUser
    }
    private val _followUsersResult = _requestFollowUser.switchMap {
        performGetOperation(gson) { apiController.followUnFollowUser(it) }
    }
    val followUserResult : LiveData<ApiResult<ResponseFollow.FollowResult>> = _followUsersResult


    //hide Post
    private val _requestHidePost : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestHidePost(requestHidePost : HashMap<String,String>) {
        _requestHidePost.value = requestHidePost
    }
    private val _hidePostResult = _requestHidePost.switchMap {
        performGetOperation(gson) { apiController.hidePost(it) }
    }
    val hidePostResult : LiveData<ApiResult<ResponseHidePost.HidePostResult>> = _hidePostResult

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