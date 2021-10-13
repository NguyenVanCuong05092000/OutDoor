package brite.outdoor.viewmodel

import androidx.lifecycle.*
import brite.outdoor.data.api_entities.response.*

import brite.outdoor.data.local.room.SaveListLocationLocal
import brite.outdoor.data.local.room.SaveListUtensilsLocal
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.SingleLiveEvent
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    //
    var currentTab = -1
    //listComment
//    fun getListComment(requestListComment: HashMap<String, String>, postId: String){
//        _requestListComment.value = requestListComment
//        _postId = postId
//    }
//    private var _postId: String = ""
//    private val _requestListComment : MutableLiveData<HashMap<String, String>> = MutableLiveData()
//    private val _listCommentResult = _requestListComment.switchMap {
//        performGetOperation(gson){
//            apiController.getListComment(it, _postId)
//        }
//    }
//    val listCommentResult: LiveData<ApiResult<ResponseListComment.ListCommentResult>> = _listCommentResult
//
//    // list Post User Follow
//    private val _requestListPostUserFollow : MutableLiveData<HashMap<String, String>> = MutableLiveData()
//    fun requestListPostUserFollow(requestListPostUserFollow : HashMap<String,String>) {
//        _requestListPostUserFollow.value = requestListPostUserFollow
//    }
//    private val _listPostUserFollowResult = _requestListPostUserFollow.switchMap {
//        performGetOperation(gson) { apiController.listPostUserFollow(it) }
//    }
//    val listPostUserFollowResult : LiveData<ApiResult<ResponseListPostUser.ListPostUserResult>> = _listPostUserFollowResult
}