package brite.outdoor.viewmodel

import android.util.Log
import androidx.lifecycle.*
import brite.outdoor.constants.AppConst.Companion.FRM_POST_LOCATION
import brite.outdoor.data.api_entities.response.ResponseEditPost
import brite.outdoor.data.api_entities.response.ResponsePushPosts
import brite.outdoor.data.entities.*
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.data.repository.AppRepository
import brite.outdoor.di.AppModule
import brite.outdoor.entity.EntityNews
import brite.outdoor.utils.SingleLiveEvent
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PushPostViewModel @Inject constructor(private val appRepository : AppRepository,apiController: ApiController,private val gson:Gson) : ViewModel() {
    var location = MutableLiveData<LocationEntity?>(null)
    var urlAvatar = MutableLiveData<ImagePicker?>(null)
    var listNameImage: ArrayList<String>? = ArrayList()
    var listImgDelete = MutableLiveData<ArrayList<String?>>(null)
    var listImgEdit = MutableLiveData<ArrayList<PostContentEntity?>>(null)
    var postContentPlace: MutableLiveData<SelectPlace> = MutableLiveData()
    var postContentUtensils: MutableLiveData<SelectUtensils> = MutableLiveData()
    var isHaveLocalData = SingleLiveEvent<Boolean>()
    var isEditPost = false
    var typePost = 0
    fun getPostContentInDB(id: String,typePost:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if(typePost==FRM_POST_LOCATION){
                this@PushPostViewModel.typePost = typePost
                val content = appRepository.getSelectPlace(id)
                if (content!=null) {
                    isHaveLocalData.postValue(true)
                    postContentPlace.postValue(content)
                } else {
                    isHaveLocalData.postValue(false)
                    createNewPost(id)
                }
            }else{
                this@PushPostViewModel.typePost = typePost
                val content = appRepository.getSelectUtensils(id)
                if (content!=null) {
                    isHaveLocalData.postValue(true)
                    postContentUtensils.postValue(content)
                } else {
                    isHaveLocalData.postValue(false)
                    createNewPost(id)
                }
            }

        }
    }

    fun createNewPost(id : String) {
        if (typePost == FRM_POST_LOCATION){
            postContentPlace.postValue(SelectPlace(id,"",
                    MutableLiveData(arrayListOf(PostContentEntity())))
            )
        }else{
            postContentUtensils.postValue(SelectUtensils(id,"",
                    MutableLiveData(arrayListOf(PostContentEntity())))
            )
        }

    }

    fun savePost(){
        if (typePost == FRM_POST_LOCATION){
            viewModelScope.launch(Dispatchers.IO) {
                postContentPlace.value?.let {
                    appRepository.insertSelectPlace(it)
                    actionState.postValue(PushPostViewModel.STATE.SAVED)
                }
            }
        }else{
            viewModelScope.launch(Dispatchers.IO) {
                postContentUtensils.value?.let {
                    appRepository.insertSelectUtensils(it)
                    actionState.postValue(PushPostViewModel.STATE.SAVED)
                }
            }
        }

    }

    fun deletePost(id: String, isNeedBackPrev : Boolean,typePost: Int){
        if (typePost ==  FRM_POST_LOCATION){
            viewModelScope.launch(Dispatchers.IO) {
                appRepository.deleteSelectPlace(id)
                if (isNeedBackPrev)
                    actionState.postValue(PushPostViewModel.STATE.DELETED)
            }
        }else{
            viewModelScope.launch(Dispatchers.IO) {
                appRepository.deleteSelectUtensils(id)
                if (isNeedBackPrev)
                    actionState.postValue(PushPostViewModel.STATE.DELETED)
            }
        }

    }
    val contentSelected = MutableLiveData<PostContentEntity>()
    fun setContentSelected(position: Int,typePost: Int) {
        if (typePost == FRM_POST_LOCATION){
            postContentPlace.value?.listConTent?.value?.let {
                if (position < it.size) contentSelected.value = it[position]
            }
        }else{
            postContentUtensils.value?.listConTent?.value?.let {
                if (position < it.size) contentSelected.value = it[position]
            }
        }

    }

    enum class STATE {
        NOTHING,
        NOTIFY_DATA,
        SAVED,
        DELETED
    }
    val actionState : MutableLiveData<STATE> = MutableLiveData(PushPostViewModel.STATE.NOTHING)

    //pushPost API
    private val _requestPushPost : MutableLiveData<HashMap<String,String>> = MutableLiveData()
    private val _requestListImg : MutableLiveData<ArrayList<MultipartBody.Part>> = MutableLiveData()
    fun requestPushPost(
        requestPushPost: HashMap<String,String>,
        requestListImg: ArrayList<MultipartBody.Part>
    ) {
        _requestPushPost.value = requestPushPost
        _requestListImg.value = requestListImg
    }
    private val _pushPostResult = _requestPushPost.switchMap {
        _requestListImg.switchMap {i->
            performGetOperation(gson) { apiController.fetchPushPost(it, i) }
        }

    }
    val pushPostResult : LiveData<ApiResult<ResponsePushPosts.PushPostResult>> = _pushPostResult

    //edit post
    private val _requestEditPost : MutableLiveData<HashMap<String,String>> = MutableLiveData()
    private val _requestListImgEdit : MutableLiveData<ArrayList<MultipartBody.Part>> = MutableLiveData()
    private var _id: String = ""
    fun requestEdtPost(
        requestEditPost: HashMap<String,String>,
        requestListImg: ArrayList<MultipartBody.Part>,
        id: String
    ) {
        _requestEditPost.value = requestEditPost
        _requestListImgEdit.value = requestListImg
        _id = id
    }
    private val _editPostResult = _requestEditPost.switchMap {
        _requestListImgEdit.switchMap {i->
            performGetOperation(gson) { apiController.editPost(it, i, _id) }
        }

    }
    val editPostResult : LiveData<ApiResult<ResponseEditPost.EditPostResult>> = _editPostResult

}
