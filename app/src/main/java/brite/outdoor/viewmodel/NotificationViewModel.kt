package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.ResponseListPostUser
import brite.outdoor.data.api_entities.response.ResponseNotification
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel() {

    // list Notification
    private val _requestListNotification : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestListNotification(requestListNotification : HashMap<String,String>) {
        _requestListNotification.value = requestListNotification
    }
    private val _listNotificationResult = _requestListNotification.switchMap {
        performGetOperation(gson) { apiController.listNotification(it) }
    }
    val listNotificationResult : LiveData<ApiResult<ResponseNotification.NotificationResult>> = _listNotificationResult
}