package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.ResponseFollowUser
import brite.outdoor.data.api_entities.response.ResponseListFollower
import brite.outdoor.data.api_entities.response.ResponseListPostUser
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListPeopleInteractiveViewModel @Inject constructor(private val gson : Gson, apiController: ApiController): ViewModel() {

    // list user Follow
    private val _requestListUserFollow: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListUserFollow(requestUserFollow : HashMap<String,String>?) {
        _requestListUserFollow.value = requestUserFollow
    }
    private val _listUserFollowResult = _requestListUserFollow.switchMap {
        performGetOperation(gson) { apiController.listFollow(it) }
    }
    val listUserFollowResult : LiveData<ApiResult<ResponseFollowUser.FollowUserResult>> = _listUserFollowResult


    // list user Follower
    private val _requestListFollower: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListFollower(requestFollower : HashMap<String,String>?) {
        _requestListFollower.value = requestFollower
    }
    private val _listFollowerResult = _requestListFollower.switchMap {
        performGetOperation(gson) { apiController.listFollower(it) }
    }
    val listFollowerResult : LiveData<ApiResult<ResponseFollowUser.FollowUserResult>> = _listFollowerResult

    //
}