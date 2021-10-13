package brite.outdoor.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val gson : Gson, apiController: ApiController): ViewModel() {

    //getListComment
    fun getListComment(requestListComment: HashMap<String, String>, postId: String){
        _requestListComment.value = requestListComment
        _postId = postId
    }
    private var _postId: String = ""
    private val _requestListComment : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private val _listCommentResult = _requestListComment.switchMap {
        performGetOperation(gson){
            apiController.getListComment(it, _postId)
        }
    }
    val listCommentResult: LiveData<ApiResult<ResponseListComment.ListCommentResult>> = _listCommentResult

    //getListCommentLevel2
    fun getListCommentLevel2(requestListCommentLevel2: HashMap<String, String>, threadId: String){
        _requestListCommentLevel2.value = requestListCommentLevel2
        _threadId = threadId
    }
    private var _threadId: String = ""
    private val _requestListCommentLevel2 : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private val _listCommentLevel2Result = _requestListCommentLevel2.switchMap {
        performGetOperation(gson){
            apiController.getListCommentLevel2(it, _threadId)
        }
    }
    val listCommentLevel2Result: LiveData<ApiResult<ResponseListCommentLevel2.ListCommentLevel2Result>> = _listCommentLevel2Result

    //postComment
    private val _requestComment : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private val _requestListImg : MutableLiveData<ArrayList<MultipartBody.Part>> = MutableLiveData()
    fun postComment(requestComment: HashMap<String, String>,
                    requestListImg: ArrayList<MultipartBody.Part>){
        _requestComment.value = requestComment
        _requestListImg.value = requestListImg
    }
    private val _postCommentResult = _requestComment.switchMap {
            performGetOperation(gson){
                apiController.postComment(it,_requestListImg.value)

        }

    }
    val postCommentResult: LiveData<ApiResult<ResponsePostComment.PostCommentResult>> = _postCommentResult

    //delete comment
    private var _idComment : String = ""
    private val _requestDeleteComment : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestDeleteComment(requestDeleteComment : HashMap<String,String>, idComment: String) {
        _requestDeleteComment.value = requestDeleteComment
        _idComment = idComment
    }
    private val _deleteCommentResult = _requestDeleteComment.switchMap {
        performGetOperation(gson) { apiController.deleteComment(it, _idComment) }
    }
    val deleteCommentResult : LiveData<ApiResult<ResponseDeleteComment.DeleteCommentResult>> = _deleteCommentResult
}