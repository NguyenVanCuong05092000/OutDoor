package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brite.outdoor.data.api_entities.ApiNoticeEntity
import brite.outdoor.data.remote.ApiResult

abstract class LoadingInPageViewModel : ViewModel() {

    private val _isNeedShowErr: MutableLiveData<ApiNoticeEntity> = MutableLiveData()
    val isNeedShowErr: LiveData<ApiNoticeEntity> = _isNeedShowErr

    //Show-Hide Loading
    val isShowLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    fun <T> showOrHideLoading(result: ApiResult<T>, isNeedShowLoading: Boolean = true) {
        try {
            when (result.status) {
                ApiResult.Status.LOADING -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = true
                }
                ApiResult.Status.SUCCESS -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                }
                ApiResult.Status.ERROR_NETWORK ->{
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                    _isNeedShowErr.value= ApiNoticeEntity(result.status,null)

                }
                ApiResult.Status.ERROR, ApiResult.Status.ERROR_TOKEN -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                    _isNeedShowErr.value = ApiNoticeEntity(result.status, result.message)
                }
                ApiResult.Status.ERROR_DELETE_POST -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                    _isNeedShowErr.value = ApiNoticeEntity(result.status, result.message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}