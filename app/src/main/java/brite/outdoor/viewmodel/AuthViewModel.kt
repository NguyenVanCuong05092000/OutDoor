package brite.outdoor.viewmodel

import android.util.Log
import androidx.lifecycle.*
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.local.room.SaveListLocationLocal
import brite.outdoor.data.local.room.SaveListUtensilsLocal
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.data.repository.AppRepository
import brite.outdoor.utils.SingleLiveEvent
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val gson: Gson,
    private val apiController: ApiController,
    private val appRepository : AppRepository
) : ViewModel() {
    var loggingWith = AppConst.LOGIN_WITH_UNKNOWN
    var locale = ""
    var isUpdateLocaleFrmHome : Boolean = true
    //Login Google
    val loginResult = MediatorLiveData<ApiResult<ResponseLogin.LoginResult>>()
    private var loginGoogleInfo = MutableLiveData<HashMap<String, String>>()
    private val _loginGoogleResult: LiveData<ApiResult<ResponseLogin.LoginResult>> =
        loginGoogleInfo.switchMap { info ->
            performGetOperation(gson) { apiController.loginWithGoogle(info) }
        }

    //Login Facebook
    private var loginFacebookInfo = MutableLiveData<HashMap<String, String>>()
    private val _loginFacebookResult: LiveData<ApiResult<ResponseLogin.LoginResult>> =
        loginFacebookInfo.switchMap { info ->
            performGetOperation(gson) { apiController.loginWithFacebook(info) }
        }

    //Logout
    private val _requestLogout: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private val _logoutResult = _requestLogout.switchMap {
        performGetOperation(gson) { apiController.logout(it) }
    }
    val logoutResult: LiveData<ApiResult<ResponseLogout.LogoutResult>> = _logoutResult

    //Regis firebase token
    private var _userId: String = ""
    private val _requestRegisterToken: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private val _registerTokenPushResult = _requestRegisterToken.switchMap {
        performGetOperation(gson) {
            apiController.registerTokenPush(it, _userId)
        }
    }
    val registerTokenPushResult: LiveData<ApiResult<ResponseRegisterToken.RegisterTokenResult>> =
        _registerTokenPushResult

    //Init
    init {
        loginResult.addSource(_loginGoogleResult) { result ->
            result?.let {
                loginResult.value = it
            }
        }

        loginResult.addSource(_loginFacebookResult) { result ->
            result?.let {
                loginResult.value = it
            }
        }
    }

    //Request API
    fun loginWithGoogle(requestParam: HashMap<String, String>) {
        loginGoogleInfo.value = requestParam
    }

    fun loginWithFacebook(requestParam: HashMap<String, String>) {
        loginFacebookInfo.value = requestParam
    }

    fun requestRegisterTokenPush(requestRegisterToken: HashMap<String, String>, userId: String) {
        _requestRegisterToken.value = requestRegisterToken
        _userId = userId
    }

    fun requestLogout(requestLogout: HashMap<String, String>) {
        _requestLogout.value = requestLogout
    }

    //create hash and send mail
    private val _requestCreateHash : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestCreateHash(requestCreateHash : HashMap<String,String>) {
        _requestCreateHash.value = requestCreateHash
    }
    private val _createHashResult = _requestCreateHash.switchMap {
        performGetOperation(gson) { apiController.createHash(it) }
    }
    val createHashResult : LiveData<ApiResult<ResponseCreateHash.CreateHashResult>> = _createHashResult

    //create new account
    private val _requestCreateNewAccount : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestCreateNewAccount(requestCreateNewAccount : HashMap<String,String>) {
        _requestCreateNewAccount.value = requestCreateNewAccount
    }
    private val _createNewAccountResult = _requestCreateNewAccount.switchMap {
        performGetOperation(gson) { apiController.createNewAccount(it) }
    }
    val createNewAccountResult : LiveData<ApiResult<ResponseCreateNewAccount.CreateNewAccountResult>> = _createNewAccountResult

    //check Code Hash
    private val _requestCheckCodeHash : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestCheckCodeHash(requestCheckCodeHash : HashMap<String,String>) {
        _requestCheckCodeHash.value = requestCheckCodeHash
    }
    private val _checkCodeHashResult = _requestCheckCodeHash.switchMap {
        performGetOperation(gson) { apiController.checkCodeHash(it) }
    }
    val checkCodeHashResult : LiveData<ApiResult<ResponseCheckCodeHash.CheckCodeHashResult>> = _checkCodeHashResult

    //merge Account
    private val _requestMergeAccount : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestMergeAccount(requestMergeAccount : HashMap<String,String>) {
        _requestMergeAccount.value = requestMergeAccount
    }
    private val _mergeAccountResult = _requestMergeAccount.switchMap {
        performGetOperation(gson) { apiController.mergeAccount(it) }
    }
    val mergeAccountResult : LiveData<ApiResult<ResponseMergeAccount.MergerAccountResult>> = _mergeAccountResult

    //merge Account
    private val _requestCancelMergeAccount : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestCancelMergeAccount(requestCancelMergeAccount : HashMap<String,String>) {
        _requestCancelMergeAccount.value = requestCancelMergeAccount
    }
    private val _cancelMergeAccountResult = _requestCancelMergeAccount.switchMap {
        performGetOperation(gson) { apiController.cancelMergeAccount(it) }
    }
    val cancelMergeAccountResult : LiveData<ApiResult<ResponseCancelMergeAccount.CancelMergeAccountResult>> = _cancelMergeAccountResult

    //fetch List Location
    private val _requestListLocation : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListLocation(requestListLocation : HashMap<String,String>) {
        _requestListLocation.value = requestListLocation
    }
    private val _listLocationResult = _requestListLocation.switchMap {
        performGetOperation(gson) { apiController.fetchListLocation(it) }
    }
    val listLocationResult : LiveData<ApiResult<ResponseListLocation.LocationResult>> = _listLocationResult

    // fetch list Utensils
    private val _requestListUtensils : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestListUtensils(requestListUtensils : HashMap<String,String>) {
        _requestListUtensils.value = requestListUtensils
    }
    private val _listUtensilsResult = _requestListUtensils.switchMap {
        performGetOperation(gson) { apiController.fetchListUtensils(it) }
    }
    val listUtensilsResult : LiveData<ApiResult<ResponseListUtensils.UtensilsResult>> = _listUtensilsResult

    //save list Location, Utensils Local
    fun saveListLocationToLocal(saveListLocationLocal: SaveListLocationLocal, list: ArrayList<ResponseListLocation.LocationData>){
        viewModelScope.launch (Dispatchers.IO){
            saveListLocationLocal.saveListLocation(list)
        }
    }
    fun saveListUtensilsToLocal(saveListUtensilsLocal: SaveListUtensilsLocal, list: ArrayList<ResponseListUtensils.UtensilsData>){
        viewModelScope.launch (Dispatchers.IO){
            saveListUtensilsLocal.saveListUtensils(list)
        }
    }

    var isHaveLocalDataSelectPlace :Boolean = false
    fun getPostSelectPlaceContentInDB(id:String){
        viewModelScope.launch (Dispatchers.IO){
            val content = appRepository.getSelectPlace(id)
            isHaveLocalDataSelectPlace = content != null
        }
    }
    var isHaveLocalDataSelectUtensils:Boolean = false
    fun getPostSelectUtensilsContentInDB(id:String){
        viewModelScope.launch (Dispatchers.IO){
            val content = appRepository.getSelectUtensils(id)
            isHaveLocalDataSelectUtensils = content != null
        }
    }
    // share post
    var postIdShare :Int = 0
    var positionPostShareAndLike : Int=0
    var currentFragmentPostShare:Int=0
    fun positionPostShareNeedNotifyChange(postIdShareParam:Int,positionPostShareParam:Int=0,currentFragmentParam:Int){
        positionPostShareAndLike = positionPostShareParam
        postIdShare = postIdShareParam
        currentFragmentPostShare=currentFragmentParam
    }
    private val _requestPostShare : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestPostShare(requestPostShare : HashMap<String,String>) {
        _requestPostShare.value = requestPostShare
    }
    private val _postShareResult = _requestPostShare.switchMap {
        performGetOperation(gson) { apiController.postShares(it) }
    }
    val postShareResult : LiveData<ApiResult<ResponseShares.SharesResult>> = _postShareResult

    // notify unread
    var countNotifyUnread = MutableLiveData<Int>().apply { value=0 }
    fun setCountNotifyUnread(count:Int){
        countNotifyUnread.value=count
    }
    private val _requestNotifyUnread : MutableLiveData<HashMap<String, String>> = MutableLiveData()
    fun requestNotifyUnread(requestNotifyUnread : HashMap<String,String>) {
        _requestNotifyUnread.value = requestNotifyUnread
    }
    private val _notifyUnreadResult = _requestNotifyUnread.switchMap {
        performGetOperation(gson) { apiController.notifyUnread(it) }
    }
    val notifyUnreadResult : LiveData<ApiResult<ResponseNotifyUnread.NotifyUnreadResult>> = _notifyUnreadResult
}