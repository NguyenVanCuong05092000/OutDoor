package brite.outdoor.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import brite.outdoor.R
import brite.outdoor.adapter.AdapterNotificationHome
import brite.outdoor.adapter.utils.AdapterNotification
import brite.outdoor.app.MyApplication
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_LASTEST_ID
import brite.outdoor.constants.ApiConst.Companion.PARAM_LANGUAGE
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.AppConst.Companion.DEFAULT_FIRST_PAGE
import brite.outdoor.constants.AppConst.Companion.FRM_NOTIFICATION
import brite.outdoor.constants.AppConst.Companion.FRM_POST_LOCATION
import brite.outdoor.constants.AppConst.Companion.FRM_POST_UTENSILS
import brite.outdoor.constants.AppConst.Companion.LANGUAGE_VIETNAM
import brite.outdoor.constants.AppConst.Companion.TYPE_NOTIFICATION_COMMENT
import brite.outdoor.constants.AppConst.Companion.TYPE_NOTIFICATION_FOLLOW
import brite.outdoor.data.api_entities.response.ResponseListPush
import brite.outdoor.data.api_entities.response.ResponseNotification
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmNotificationBinding
import brite.outdoor.databinding.FrmSearchBinding
import brite.outdoor.ui.dialog.DialogFragmentAddImagesAvatar
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.AuthViewModel
import brite.outdoor.viewmodel.MyPostViewModel
import brite.outdoor.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.math.log

@AndroidEntryPoint
class FrmNotification : BaseFragment<FrmNotificationBinding>(){
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val viewMode by viewModels<NotificationViewModel>()
    private var adapter : AdapterNotificationHome? = null
    private var numberPage = DEFAULT_FIRST_PAGE
    var totalLoadMoreResult: Int = AppConst.DEFAULT_TOTAL_RESULT
    private val myPostViewModel by viewModels<MyPostViewModel>()
    private var lastestId:Int?=null

    override fun loadControlsAndResize(binding: FrmNotificationBinding?) {
            binding?.apply {
                setupTopNavigation(this)
                val rect = Rect()
                refreshLayout.getDrawingRect(rect)
                refreshLayout.setProgressViewOffset(false, 200, 350)

                naviBottomHome.btnHome.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
                naviBottomHome.btnMyPage.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
                naviBottomHome.btnNotification.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
                naviBottomHome.ivBackGround.resizeLayout(getSizeWithScale(375.0),getSizeWithScale(89.0))
//            btnNotification1.resizeLayout(getSizeWithScale(61.0),getSizeWithScale(87.0))

                naviBottomHome.btnSearch.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
//            naviBottHome.imgBgIcAddHome.resizeLayout(getSizeWithScale(71.0),getSizeWithScale(100.0))
                naviBottomHome.btnAddHome.resizeLayout(getSizeWithScale(60.0),getSizeWithScale(60.0))
                naviBottomHome.vFakeButton.resizeLayout(getSizeWithScale(72.0),getSizeWithScale(72.0))
                naviBottomHome.btnNotification.isChecked=true
                naviBottomHome.btnNotificationUnread.resizeLayout(getSizeWithScale(18.0),getSizeWithScale(18.0))
                naviBottomHome.btnNotificationUnread.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(10.0))

                naviBottomHome.apply {
                    btnAddHome.setOnClickListener {
                        showPopupWindow()
                    }
                    btnAddPlace.setSingleClickSwitchScreen {
                        if (authViewModel.isHaveLocalDataSelectPlace){
                            FrmPushPosts.getInstance(typeFragment = FRM_POST_LOCATION).show(childFragmentManager,"FrmPushPost")
                            hideBtnAddHomeBackToScreenHome()
                        }else{
                            DialogFragmentAddImagesAvatar.getInstance(FrmHome.DIALOG_FRM_LOCATION).show(this@FrmNotification.childFragmentManager,"DialogFragmentAddImages")
                            hideBtnAddHomeBackToScreenHome()
                        }
                    }
                    btnAddUtensils.setSingleClickSwitchScreen {
                        if (authViewModel.isHaveLocalDataSelectUtensils){
                            FrmPushPosts.getInstance(typeFragment = FRM_POST_UTENSILS).show(childFragmentManager,"FrmPushPost")
                            hideBtnAddHomeBackToScreenHome()
                        }else{
                            DialogFragmentAddImagesAvatar.getInstance(FrmHome.DIALOG_FRM_UTENSILS).show(this@FrmNotification.childFragmentManager,"DialogFragmentAddImages")
                            hideBtnAddHomeBackToScreenHome()
                        }
                    }
                }
                getBinding()?.apply {
                    ltRetry.apply {
                        tvErrMsg.setTextSizePX(getSizeWithScaleFloat(14.0))
                        btnRetry.setSingleClick {
                            shimmerFrameLayout.startShimmer()
                            shimmerFrameLayout.visibility = View.VISIBLE
                            callApiListNotification(AppConst.DEFAULT_FIRST_PAGE)
                            ltRetry.clRetryRoot.visibility=View.GONE
                        }
                    }

                }
            }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmNotificationBinding {
        return FrmNotificationBinding.inflate(inflater,container,false)
    }
    private fun setupTopNavigation(binding: FrmNotificationBinding) {
        binding.apply {
            clTopNav.resizeHeight(getSizeWithScale(56.0))
            tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))

        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        initAdapter()
        getBinding()?.rcView?.adapter=adapter
        callApiListNotification()
        observerListNotification()
        eventLoadMore()
        onRefreshListener()
        onListenerNaviBottom()
        observerHandleAPILoadingAndErr()
        observerNotifyUnread()
    }
    private fun initAdapter(){
        try {
            adapter= AdapterNotificationHome(requireContext(),getBinding()?.rcView!!,object : AdapterNotificationHome.OnClickItemListener{
                override fun onClickItem(entity: ResponseNotification.ListNotificationData?, position: Int) {
                    if (entity?.type == TYPE_NOTIFICATION_FOLLOW){
                        mActivity?.showPersonalPage(entity.user_created_id)
                    }else if (entity?.type== TYPE_NOTIFICATION_COMMENT){
                        entity.p_id?.let { pId->
                            entity.c_id?.let { cId->
                                entity.thread_id?.let {threadId->
                                    mActivity?.showDetail(pId,null,null,itemDeleteListener = {},itemHideListener = {},true,cId,threadId = threadId)
                                }

                            }
                        }
                    }else{
                        entity?.p_id?.let {
                            mActivity?.showDetail(it,null,null,itemDeleteListener = {},itemHideListener = {},)
                        }
                    }

                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiListNotification(lastestId:Int?=null){
        try {
            getBinding()?.apply {
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
                rcView.visibility = View.GONE
            }
            val requestParam = mActivity?.getRequestParamWithToken()
            requestParam?.set(PARAM_LANGUAGE, LANGUAGE_VIETNAM)
            lastestId?.let {
                requestParam?.set(PARAM_API_LASTEST_ID,it.toString())
            }
            requestParam?.let {
                viewMode.requestListNotification(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun observerListNotification(){
        viewMode.listNotificationResult.observe(viewLifecycleOwner,{ it ->
            if (it.status !=ApiResult.Status.CONSUMED){
                try {
                    myPostViewModel.showOrHideLoading(it)
                    if (it.status ==  ApiResult.Status.SUCCESS){
                        getBinding()?.apply {
                            ltRetry.clRetryRoot.visibility = View.GONE
                            shimmerFrameLayout.visibility = View.GONE
                            rcView.visibility = View.VISIBLE
                        }
                       if (it.data?.response is ResponseNotification.NotificationResponse){
                           (it.data.response as ResponseNotification.NotificationResponse).apply {
                               adapter?.let { adapter ->
                                   this.getListNotification()?.let {
                                       if (numberPage == AppConst.DEFAULT_FIRST_PAGE){
                                            adapter.submitList(it)
                                            adapter.setUrlPrefixAvatar(this.url_prefix_avatar)
                                            adapter.setLoaded()
                                       }
                                       else{
                                           adapter.addAllItem(it)
                                           adapter.setLoaded()
                                         
                                       }
                                   }
                               }
                               this.getListNotification()?.let {
                                   totalLoadMoreResult = it.size
                               }

                               this.getListNotification()?.let {
                                   lastestId= it[it.size-1].id
                               }

                           }
                       }
                        myPostViewModel.isShowLoading.value = false

                        // count notify unread = 0
                        authViewModel.countNotifyUnread.value=AppConst.DEFAULT_COUNT_NOTIFY_UNREAD

                    }  else if (it.status==ApiResult.Status.ERROR_NETWORK) adapter?.setLoaded()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        })
    }
    private fun observerHandleAPILoadingAndErr() {
        try {
            myPostViewModel.isNeedShowErr.observe(viewLifecycleOwner, {
                when (it.status) {
                    ApiResult.Status.ERROR, ApiResult.Status.ERROR_NETWORK -> {
                        getBinding()?.apply {
                            if (adapter?.arrayList?.size == 0){
                                ltRetry.clRetryRoot.visibility = View.VISIBLE
                                ltRetry.tvErrMsg.text =
                                        if (it.status == ApiResult.Status.ERROR_NETWORK) this@FrmNotification.getString(
                                                R.string.no_internet_connection
                                        ) else it.message
                            }else{
                                shareViewModel.isNeedShowErr.value = it
                            }
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            rcView.visibility = View.VISIBLE
                        }

                    }
                    ApiResult.Status.ERROR_TOKEN ->{
                        shareViewModel.isNeedShowErr.value = it
                        getBinding()?.apply {
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            rcView.visibility = View.VISIBLE
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun eventLoadMore() {
        try {
            adapter?.setLoadMore(object : AdapterNotificationHome.ILoadMore {
                override fun onLoadMore() {
                    try {
                        if (totalLoadMoreResult == AppConst.DEFAULT_ITEM_PER_PAGE_COMMENT) {
                            ++numberPage
                            lastestId?.let {
                                callApiListNotification(it)
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun onRefreshListener() {
        getBinding()?.refreshLayout?.setOnRefreshListener {
            onRefreshData()
            getBinding()?.refreshLayout?.isRefreshing=false
        }

    }
    override fun getCurrentFragment(): Int {
        return FRM_NOTIFICATION
    }

    override fun finish() {
        mActivity?.closeMainFuncScreen(this)
        authViewModel.apply {
            if (!isUpdateLocaleFrmHome){
                mActivity?.showHome()
                authViewModel.isUpdateLocaleFrmHome = true
            }
        }
    }

    private fun onListenerNaviBottom(){
        try {
            getBinding()?.naviBottomHome?.apply {
                var currentSelected = this.btnMyPage
                listOf<AppCompatRadioButton>(
                        this.btnHome, this.btnMyPage, this.btnNotification,this.btnSearch
                ).forEach {
                    it.setOnClickListener { view ->
                        currentSelected.isChecked = false
                        currentSelected = it
                        currentSelected.isChecked = true

                        when(view.id){
                            btnHome.id->{
                                finish()
                            }
                            btnMyPage.id->{
                                mActivity?.showMyPage()
                                hideBtnAddHomeBackToScreenHome()
                            }
                            btnNotification.id->{
                                onRefreshData()
                            }
                            btnSearch.id->{
                                mActivity?.showSearch()
                                hideBtnAddHomeBackToScreenHome()
                            }
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun hideBtnAddHomeBackToScreenHome(){
        try {
            getBinding()?.apply {
                naviBottomHome.btnAddHome.post {
                    if (naviBottomHome.btnAddHome.isChecked){
                        naviBottomHome.btnAddPlace.visibility=View.GONE
                        naviBottomHome.btnAddUtensils.visibility=View.GONE
                        naviBottomHome.btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_ic_add_home_unchecked))
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun hideBtnAddHome(isRefresh :Boolean = false){
        try {
            getBinding()?.apply {
                naviBottomHome.btnAddHome.post {
                    if (naviBottomHome.btnAddHome.isChecked){
                        naviBottomHome.btnAddPlace.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide_item_left))
                        naviBottomHome.btnAddPlace.visibility=View.GONE

                        naviBottomHome. btnAddUtensils.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide_item_right))
                        naviBottomHome.btnAddUtensils.visibility=View.GONE

                        naviBottomHome.btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_ic_add_home_unchecked))
                        naviBottomHome.btnAddHome.isChecked=false
                        if (isRefresh) naviBottomHome.btnNotification.isChecked = true
                        else naviBottomHome.btnNotification.isChecked=!naviBottomHome.btnNotification.isChecked
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    private fun showPopupWindow(){
        try {
            getBinding()?.naviBottomHome?.apply {
                btnAddPlace.apply {
                    if (isVisible){
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.hide_item_left))
                        visibility=View.GONE

                    }else{
                        visibility=View.VISIBLE
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.show_item_left))
                    }
                }
                btnAddUtensils.apply {
                    if (isVisible){
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.hide_item_right))
                        visibility=View.GONE

                    }else{
                        visibility=View.VISIBLE
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.show_item_right))
                    }
                }
                if (btnAddHome.isChecked){
                    btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_ic_add_home_checked))
                    btnNotification.isChecked=false
                }
                else{
                    btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_ic_add_home_unchecked))
                    btnNotification.isChecked=true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post(true)
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe
    fun onEvent(event: Any) {
        when(event){
            is Boolean ->{
                event.apply {
                    getBinding()?.apply {
                        naviBottomHome.btnHome.isChecked=false
                        naviBottomHome.btnMyPage.isChecked=false
                        naviBottomHome.btnNotification.isChecked=true
                        naviBottomHome.btnSearch.isChecked=false
                        naviBottomHome.btnAddHome.isChecked=false
                    }
                }
            }
            is String ->{
                event.apply {
                    hideBtnAddHome()
                }
            }
        }
    }
    private fun onRefreshData(){
        val refresh = CoroutineScope(Dispatchers.Main)
        refresh.launch {

           if (getBinding()?.naviBottomHome?.btnAddHome?.isChecked==true){
               hideBtnAddHome(true)
               delay(325)
           }
            numberPage= DEFAULT_FIRST_PAGE
            callApiListNotification()

        }

    }

    override fun backToPrevious() {
        finish()
    }
    override fun isBackPreviousEnable() : Boolean{
        return true
    }
    private fun observerNotifyUnread(){
        try {
            authViewModel.countNotifyUnread.observe(viewLifecycleOwner,{
                getBinding()?.naviBottomHome?.apply {
                    if (it!=0){
                        btnNotificationUnread.visibility = View.VISIBLE
                        btnNotificationUnread.text = it.toString()
                    }else{
                        btnNotificationUnread.visibility = View.GONE
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}