package brite.outdoor.viewmodel

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brite.outdoor.R
import brite.outdoor.data.api_entities.ApiNoticeEntity
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ResponseEditPost
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.SingleLiveEvent
import com.bumptech.glide.load.engine.Resource
import kotlin.math.log


class ShareViewModel : ViewModel() {
    //
    var isNeedRefreshPostDataInMyPage = MutableLiveData(false)
    var isNeedRefreshPostDataInMyBookmark = MutableLiveData(false)

    var isNeedRefreshPostDataInHome_Hot = MutableLiveData(false)
    var isNeedRefreshPostDataInHome_Follow = MutableLiveData(false)
    var isNeedRefreshPostDataInHome_News = MutableLiveData(false)

    var isNedRefreshPostDataInListLocation = MutableLiveData(false)
    var isNeedRefreshPostDataInDetail = MutableLiveData(false)

    var isNeedUpdateDataWhenEditPostInHome_Hot = MutableLiveData(false)
    var isNeedUpdateDataWhenEditPostInHome_News = MutableLiveData(false)
    var isNeedUpdateDataWhenEditPostInMyPost = MutableLiveData(false)
    var isNeedUpdateDataWhenEditPostInMyBookMark = MutableLiveData(false)
    var isNeedUpdateDataWhenEditPostInListLocation = MutableLiveData(false)


    fun setNeedRefreshPostDataAllScreen() {
        isNeedRefreshPostDataInMyPage.value = true
        isNeedRefreshPostDataInHome_Hot.value = true
        isNeedRefreshPostDataInHome_Follow.value = true
        isNeedRefreshPostDataInHome_News.value = true

    }
    fun setNeedRefreshEditDataAllScreen(){
        isNeedUpdateDataWhenEditPostInHome_Hot.value = true
        isNeedUpdateDataWhenEditPostInHome_News.value = true
        isNeedUpdateDataWhenEditPostInMyPost.value = true
        isNeedUpdateDataWhenEditPostInMyBookMark.value = true
        isNeedUpdateDataWhenEditPostInListLocation.value = true
    }
    var editPost :ResponseEditPost.EditPostResponse?=null
    //

    val listIdFollow=HashMap<Int,Int>()
    val isNeedShowErr: MutableLiveData<ApiNoticeEntity> = MutableLiveData()

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
                    isNeedShowErr.value= ApiNoticeEntity(result.status,null)

                }
                ApiResult.Status.ERROR_DELETE_COMMENT->{
                    if (isNeedShowLoading)
                        isShowLoading.value=false
                    isNeedShowErr.value = ApiNoticeEntity(result.status, result.message)
                }
                ApiResult.Status.ERROR, ApiResult.Status.ERROR_TOKEN -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                    isNeedShowErr.value = ApiNoticeEntity(result.status, result.message)
                }
                ApiResult.Status.ERROR_DELETE_POST -> {
                    if (isNeedShowLoading)
                        isShowLoading.value = false
                    isNeedShowErr.value = ApiNoticeEntity(result.status, result.message)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}