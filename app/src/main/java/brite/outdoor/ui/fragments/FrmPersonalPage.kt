package brite.outdoor.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.adapter.AdapterHome
import brite.outdoor.adapter.AdapterMyPost
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.ExtraConst
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.ApiNoticeEntity
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmPersonalPageBinding
import brite.outdoor.ui.dialog.DialogComment
import brite.outdoor.ui.fragments.my_page.FrmMyPost
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.*
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class FrmPersonalPage : BaseFragment<FrmPersonalPageBinding>(), View.OnClickListener, DialogComment.OnChangeNumberComment {
    private val myPageViewModel by viewModels<MyPageViewModel>()
    private val followViewModel by viewModels<FollowViewModel>()
    private val myPostViewModel by viewModels<MyPostViewModel>()
    private val personalPageViewModel by viewModels<PersonalPageViewModel>()
    private var userId: Int? = null
    private var position: Int? = null
    private var postId: Int? = null
    private val likeViewModel by viewModels<LikeViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var dialogComment: DialogComment? = null
    private var mAdapterPostUser: AdapterMyPost? = null
    private var detailUser: ResponseDetail.DetailResponse? = null
    var totalLoadMoreResult: Int = AppConst.DEFAULT_TOTAL_RESULT
    var lastVisibleItem: Int? = null
    var totalItemCount: Int? = null
    var visibleThreshold: Int = 1
    var visibleItemCount: Int? = null
    var offset: Int? = null
    var extent: Int? = null
    var range: Int? = null
    var percentage: Int? = null
    companion object {
        fun getInstance(userId: Int): FrmPersonalPage {
            val dialog = FrmPersonalPage()
            val bundle = Bundle()
            bundle.putInt(ExtraConst.EXTRA_USER_ID,userId)
            dialog.arguments=bundle
            return dialog
        }
    }
    override fun loadControlsAndResize(binding: FrmPersonalPageBinding?) {
        binding?.apply {
            val heightToolBar = getSizeWithScale(80.0)
            val widthButtonNav = getSizeWithScale(56.0)
            val sizeAvatar = getSizeWithScale(80.0)
            val sizeImageHolder = getSizeWithScale(56.0)

            val textSizeH1 = getSizeWithScaleFloat(25.0)
            val textSizeH2 = getSizeWithScaleFloat(13.0)
            val textSizeTitlePin = getSizeWithScaleFloat(14.0)

            tvNumberLike.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH1)

            tvNumberFollowers.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH1)
            tvNumberFollowersDes.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH2)

            tvNumberFollow.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH1)
            tvNumberFollowDes.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH2)


            shimmerFrameLayout.resizeLayout(getSizeWithScale(302.0), getSizeWithScale(88.0))
            shimmerFrameLayout.layoutParams.let {
                if (it is ConstraintLayout.LayoutParams) {
                    it.topMargin = getSizeWithScale(233.0)
                }

            }
            toolbar.resizeHeight(heightToolBar)
            toolbar.setContentInsetsRelative(getSizeWithScale(70.0), widthButtonNav)
            toolbar.titleMarginStart = getSizeWithScale(10.0)
            btnFollowUser.resizeLayout(getSizeWithScale(110.0), getSizeWithScale(40.0))

            toolbarLayout.resizeLayout(getSizeWithScale(375.0), getSizeWithScale(375.0))
            toolbarLayout.expandedTitleMarginTop = getSizeWithScale(74.0)

            imageHolder.resizeLayout(sizeImageHolder, sizeImageHolder)

            clController.resizeHeight(heightToolBar)
            btnBack.resizeLayout(widthButtonNav, heightToolBar)

            imgAvatar.resizeLayout(sizeAvatar, sizeAvatar)
            imgAvatar.layoutParams.let {
                if (it is CoordinatorLayout.LayoutParams) {
                    it.topMargin = getSizeWithScale(70.0)
                }
            }

            clInteractive.resizeLayout(getSizeWithScale(302.0), getSizeWithScale(88.0))
            clInteractive.layoutParams.let {
                if (it is ConstraintLayout.LayoutParams) {
                    it.topMargin = getSizeWithScale(233.0)
                }
            }

            appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
                override fun onStateChanged(appBarLayout: AppBarLayout?, state: Int) {
                    myPageViewModel.isExpanded = state == EXPANDED
                }

            })
            btnBack.setOnClickListener(this@FrmPersonalPage)
            btnFollowUser.setOnClickListener(this@FrmPersonalPage)
        }
    }
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmPersonalPageBinding {
        return FrmPersonalPageBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            userId = getInt(ExtraConst.EXTRA_USER_ID)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        getBinding()?.apply {
            prepareList()
            callApiDetailUser()
            observerDetailUser()
            observerListPostUser()
            observerHandleAPILoadingAndErr()
            observerLike()
            observerFollowUser()
            observerSharePost()
            observerCountLike()
            getBinding()?.apply {
                shimmerFrameLayout.startShimmer()

            }
            appBarLayout.setExpanded(myPageViewModel.isExpanded, true)

            mActivity?.apply {
                try {
                    myPageViewModel.detailUser.observe(viewLifecycleOwner, {
                        try {
                            detailUser = it
                            tvNumberFollow.text = convertNumberInteractive(it.follow_counts)
                            tvNumberFollowers.text = convertNumberInteractive(it.follower_counts)
                            tvNumberLike.text = convertNumberInteractive(it.like_counts)
                            when (it.followed_flag) {
                                1 -> {
                                    btnFollowUser.setText(R.string.lblUnFollow)
                                    btnFollow.isSelected = false
                                }
                                0 -> {
                                    btnFollowUser.setText(R.string.lblFollow)
                                    btnFollow.isSelected = false
                                }
                                else -> btnFollow.visibility = View.GONE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        eventLoadMore()
        myPageViewModel.pageNumberMyPost.observe(viewLifecycleOwner,{
            checkAndCallApiListMyPostIfNeed()
        })
    }
    override fun isBackPreviousEnable() : Boolean{
        return true
    }

    override fun backToPrevious() {
        finish()
    }
    override fun finish() {
        mActivity?.closePersonalPage(this)

    }
    override fun onClick(v: View?) {
        if (!isClickAble()) return
        when (v?.id) {
            R.id.btnBack -> finish()
            R.id.btnFollowUser -> callApiFollow(detailUser?.id)
        }
    }
    private fun convertNumberInteractive(counts: Int): String {
        return if (counts > 1000) {
            (counts / 1000).toString() + " K"
        } else counts.toString()
    }
    //call api detail user
    private fun callApiDetailUser(){
        try {
            getBinding()?.apply {
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
                clInteractive.visibility = View.GONE
            }
            val requestParam = mActivity!!.getRequestParamWithToken()
            userId?.let {
                requestParam[ApiConst.PARAM_API_USER_ID] = userId.toString()
            }
            myPageViewModel.requestDetailUser(requestParam)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun observerDetailUser() {
        try {
            myPageViewModel.detailUserResult.observe(viewLifecycleOwner, {
                try {
                    if (it.status == ApiResult.Status.SUCCESS) {
                        if (it.data?.response !is Boolean) {
                            if (it.data?.response is ResponseDetail.DetailResponse) {
                                (it.data.response as ResponseDetail.DetailResponse).let { response ->
                                    getBinding()?.apply {
                                        shimmerFrameLayout.visibility = View.GONE
                                        shimmerFrameLayout.stopShimmer()

                                        clInteractive.visibility = View.VISIBLE
                                        btnFollowUser.visibility  = View.VISIBLE
                                    }

                                    myPageViewModel.detailUser.value = response
                                    myPageViewModel.setNumberLike(response.like_counts)
                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            myPageViewModel.detailUser.observe(viewLifecycleOwner, {
                getBinding()?.toolbarLayout?.title = it.name
                getBinding()?.imgAvatar?.let { imageView -> it.avatar?.let { it1 ->
                    imageView.loadImageWithCustomCorners(urlImage(it1, it.url_prefix_avatar), 150)
                    }
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun callApiListPostUser() {
        try {
            startShimmerLayout()
            val requestParam = mActivity!!.getRequestParamWithToken()
            requestParam[ApiConst.PARAM_API_PAGE] = (myPageViewModel.pageNumberMyPost.value ?: 1).toString()
            requestParam[ApiConst.PARAM_API_LIMIT] = AppConst.DEFAULT_ITEM_PER_PAGE.toString()
            requestParam[ApiConst.PARAM_API_USER_ID] = userId.toString()
            requestParam[ApiConst.PARAM_API_LASTEST_ID] = myPageViewModel.lastestId
            myPageViewModel.requestListPostUser(requestParam)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun observerListPostUser() {
        myPageViewModel.listPostUserResult.observe(viewLifecycleOwner, {
            try {
                if (it.status != ApiResult.Status.CONSUMED) {
                    if (it.status == ApiResult.Status.LOADING) {
                        if (shareViewModel.isNeedRefreshPostDataInMyPage.value == true) {
                            mAdapterPostUser?.notifyDataSetChanged()
                        }
                    }
                    personalPageViewModel.showOrHideLoading(it)
                    if (it.status == ApiResult.Status.SUCCESS) {
                        getBinding()?.apply {
                            ltRetry.clRetryRoot.visibility = View.GONE
                        }
                        if (it.data?.response is ListPostUserResponse) {
                            (it.data.response as ListPostUserResponse).let { response ->
                                stopShimmerLayout()
                                myPageViewModel.syncPageNumberMyPost()
                                response.total?.apply {
                                    try {
                                        myPageViewModel.totalMyPost = this.toInt()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                response.data?.apply {
                                    try {
                                        response.getListData()?.let { list ->
                                            totalLoadMoreResult = list.size
                                            shareViewModel.isNeedRefreshPostDataInMyPage.value = false

                                            val startRange = if (myPageViewModel.listMyPost.size == 0) 0 else (myPageViewModel.listMyPost.size)
                                            showOrHideLoadingInItem(false)
                                            val itemInsertCount = list.size
                                            myPageViewModel.listMyPost.addAll(list)
                                            mAdapterPostUser?.notifyItemRangeInserted(
                                                startRange,
                                                itemInsertCount
                                            )
                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                if (response.getListData()?.size?:0 > 0){
                                    myPageViewModel.lastestId = response.getListData()?.get(response.getListData()?.size!!-1)?.id.toString()
                                }


                            }
                        }
                        personalPageViewModel.isShowLoading.value = false
                    }
                }
                it.status = ApiResult.Status.CONSUMED
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun prepareList() {
        try {
            mAdapterPostUser =
                AdapterMyPost(myPageViewModel.listMyPost, object : AdapterMyPost.OnClickItemListener {
                    override fun onClickMenu(entityMyPage: ListPostUserData?, position: Int?) {
                        this@FrmPersonalPage.position = position
                    }

                    override fun onClickItem(entityMyPage: ListPostUserData?, position: Int) {
                        try {
                            entityMyPage?.let {
                                mActivity?.showDetail(it.id, it, position, itemDeleteListener = {position:Int ->deleteItemFrmDetailCallBack(position)},itemHideListener = {})
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onClickLike(entityNew: ListPostUserData?, position: Int) {
                        entityNew?.id?.let { callApiPostLike(it,position) }
                    }

                    override fun onClickComment(entityNew: ListPostUserData?,position: Int) {
                        postId = entityNew?.id
                        entityNew?.id?.let { showDialogComment(it,position) }
                    }

                    override fun onClickShare(entityNew: ListPostUserData?,position: Int) {
                        entityNew?.let {
                            mActivity?.showDialogShare(entityNew,onClickShareListener = {content:String ->onClickShareListener(content)})
                            authViewModel.positionPostShareNeedNotifyChange(postIdShareParam = it.id,positionPostShareParam = position,currentFragmentParam = AppConst.FRM_PERSONAL_PAGE)
                        }
                        EventBus.getDefault().post("dialogShare")
                    }
                }, mActivity?.getUserId(),false)

            getBinding()?.rcPersonalPage?.apply {
                adapter = mAdapterPostUser
                val ltManager = LinearLayoutManager(mActivity)
                layoutManager = ltManager
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (!myPageViewModel.isCallingApiGetListMyPost && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            val totalItemCount: Int = mAdapterPostUser?.itemCount ?: 0
                            if (totalItemCount < myPageViewModel.totalMyPost) {
                                var firstVisibleItem = 0
                                recyclerView.layoutManager?.apply {
                                    if (this is LinearLayoutManager) {
                                        firstVisibleItem = this.findFirstVisibleItemPosition()
                                    }
                                }
                                val visibleItemCount = recyclerView.childCount
                                val lastInScreen = firstVisibleItem + visibleItemCount
                                if (lastInScreen > 0 && lastInScreen >= totalItemCount) {
                                    myPageViewModel.pageNumberMyPost.value = 1
                                }
                            }
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun deleteItemFrmDetailCallBack(position: Int){
        mAdapterPostUser?.removeItem(position)
    }

    //like
    private fun callApiPostLike(idPost: Int,position: Int) {
        try {
            authViewModel.positionPostShareAndLike = position

            val requestParam = mActivity!!.getRequestParamWithToken()
            val userId = PrefManager.getInstance(requireContext()).getString(PrefConst.PREF_USER_ID)
            userId?.let {
                requestParam[ApiConst.PARAM_API_USER_ID] = userId
            }
            requestParam[ApiConst.PARAM_API_POST_ID] = idPost.toString()

            likeViewModel.requestLike(requestParam)
        } catch (e: Exception) {
        }
    }

    private fun observerLike() {
        try {
            likeViewModel.likeResult.observe(viewLifecycleOwner, {
                if (it.status !=ApiResult.Status.CONSUMED){
                    myPostViewModel.showOrHideLoading(it)
                    if (it.status == ApiResult.Status.SUCCESS) {
                        if (it.data?.response is ResponseLike.LikeResponse) {
                            (it.data.response as ResponseLike.LikeResponse).let {
                                mAdapterPostUser?.apply {
                                    for (i in 0 until  myPageViewModel.listMyPost.size) {
                                        if ( myPageViewModel.listMyPost[i].id == it.post_id) {
                                            myPageViewModel.listMyPost[i].post_likes = it.liked
                                            myPageViewModel.listMyPost[i].like_count = it.like_count
                                            if (it.liked==true) myPageViewModel.numberLike.value=myPageViewModel.numberLike.value?.plus(1) //   like post, number plus 1
                                            else myPageViewModel.numberLike.value=myPageViewModel.numberLike.value?.minus(1) // unlike post,number like minus 1
                                            notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                myPostViewModel.isNeedShowErr.observe(viewLifecycleOwner,{
                    when(it.status){
                        ApiResult.Status.ERROR_DELETE_POST ->{
                            val dismissListener: DialogInterface.OnDismissListener = DialogInterface.OnDismissListener {
                                mAdapterPostUser?.removeItem(authViewModel.positionPostShareAndLike)
                            }
                            mActivity?.showDialogErrorDelete(resources.getString(R.string.titlePostNoLongerExists),resources.getString(R.string.msgPostNoLongerExists),dismissListener)

                        }
                        ApiResult.Status.ERROR,ApiResult.Status.ERROR_TOKEN,ApiResult.Status.ERROR_NETWORK->{
                            shareViewModel.isNeedShowErr.value=it
                        }
                    }
                })
                // show loading
                myPostViewModel.isShowLoading.observe(viewLifecycleOwner,{
                    shareViewModel.isShowLoading.value=it
                })
                it.status=ApiResult.Status.CONSUMED
            })

        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    private fun observerSharePost(){
        authViewModel.postShareResult.observe(viewLifecycleOwner,{
            try {
                when(it.status){
                    ApiResult.Status.SUCCESS->{
                        if (it.data?.response is ResponseShares.ShareResponse){
                            (it.data.response as ResponseShares.ShareResponse).let {
                                mAdapterPostUser?.apply {
                                    listMyPost[authViewModel.positionPostShareAndLike].post_shares = true
                                    listMyPost[authViewModel.positionPostShareAndLike].share_count=it.share_count
                                    notifyItemChanged(authViewModel.positionPostShareAndLike)
                                }
                            }
                        }
                        mActivity?.showDialogNotify(resources.getString(R.string.str_sharefb_success))
                    }
                    ApiResult.Status.ERROR,ApiResult.Status.ERROR_TOKEN->shareViewModel.isNeedShowErr.value = ApiNoticeEntity(it.status, it.message)
                    ApiResult.Status.ERROR_NETWORK->shareViewModel.isNeedShowErr.value == ApiNoticeEntity(it.status,null)
                    ApiResult.Status.ERROR_DELETE_POST->{
                        val dismissListener: DialogInterface.OnDismissListener = DialogInterface.OnDismissListener {
                            mAdapterPostUser?.removeItem(authViewModel.positionPostShareAndLike)
                        }
                        mActivity?.showDialogErrorDelete(resources.getString(R.string.titlePostNoLongerExists),resources.getString(R.string.msgPostNoLongerExists),dismissListener)
                    }

                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        })
    }
//comment
    private fun showDialogComment(postId: Int,position: Int) {
        dialogComment = DialogComment()
        dialogComment!!.show(childFragmentManager, "DialogComment")
        dialogComment?.apply {
            mActivity?.getAvatarId()?.let { setData(it, mActivity, postId, shareViewModel, this@FrmPersonalPage,position=position) }
        }
    }

    override fun onNumberComment(numberComment: Int?) {
        mAdapterPostUser?.apply {
            for (i in 0 until myPageViewModel.listMyPost.size) {
                if (myPageViewModel.listMyPost[i].id == postId) {
                    myPageViewModel.listMyPost[i].comment_count = numberComment
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onErrorDeletePost(position: Int?) {
        position?.let {
            mAdapterPostUser?.removeItem(position)
        }
    }

    private fun onClickShareListener(content:String){
        mActivity?.onShareLinkFacebook(content)
    }

    private fun showOrHideLoadingInItem(isShown : Boolean) {
        try {
            if (myPageViewModel.listMyPost.size > 0) {
                myPageViewModel.listMyPost.last().isShowLoading = isShown
                mAdapterPostUser?.notifyItemChanged(myPageViewModel.listMyPost.lastIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observerHandleAPILoadingAndErr() {
        try {
            personalPageViewModel.isNeedShowErr.observe(viewLifecycleOwner, {
                when (it.status) {
                    ApiResult.Status.ERROR_TOKEN -> {
                        stopShimmerLayout()
                        shareViewModel.isNeedShowErr.value = it
                        getBinding()?.apply {
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            clInteractive.visibility = View.VISIBLE
                        }
                    }
                    ApiResult.Status.ERROR, ApiResult.Status.ERROR_NETWORK -> {
                        stopShimmerLayout()
                        shareViewModel.isNeedShowErr.value = it
                        getBinding()?.apply {
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            clInteractive.visibility = View.VISIBLE
                        }
                        if (myPageViewModel.listMyPost.size > 0) {
                            showOrHideLoadingInItem(false)
                        } else {
                            getBinding()?.apply {
                                ltRetry.clRetryRoot.visibility = View.VISIBLE
                                ltRetry.tvErrMsg.text =
                                    if (it.status == ApiResult.Status.ERROR_NETWORK) this@FrmPersonalPage.getString(
                                        R.string.no_internet_connection
                                    ) else it.message
                            }
                        }
                        myPageViewModel.syncPageNumberMyPostFailed()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun observerFollowUser() {
        try {
            followViewModel.followUserResult.observe(viewLifecycleOwner, {
                try {
                    if (it.status != ApiResult.Status.CONSUMED) {
                        shareViewModel.showOrHideLoading(it)
                        if (it.status == ApiResult.Status.SUCCESS) {
                            if (it.data?.response is ResponseFollow.FollowResponse) {
                                (it.data.response as ResponseFollow.FollowResponse).let {
                                    getBinding()?.btnFollowUser?.visibility = View.VISIBLE
                                    if (it.follows == false){
                                        getBinding()?.btnFollowUser?.setText(R.string.lblFollow)
                                        getBinding()?.btnFollowUser?.isSelected = false
                                    }else{
                                        getBinding()?.btnFollowUser?.setText(R.string.lblUnFollow)
                                        getBinding()?.btnFollowUser?.isSelected = false
                                    }
                                }
                            }

                        }
                    }
                    it.status = ApiResult.Status.CONSUMED
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun startShimmerLayout(){
        getBinding()?.apply {
            rcShimmerFrameLayout.visibility = View.VISIBLE
            rcShimmerFrameLayout.startShimmer()
            rcPersonalPage.visibility = View.GONE
            if (ltRetry.clRetryRoot.isShown) ltRetry.clRetryRoot.visibility = View.GONE
        }

    }
    private fun stopShimmerLayout(){
        getBinding()?.apply {
            // scroll to old position before load more
            rcShimmerFrameLayout.visibility = View.GONE
            rcPersonalPage.visibility = View.VISIBLE

        }
    }
    private fun callApiFollow(idUser: Int?) {
        try {
            val requestParam = mActivity!!.getRequestParamWithToken()
            idUser?.let {
                requestParam[ApiConst.PARAM_API_FOLLOW_ID] = it.toString()

            }
            followViewModel.requestFollowUser(requestParam)
        } catch (e: Exception) {
        }
    }

    override fun getCurrentFragment(): Int {
        return AppConst.FRM_PERSONAL_PAGE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post("FrmPersonalPage")
    }
    private fun eventLoadMore(){
        val linearLayoutManager = getBinding()?.rcPersonalPage?.layoutManager as LinearLayoutManager
        getBinding()?.rcPersonalPage?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
               try {
                   totalItemCount = linearLayoutManager.itemCount
                   lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                   visibleItemCount = linearLayoutManager.childCount
                   offset = recyclerView.computeVerticalScrollOffset()
                   extent = recyclerView.computeVerticalScrollExtent()
                   range = recyclerView.computeVerticalScrollRange()
                   percentage = ((100.0 * offset!! / (range!!.toFloat() - extent!!)).toInt())
                  if (((totalItemCount!!) <= (lastVisibleItem!! + visibleThreshold)) && dy > 0 && percentage!! > AdapterHome.SCROLL_PERCENTAGE && totalLoadMoreResult == AppConst.DEFAULT_ITEM_PER_PAGE){
                      val nextPage = (myPageViewModel.pageNumberMyPost.value ?: 1) + 1
                      myPageViewModel.pageNumberMyPost.value = nextPage

                  }
               }catch (e:Exception){
                   e.printStackTrace()
               }
            }
        })
    }
    private fun checkAndCallApiListMyPostIfNeed() {
        try {
            if (myPageViewModel.isNeedGetListMyPost()) {
                callApiListPostUser()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun observerCountLike(){
        try {
            myPageViewModel.numberLike.observe(viewLifecycleOwner,{
                getBinding()?.tvNumberLike?.text = it.toString()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}