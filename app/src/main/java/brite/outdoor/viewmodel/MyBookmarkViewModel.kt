package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.data.repository.AppRepository
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class MyBookmarkViewModel @Inject constructor(apiController: ApiController, private val gson: Gson) : LoadingInPageViewModel() {
    //unBookMark
    private val _requestUnBookMark : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestUnBookMark(requestUnBookMark : HashMap<String,String>) {
        _requestUnBookMark.value = requestUnBookMark
    }
    private val _unBookMarkResult = _requestUnBookMark.switchMap {
        performGetOperation(gson) { apiController.unBookMark(it) }
    }
    val unBookMarkResult : LiveData<ApiResult<ResponseUnBookMark.UnBookMarkResult>> = _unBookMarkResult

    //registered BookMart
    private val _requestBookMart : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestBookMart(requestBookMart : HashMap<String,String>) {
        _requestBookMart.value = requestBookMart
    }
    private val _bookMartResult = _requestBookMart.switchMap {
        performGetOperation(gson) { apiController.registeredBookMart(it) }
    }
    val bookMartResult : LiveData<ApiResult<ResponseBookMart.BookMartResult>> = _bookMartResult

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