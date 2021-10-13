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
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel(){
    // for hold all players generated
    var playersMap: MutableMap<Int, SimpleExoPlayer>  = mutableMapOf()
    // for hold current player
    var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null
    fun releaseAllPlayers(){
        playersMap.map {
            it.value.release()
        }
    }

    // call when item recycled to improve performance
    fun releaseRecycledPlayers(index: Int){
        playersMap[index]?.release()
    }

    // call when scroll to pause any playing player
    fun pauseCurrentPlayingVideo(){
        if (currentPlayingVideo != null){
            currentPlayingVideo?.second?.playWhenReady = false
        }
    }

    fun playIndexThenPausePreviousPlayer(index: Int){
        if (playersMap.get(index)?.playWhenReady == false) {
            pauseCurrentPlayingVideo()
            playersMap.get(index)?.playWhenReady = true
            currentPlayingVideo = Pair(index, playersMap.get(index)!!)
        }

    }
    //
    val resultLoadMore = SingleLiveEvent<ListPostUserResponse>()
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

    // list Post Focus
    private val _requestListPostFocus : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestListPostFocus(requestListPostFocus : HashMap<String,String>) {
        _requestListPostFocus.value = requestListPostFocus
    }
    private val _listPostFocusResult = _requestListPostFocus.switchMap {
        performGetOperation(gson) { apiController.listPostFocus(it) }
    }
    val listPostFocusResult : LiveData<ApiResult<ResponseListPostUser.ListPostUserResult>> = _listPostFocusResult

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