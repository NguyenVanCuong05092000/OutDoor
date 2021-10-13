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
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val gson: Gson, apiController: ApiController) :
    ViewModel() {
    val detailUser = MutableLiveData<ResponseDetail.DetailResponse>()
    //State
    var isExpanded = true

    // load more myPost
    var isLoadMoreMyPost = false

    //load more myBookMark
    var isLoadMoreMyBookMark=false

    //detail User
    private val _requestDetailUser: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestDetailUser(requestDetailUser: HashMap<String, String>) {
        _requestDetailUser.value = requestDetailUser
    }

    private val _detailUserResult = _requestDetailUser.switchMap {
        performGetOperation(gson) { apiController.detailUser(it) }
    }
    val detailUserResult: LiveData<ApiResult<ResponseDetail.DetailResult>> = _detailUserResult

    val numberLike = SingleLiveEvent<Int>()
    fun setNumberLike(count:Int){
        numberLike.value = count
    }
    //list Post User
    val listMyPost = ArrayList<ListPostUserData>()

    val pageNumberMyPost: MutableLiveData<Int> = MutableLiveData(1)
    var lastestId : String = ""
    var lastestIdBookmark:String=""
    var tmpPageNumberMyPost = 0
    var totalMyPost = 0
    var isCallingApiGetListMyPost = false
    fun isNeedGetListMyPost() = tmpPageNumberMyPost != pageNumberMyPost.value
    fun syncPageNumberMyPost() {
        isCallingApiGetListMyPost = false
        tmpPageNumberMyPost = pageNumberMyPost.value ?: 0
    }

    fun syncPageNumberMyPostFailed() {
        isCallingApiGetListMyPost = false
        pageNumberMyPost.value = tmpPageNumberMyPost
    }

    private val _requestListPostUser: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListPostUser(requestListPostUser: HashMap<String, String>) {
        _requestListPostUser.value = requestListPostUser
    }

     val _listPostUserResult = _requestListPostUser.switchMap {
        isCallingApiGetListMyPost = true
        performGetOperation(gson) { apiController.listPostUser(it) }
    }
    val listPostUserResult: LiveData<ApiResult<ResponseListPostUser.ListPostUserResult>> =
        _listPostUserResult

    //list Bookmark
    val listMyBookmark = ArrayList<ResponseListBookMark.ListBookMarkData>()

    val pageNumberMyBookmark: MutableLiveData<Int> = MutableLiveData(1)
    var tmpPageNumberMyBookmark = 0
    var totalMyBookmark = 0
    var isCallingApiGetListMyBookmark = false
    fun isNeedGetListMyBookmark() = tmpPageNumberMyBookmark != pageNumberMyBookmark.value
    fun syncPageNumberMyBookmark() {
        isCallingApiGetListMyBookmark = false
        tmpPageNumberMyBookmark = pageNumberMyBookmark.value ?: -1
    }
    fun syncPageNumberMyBookmarkFailed() {
        isCallingApiGetListMyBookmark = false
        pageNumberMyBookmark.value = tmpPageNumberMyBookmark
    }

    val itemPosition = MutableLiveData<Int>(0)
    private val _requestListBookMark: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListBookMark(requestListBookMark: HashMap<String, String>) {
        _requestListBookMark.value = requestListBookMark
    }

    private val _listBookMarkResult = _requestListBookMark.switchMap {
        isCallingApiGetListMyBookmark = true
        performGetOperation(gson) { apiController.listBookMark(it) }
    }
    val listBookMarkResult: LiveData<ApiResult<ResponseListBookMark.ListBookMarkResult>> =
        _listBookMarkResult

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