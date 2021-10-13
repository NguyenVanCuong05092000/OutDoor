package brite.outdoor.ui.activities

import android.app.Dialog
import android.content.*
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import brite.outdoor.R
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.ApiConst.Companion.ANDROID
import brite.outdoor.constants.ApiConst.Companion.API_KEY
import brite.outdoor.constants.ApiConst.Companion.PARAM_API
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_CODE_HASH
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_TYPE_SOCIAL
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_USER_ID
import brite.outdoor.constants.ApiConst.Companion.PARAM_DEVICE_TYPE
import brite.outdoor.constants.ApiConst.Companion.PARAM_DEVICE_UUID
import brite.outdoor.constants.ApiConst.Companion.PARAM_TOKEN
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.AppConst.Companion.LOGIN_WITH_FACEBOOK
import brite.outdoor.constants.AppConst.Companion.LOGIN_WITH_GOOGLE
import brite.outdoor.constants.AppConst.Companion.LOGIN_WITH_UNKNOWN
import brite.outdoor.constants.ExtraConst
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_COMMENT_ID
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_LOCATION_ID
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_THREAD_ID
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_TYPE_FRAGMENT
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.ApiNoticeEntity
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.entities.BackStackData
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.ActMainNewBinding
import brite.outdoor.ui.dialog.*
import brite.outdoor.ui.fragments.*
import brite.outdoor.ui.fragments.my_page.FrmMyPage
import brite.outdoor.ui.fragments.select_place.FrmMap
import brite.outdoor.utils.AppUtils
import brite.outdoor.utils.ViewSize
import brite.outdoor.viewmodel.AuthViewModel
import brite.outdoor.viewmodel.ShareViewModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.progress_dialog_loading.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class MainActivity : BaseActivity<ActMainNewBinding>() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun setBinding(): ActMainNewBinding {
        return ActMainNewBinding.inflate(layoutInflater)
    }

    private val shareViewModel by viewModels<ShareViewModel>() //Need add @AndroidEntryPoint to class use
    private val authViewModel by viewModels<AuthViewModel>()
    private var shareDialogFacebook:ShareDialog?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        prepareGoogleClient()
        prepareLoginFacebook()
        navigationApp()
        setData()
        setupBackgroundTop()
        setupObserverLoadingAndError()
        callBackShareLinkFacebook()
        observerNotifyUnread()
//        callApiRegisterTokenPush()
//        initScreen()
        getLocale()
        observerRegisterToken()
        setup()
        loadLocale()


    }

    private fun getLocale(){
        val language = PrefManager.getInstance(this).getString(PrefConst.PREF_MULTI_LANGUAGE)
        if (language != null) {
            authViewModel.locale=language
        }
    }

    private fun prepareGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initScreen() {
        if (isLogged()) {
            callApiRegisterTokenPush()
//            navController.navigate(R.id.action_global_frmHome)
        }
    }

    private fun setData() {
//        getBinding()?.lifecycleOwner = this
//        getBinding()?.shareViewModel = shareViewModel
    }

    private fun setupBackgroundTop() {
        getBinding()?.apply {
        }
    }

    private fun setupObserverLoadingAndError() {
        shareViewModel.isShowLoading.observe(this, {
            if (it == true) {
                showProgress(false)
            } else {
                hideProgress()
            }
        })
        shareViewModel.isNeedShowErr.observe(this, {
            if (it.status==ApiResult.Status.ERROR_NETWORK)  showErrDialog(ApiNoticeEntity(it.status,this.getString(R.string.no_internet_connection)))
            else showErrDialog(it)
        })
    }

    //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue = resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }

    fun getSizeWithScale(sizeDesign: Double): Int {
        return (sizeDesign * getScaleValue()).toInt()
    }

    fun getSizeWithScaleFloat(sizeDesign: Double): Float {
        return (sizeDesign * getScaleValue()).toFloat()
    }

    fun getRealSize(sizeDesign: ViewSize): ViewSize {
        return ViewSize(sizeDesign.width * getScaleValue(), sizeDesign.height * getScaleValue())
    }
    //End Resize

    fun getGoogleSignInClient() = mGoogleSignInClient

    //Start Dialog
    private var dialogProgress: DialogProgress? = null
    private var dlgNotice: DialogNotice? = null
    var dlgReport : DialogFragmentReport?=null
    var bottomSheet:BottomSheetDialogFrHome?=null
    private var dlgShare : DialogShare?=null
    private var dlgErrorDelete : DialogNotice?=null
    private var dlgNotify:DialogNotify?=null
    private fun showErrDialog(apiNoticeEntity: ApiNoticeEntity) {
        if (dlgNotice == null) {
            dlgNotice = DialogNotice(this)
        }
        if (dlgNotice!!.isShowing) return
        //not show dialog ApiResult.Status.ERROR_DELETE_POST
        when(apiNoticeEntity.status){
            ApiResult.Status.ERROR_DELETE_POST ->{
            }
            else ->{
                dlgNotice!!.show(resources.getString(R.string.titlePostNoLongerExists),apiNoticeEntity.message, false) {
                    when (apiNoticeEntity.status) {
                        ApiResult.Status.ERROR_TOKEN -> {
                            //remove notification
                            NotificationManagerCompat.from(this).cancel(AppConst.NOTIFICATION_USER)
                            logoutSNS()
                            releaseMemory()
                        }
                        else -> {
                        }

                    }
                }
            }
        }

    }

    fun logoutSNS() {
        when (loginWith()) {
            LOGIN_WITH_GOOGLE -> {
                logoutGoogle()
            }
            LOGIN_WITH_FACEBOOK -> {
                logoutFacebook()
            }
        }
    }
    fun onShareLinkFacebook(content:String){
        try {
            val shareLink = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(AppConst.URL_OUTDOOR))
                    .setQuote(content)
                    .build()
            if (shareDialogFacebook==null) shareDialogFacebook= ShareDialog(this)
             shareDialogFacebook?.show(shareLink)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun showNotice(msg: String) {
        if (dlgNotice == null) {
            dlgNotice = DialogNotice(this)
        }
        if (dlgNotice!!.isShowing) return
        dlgNotice!!.show(resources.getString(R.string.titlePostNoLongerExists),msg, false, null)
    }

    fun showNotice(resMsg: Int) {
        if (dlgNotice == null) {
            dlgNotice = DialogNotice(this)
        }
        if (dlgNotice!!.isShowing) return
        dlgNotice!!.show(resMsg, false, null)
    }
    fun showDialogReport(entity:ListPostUserData?,callback:DialogFragmentReport.CallBackListener,position: Int?){
        dlgReport= DialogFragmentReport(entity,position,getListReport(),getRequestParamWithToken())
        dlgReport?.onCallBackListener(callback)
        dlgReport?.setStyle(DialogFragment.STYLE_NORMAL,R.style.dialogReport)
        dlgReport?.isCancelable=true
        dlgReport?.show(this.supportFragmentManager,"dialogReport")
    }
    fun showBottomSheet(entity:ListPostUserData?,callback:BottomSheetDialogFrHome.CallBackListener,position:Int){
        if (bottomSheet==null)  bottomSheet= BottomSheetDialogFrHome()
        bottomSheet?.setData(entity,position)
        bottomSheet?.setRequestParam(getRequestParamWithToken())
        bottomSheet?.onCallBackListener(callback)
        if (bottomSheet?.isAdded==false) bottomSheet?.show(this.supportFragmentManager,"bottomSheet")

    }
    fun showDialogExit(title:String, mgs:String, listener: DialogExit.CallbackListenerExit){
        val dlgExit = DialogExit(this)
        if (dlgExit.isShowing) return
        dlgExit.show(false,title,mgs,listener)
    }
    fun showDialogShare(any:Any?,onClickShareListener:(String)->Unit,urlPrefixAvatar:String?=null){
        try {
             dlgShare = DialogShare(this,onClickShareListener = {title:String->onClickShareListener(title)})

            when(any){
                is ListPostUserData -> getAvatarId()?.let { dlgShare?.show(any, it) }
                is ResponseDetailPost.DetailPostResponse-> getAvatarId()?.let {
                    dlgShare?.show(any,urlPrefixAvatar,
                        it)
                }
                is ResponseListBookMark.ListBookMarkData -> dlgShare?.show(any)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun showDialogErrorDelete(title: String,mgs:String,dismissListener: DialogInterface.OnDismissListener?=null){
        if (dlgErrorDelete == null) dlgErrorDelete= DialogNotice(this)
        if (dlgErrorDelete?.isShowing==true) return
        dlgErrorDelete?.show(title,mgs,false,dismissListener)
    }
    fun showDialogNotify(message:String){
        if (dlgNotify==null) dlgNotify = DialogNotify(this)
        if (dlgNotify?.isShowing==true) return
        dlgNotify?.show(message =message,cancelAble=false )
    }

    fun showProgress(cancelAble: Boolean) {
        try {
            if (dialogProgress==null) dialogProgress = DialogProgress(this)
            if (dialogProgress!!.isShowing) return
            dialogProgress?.show(cancelAble)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
        fun hideProgress() {
        try {
            if (dialogProgress != null){
                dialogProgress?.dismiss()

            }
            dialogProgress?.stopAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //End Dialog

    //Start Utils
    private fun loginWith() = PrefManager.getInstance(this).getString(PrefConst.PREF_LOGIN_WITH) ?: ""

    fun isLogged() = loginWith().isNotEmpty()
    fun isFirstOpenApp() = PrefManager.getInstance(this).getBoolean(PrefConst.PREF_IS_FIRST_OPEN_APP)


    private var currentToken: String? = null
    fun updateToken(token: String) {
        currentToken = token
        PrefManager.getInstance(this).writeString(PrefConst.PREF_TOKEN, token)
    }

    private fun getCurrentToken(): String {
        if (currentToken.isNullOrEmpty()) currentToken =
            PrefManager.getInstance(this).getString(PrefConst.PREF_TOKEN)
        return currentToken!!
    }

    fun releaseMemory() {
        PrefManager.getInstance(this).releaseUserDataWhenLogout()
    }
    //End Utils

    //Request param
    private fun getRequestParam(): HashMap<String, String> {
        val params = HashMap<String, String>()
        params[PARAM_API] = API_KEY
//        params[PARAM_API_VERSION] = API_VERSION
        params[PARAM_DEVICE_TYPE] = ANDROID
        params[ApiConst.PARAM_LANGUAGE] = getLanguage().toString()
        AppUtils.getAndroidId(this)?.let {
            params.put(PARAM_DEVICE_UUID, it)
        }

        return params
    }

    fun getRequestParamWithToken(): HashMap<String, String> {
        val params = getRequestParam()
        getCurrentToken().let {
            if (it.isNotEmpty()) {
                params[PARAM_TOKEN] = it
            }
        }
        return params
    }

    /**
     * Dismiss all DialogFragments added to given FragmentManager and child fragments
     */
    private fun dismissAllDialogs(manager: FragmentManager?) {
        val fragments: List<Fragment> = manager?.fragments ?: return
        for (fragment in fragments) {
            if (fragment is DialogFragment) {
                val dialogFragment = fragment as DialogFragment
                dialogFragment.dismissAllowingStateLoss()
            }
            val childFragmentManager: FragmentManager = fragment.childFragmentManager
            dismissAllDialogs(childFragmentManager)
        }
    }
    //Logout SNS
    fun logoutGoogle() {
        try {
            shareViewModel.isShowLoading.value = true
            getGoogleSignInClient().signOut().addOnCompleteListener {
                if (it.isSuccessful) {
//                    Timber.d("isSuccessful ${navController.currentDestination?.id} != ${R.id.frmLogin}")
                    releaseMemory()
//                    if (navController.currentDestination?.id != R.id.frmLogin) {
//                        navController.navigate(R.id.action_global_frmLogin)
//                    }
                    showLogin()
                    unregisterReceiver()
                    dismissAllDialogs(supportFragmentManager)
                }
                authViewModel.loggingWith = LOGIN_WITH_UNKNOWN
                shareViewModel.isShowLoading.value = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logoutFacebook() {
        try {
            shareViewModel.isShowLoading.value = true
            LoginManager.getInstance().logOut()
            releaseMemory()
            showLogin()
            unregisterReceiver()
            dismissAllDialogs(supportFragmentManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Login Facebook
    fun loginFacebook() {
        try {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val accessTokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            try {
                if (currentAccessToken == null) {
                    releaseMemory()
//                    if (navController.currentDestination?.id != R.id.frmLogin) {
//                        navController.navigate(R.id.action_global_frmLogin)
//                    }
                    showLogin()
                    dismissAllDialogs(supportFragmentManager)
                    this.stopTracking()
                    authViewModel.loggingWith = LOGIN_WITH_UNKNOWN
                    shareViewModel.isShowLoading.value = false
                    Timber.d("currentAccessToken: ${this.isTracking}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private var callbackManager = CallbackManager.Factory.create()
    private fun prepareLoginFacebook() {
        try {
            LoginManager.getInstance().registerCallback(callbackManager, object :
                FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    Timber.d("onSuccess: ${result.toString()}")
                    accessTokenTracker.startTracking()
                    callApiLoginWithFacebook(result)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                    Timber.d("error: ${error.toString()}")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callBackShareLinkFacebook(){
        try {
            if (shareDialogFacebook ==null) shareDialogFacebook = ShareDialog(this)
            shareDialogFacebook?.registerCallback(callbackManager,object :FacebookCallback<Sharer.Result>{
                override fun onSuccess(result: Sharer.Result?) {
                    callApiPostShare()
                    dlgShare?.dismiss()
                    val language = PrefManager.getInstance(this@MainActivity).getString(PrefConst.PREF_MULTI_LANGUAGE)
                    language?.let { setLocale(it) }

                }

                override fun onCancel() {
                    val language = PrefManager.getInstance(this@MainActivity).getString(PrefConst.PREF_MULTI_LANGUAGE)
                    language?.let { setLocale(it) }
                }

                override fun onError(error: FacebookException?) {
                    Timber.d("error: ${error.toString()}")
                    showNotice(resources.getString(R.string.str_sharefb_error))

                }

            })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun callApiPostShare(){
        try {
            val requestParam = getRequestParamWithToken()
                requestParam[ApiConst.PARAM_API_POST_ID] = authViewModel.postIdShare.toString()
                authViewModel.requestPostShare(requestParam)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
     fun callApiNotifyUnread(){
        try {
            val requestParam = getRequestParamWithToken()
            authViewModel.requestNotifyUnread(requestParam)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerNotifyUnread(){
        authViewModel.notifyUnreadResult.observe(this,{
            if (it.status == ApiResult.Status.SUCCESS){
                if (it.data?.response is ResponseNotifyUnread.NotifyUnreadResponse){
                    (it.data.response as ResponseNotifyUnread.NotifyUnreadResponse).let {
                        authViewModel.setCountNotifyUnread(it.count)
                    }
                }
            }
        })
    }

    //Login Google
    fun loginGoogle() {
        try {
            val signInIntent = getGoogleSignInClient().signInIntent
            startForResult.launch(signInIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            try {
                result.data?.let {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    fun getAvatarId(): String? {
        try {
            return PrefManager.getInstance(this).getString(PrefConst.PREF_USER_AVATAR)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun getUserId(): String? {
        try {
            return PrefManager.getInstance(this).getString(PrefConst.PREF_USER_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun getUserEmail(): String? {
        try {
            return PrefManager.getInstance(this).getString(PrefConst.PREF_EMAIL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun getUserName(): String? {
        try {
            return PrefManager.getInstance(this).getString(PrefConst.PREF_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun getUserNumberInteractive(key: String): String{
        try {
            val number: String? = PrefManager.getInstance(this).getString(key)
            if (!number.isNullOrEmpty()) return number
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }
    fun getUrlPrefixAvatar(): String? {
        try {
            return PrefManager.getInstance(this).getString(PrefConst.PREF_URL_PREFIX_AVATAR)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun getLanguage(): Int{
        try {
            return PrefManager.getInstance(this).getInt(PrefConst.PREF_LANGUAGE, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 1
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            completedTask.getResult(ApiException::class.java)?.let {
                callApiLoginWithGoogle(it)
            }
        } catch (e: ApiException) {
            Timber.d("handleSignInResult>>ApiException: ${e.statusCode}")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    //API
    private fun callApiLoginWithFacebook(result: LoginResult?) {
        try {
            val requestParam = getRequestParam()
            result?.accessToken?.userId?.let {
                requestParam[ApiConst.PARAM_API_SOCIAL_ID] = it
            }
            result?.accessToken?.token?.let {
                requestParam[ApiConst.PARAM_API_ACCESS_TOKEN] = it
            }
            authViewModel.loggingWith = LOGIN_WITH_FACEBOOK
            authViewModel.loginWithFacebook(requestParam)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiLoginWithGoogle(account: GoogleSignInAccount) {
        try {
            val requestParam = getRequestParam()
            account.id?.let { requestParam[ApiConst.PARAM_API_SOCIAL_ID] = it }
            account.idToken?.let { requestParam[ApiConst.PARAM_API_ACCESS_TOKEN] = it }
            authViewModel.loggingWith = LOGIN_WITH_GOOGLE
            authViewModel.loginWithGoogle(requestParam)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callApiRegisterTokenPush() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val token = task.result
                    if (token != null) {
                        val requestParam = getRequestParamWithToken()
                        val userId = PrefManager.getInstance(this).getString(PrefConst.PREF_USER_ID)
                        requestParam[ApiConst.PARAM_API_FIREBASE_TOKEN] = token
                        if (userId != null) {
                                authViewModel.requestRegisterTokenPush(requestParam, userId)

                        }
                    }else{
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun callApiCreateHash(userId:String?){
        try {
            val requestParam = getRequestParam()
            userId?.let {
                requestParam[PARAM_API_USER_ID]=it
                Log.e(TAG, "callApiCreateHash: ${requestParam.values}")
                authViewModel.requestCreateHash(requestParam)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun callApiCreateNewAccount(){
        try {
            val requestParam = getRequestParam()
            authViewModel.requestCreateNewAccount(requestParam)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun callApiCheckCodeHash(codeHash:String?){
        try {
            val requestParam = getRequestParam()
            codeHash?.let {
                requestParam[PARAM_API_CODE_HASH] = it
            }
            authViewModel.requestCheckCodeHash(requestParam)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun callApiMergeAccount(socialId:String?,typeSocial:String?){
        try {
            val requestParam = getRequestParam()
            socialId?.let {
                requestParam[ApiConst.PARAM_API_SOCIAL_ID]=it
            }
            typeSocial?.let {
                requestParam[PARAM_API_TYPE_SOCIAL] = typeSocial
            }
            authViewModel.requestMergeAccount(requestParam)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun callApiCancelMerge(codeHash: String?){
        try {
            val requestParam = getRequestParam()
            codeHash?.let {
                requestParam[PARAM_API_CODE_HASH]=codeHash
                authViewModel.requestCancelMergeAccount(requestParam)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
     fun callApiListLocation(){
        try {
            val requestParam=getRequestParamWithToken()
            val lastSync=PrefManager.getInstance(this).getString(PrefConst.PREF_LAST_SYNC)
            requestParam[ApiConst.PARAM_API_LAST_SYNC]=""
//        lastSync?.let {
//            requestParam[PARAM_API_LAST_SYNC]=lastSync
//        }
            authViewModel.requestListLocation(requestParam)
            authViewModel.requestListUtensils(requestParam)
        } catch (e: Exception) {
        }
    }
   private fun getListReport():ArrayList<ResponseReport.Report>?{
        try {
            val language = PrefManager.getInstance(this).getString(PrefConst.PREF_MULTI_LANGUAGE)
            val json:String? = if (language=="vi") getJsonDataFromAsset(applicationContext, "report_violation_reason.json")
            else getJsonDataFromAsset(applicationContext, "report_violation_reason_en.json")
            val gson= GsonBuilder().create()
            return gson.fromJson(json,object : TypeToken<ArrayList<ResponseReport.Report>>(){}.type)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }
    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    fun getDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, object : OnSuccessListener<PendingDynamicLinkData?> {
                    override fun onSuccess(pendingDynamicLinkData: PendingDynamicLinkData?) {
                        // Get deep link from result (may be null if no link is found)
                        var deepLink: Uri? = null
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.link
                        }

                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, object : OnFailureListener {
                    override fun onFailure(e:Exception) {
                        Log.w(TAG, "getDynamicLink:onFailure", e)
                    }
                })
    }

    //

    fun showDetail(id : Int, entity: ListPostUserData?=null,position: Int?=null,itemDeleteListener:(Int) -> Unit,itemHideListener:(Int)->Unit,isNotification:Boolean=false,commentId:Int?=null,threadId:Int?=null) {
        val mapData = HashMap<String, Any>().apply {
            put(ExtraConst.EXTRA_POST_ID,id)
            entity?.let {
               put(ExtraConst.EXTRA_ENTITY,it)
            }
            position?.let {
                put(ExtraConst.EXTRA_POSITION,it)
            }
            commentId?.let {
                put(EXTRA_COMMENT_ID,it)

            }
            threadId?.let {
                put(EXTRA_THREAD_ID,it)
            }
            
            put(ExtraConst.EXTRA_IS_NOTIFICATION,isNotification)

        }
        addFragmentToDetail(FrmDetail.getInstance(null,mapData,itemDeleteListener,itemHideListener))
    }
    fun showLanguage(){
        val f = FrmLanguage()
        addOrReplaceFragment(f,R.id.flContainerFuncChildLanguage, f.javaClass.simpleName)
    }

    fun showSetting() {
        addFragmentToSetting(FrmSetting())
    }

    fun showMyPage(isChangeLanguage : Boolean=false) {
        addFragmentToMainFunc(FrmMyPage.getInstance(isChangeLanguage))
    }

    fun showPushPost() {
        addFragmentToMainFunc(FrmPushPosts())
    }
    fun showNotification(){
        addFragmentToMainFunc(FrmNotification())
    }
    fun showPeopleInteractive(status: Int) {
        val mapData = HashMap<String, Any>().apply {
            put(ExtraConst.EXTRA_STATUS_PEOPLE_INTERACTIVE,status)
        }
        addFragmentToListPeopleInteractive(FrmListPeopleInteractive.getInstance(null, status))
    }
    fun showSelectPlace(userId: Int) {
        addFragmentToMainFunc(FrmSelectPlaces())
    }
    fun showPersonalPage(userId: Int) {
        addFragmentToPersonalPage(FrmPersonalPage.getInstance(userId = userId))
    }
    fun showPersonalPageOrMyPage(userId: Int){
        val idUserPref= PrefManager.getInstance(this).getString(PrefConst.PREF_USER_ID)
        if (idUserPref?.toInt()==userId){
            showMyPage()
            EventBus.getDefault().post("FrmMyPage")
        }
        else showPersonalPage(userId)
    }
    fun showSearch() {
        addFragmentToMainFunc(FrmSearch())
    }
    fun showListPush() {
        addFragmentToMainFunc(FrmListPush())
    }
    fun showMap() {
        addFragmentToMainFunc(FrmMap())
    }
    fun closeMainFuncScreen(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerMainFunc)
        if (currentFragment == null) currentFragment = f
        closeScreen(currentFragment)
    }

    fun closeFuncChildScreen(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerFuncChild)
        if (currentFragment == null ) currentFragment = f
        closeScreen(currentFragment)
    }
    fun closeFuncChildScreenLanguage(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerFuncChildLanguage)
        if (currentFragment == null ) currentFragment = f
        closeScreen(currentFragment)
    }

    fun closeDetailScreen(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerFuncChild)
        if (currentFragment == null) currentFragment = f
        closeScreen(currentFragment)
    }
    fun closeListPeopleInteractive(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerListPeopleInteractive)
        if (currentFragment == null) currentFragment = f
        closeScreen(currentFragment)
    }
    fun closePersonalPage(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerFuncChild)
        if (currentFragment == null) currentFragment = f
        closeScreen(currentFragment)
    }
    fun closeSettingScreen(f : Fragment) {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.flContainerSetting)
        if (currentFragment == null) currentFragment = f
        closeScreen(currentFragment)
    }

    private fun closeScreen(currentFragment : Fragment) {
        supportFragmentManager.beginTransaction()
            .remove(currentFragment)
            .commitAllowingStateLoss()
    }

    fun showListPostLocation(locationId:String,typeFragment:Int){
        val mapData = HashMap<String,Any>().apply {
            put(EXTRA_LOCATION_ID,locationId)
            put(EXTRA_TYPE_FRAGMENT,typeFragment)
        }
        addFragmentToFuncChild(FrmListPostLocation.getInstance(null,mapData))
    }

    //
    private var currentFragment: Int = 0

    fun setCurrentScreen(currentFragment: Int) {
        this.currentFragment = currentFragment
    }

    private fun addFragmentToMain(f: Fragment) {
        removeAllTopScreenIfNeed(R.id.flContainerMain)
        addOrReplaceFragment(f,R.id.flContainerMain, f.javaClass.simpleName)
    }
    private fun addFragmentToMainFunc(f: Fragment) {
        removeAllTopScreenIfNeed(R.id.flContainerMainFunc)
        addOrReplaceFragment(f,R.id.flContainerMainFunc, f.javaClass.simpleName)
    }
    private fun addFragmentToFuncChild(f: Fragment) {
        removeAllTopScreenIfNeed(R.id.flContainerFuncChild)
        addOrReplaceFragment(f,R.id.flContainerFuncChild, f.javaClass.simpleName)
    }
    private fun addFragmentToDetail(f: Fragment) {
        addFragment(f,R.id.flContainerFuncChild,f.javaClass.simpleName)
    }
    private fun addFragmentToListPeopleInteractive(f: Fragment) {
        removeAllTopScreenIfNeed(R.id.flContainerListPeopleInteractive)
        addOrReplaceFragment(f,R.id.flContainerListPeopleInteractive, f.javaClass.simpleName)
    }
    private fun addFragmentToPersonalPage(f: Fragment) {
        addFragment(f,R.id.flContainerFuncChild,f.javaClass.simpleName)
    }
    private fun addFragmentToSetting(f: Fragment) {
        addOrReplaceFragment(f,R.id.flContainerSetting, f.javaClass.simpleName)
    }
    private val listFrame = arrayListOf(R.id.flContainerSetting, R.id.flContainerPersonalPage,R.id.flContainerDetail,R.id.flContainerListPeopleInteractive,R.id.flContainerFuncChild,R.id.flContainerMainFunc,R.id.flContainerMain)
    private fun removeAllTopScreenIfNeed(resId : Int) {
        try {
            var maxPosition = when(resId) {
                R.id.flContainerMain -> {
                    listFrame.size-2
                } R.id.flContainerMainFunc -> {
                    listFrame.size-3
                } R.id.flContainerFuncChild -> {
                    listFrame.size-4
                } R.id.flContainerDetail -> {
                    listFrame.size-5
                }
                R.id.flContainerListPeopleInteractive -> {
                    listFrame.size-6
                }R.id.flContainerPersonalPage -> {
                    listFrame.size-7
                }
                else -> {
                    -1
                }
            }
            if (maxPosition < 0) return
            for (i in 0..maxPosition) {
                val currentFragment = supportFragmentManager.findFragmentById(listFrame[i])
                if (currentFragment != null) {
                    supportFragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commitAllowingStateLoss()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun navigationApp() {
        if (isLogged()) {
            callApiRegisterTokenPush()
            showHome()
            callApiNotifyUnread()
        } else {
            if (isFirstOpenApp()) {
                showLogin()
                return
            }
            showIntro()
        }
    }

    private fun showIntro() {
        addFragmentToMain(FrmIntro())
    }

    fun showLogin(arrayList: ArrayList<BackStackData>? = null, mapData: HashMap<String, Any>? = null) {
        addFragmentToMain(FrmLogin.getInstance(arrayList, mapData))
    }

    fun showHome() {
        addFragmentToMain(FrmHome())
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                if (handleBackScreen())
                    return true
                onBackPressed()
                return true
            }
            return super.dispatchKeyEvent(event)
        }
        return false
    }



    private fun handleBackScreen() : Boolean {
        try {
            for (frame in listFrame) {
                val currentFragment = supportFragmentManager.findFragmentById(frame)
                if (currentFragment != null && currentFragment is BaseFragment<*>) {
                    if (currentFragment.isBackPreviousEnable()) {
                        currentFragment.backToPrevious()
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onNewIntent(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("push")){
                val data = extras.getBundle("push")
                val userFollowId = data?.getString("user_follow_id")
                if (userFollowId!=null){
                    this.showPersonalPage(userFollowId.toInt())
                }else{
                    val commentId = data?.getString("comment_id")
                    val postId = data?.getString("post_id")
                    val threadId =data?.getString("thread_id")
                    if (commentId?.toInt()==0){
                        postId?.let {
                            this.showDetail(it.toInt(),itemDeleteListener = {},itemHideListener = {})
                        }
                    }else{
                        if (postId !=null)
                        this.showDetail(postId.toInt(),itemHideListener = {},itemDeleteListener = {},isNotification = true,commentId = commentId?.toInt(),threadId = threadId?.toInt())
                    }
                }


            }

        }
        super.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)

    }
     fun setup() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("notify-receive-listener")
        )
    }
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
           authViewModel.countNotifyUnread.value = authViewModel.countNotifyUnread.value?.plus(1) // count notify unread increase 1 when receive notification
        }
    }
    private fun observerRegisterToken(){
        authViewModel.registerTokenPushResult.observe(this,{
        })
    }
     fun unregisterReceiver(){
         LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }
    private fun loadLocale(){
        val language = PrefManager.getInstance(this).getString(PrefConst.PREF_MULTI_LANGUAGE)
        language?.let {
            if (it=="") PrefManager.getInstance(this).writeString(PrefConst.PREF_MULTI_LANGUAGE,"vi")
            setLocale(language)
        }
    }
    private fun setLocale(lang:String){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale=locale

        this.resources.updateConfiguration(config,this.resources.displayMetrics)

    }
}