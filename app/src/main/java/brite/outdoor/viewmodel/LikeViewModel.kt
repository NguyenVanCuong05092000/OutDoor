package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.ResponseLike
import brite.outdoor.data.api_entities.response.ResponseLogout
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel(){

    //like
    private val _requestLike : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestLike(requestLike : HashMap<String,String>) {
        _requestLike.value = requestLike
    }
    private val _likeResult = _requestLike.switchMap {
        performGetOperation(gson) { apiController.postLike(it) }
    }
    val likeResult : LiveData<ApiResult<ResponseLike.LikesResult>> = _likeResult
}