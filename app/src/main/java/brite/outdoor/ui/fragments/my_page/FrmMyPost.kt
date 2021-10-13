package brite.outdoor.ui.fragments.my_page

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.adapter.AdapterMyPost
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.ApiNoticeEntity
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmMyPostBinding
import brite.outdoor.ui.dialog.DialogComment
import brite.outdoor.ui.dialog.DialogDelete
import brite.outdoor.ui.fragments.BaseFragment
import brite.outdoor.ui.fragments.BottomSheetDialogFrHome
import brite.outdoor.ui.fragments.FrmPushPosts
import brite.outdoor.utils.setTextSizePX
import brite.outdoor.viewmodel.AuthViewModel
import brite.outdoor.viewmodel.LikeViewModel
import brite.outdoor.viewmodel.MyPageViewModel
import brite.outdoor.viewmodel.MyPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frm_my_page.*
import kotlinx.android.synthetic.main.frm_my_post.*
import org.greenrobot.eventbus.EventBus


@AndroidEntryPoint
class FrmMyPost : BaseFragment<FrmMyPostBinding>(), DialogComment.OnChangeNumberComment, BottomSheetDialogFrHome.CallBackListener {
    private val myPageViewModel by viewModels<MyPageViewModel>({ requireParentFragment() })
    private val myPostViewModel by viewModels<MyPostViewModel>()
    private val likeViewModel by viewModels<LikeViewModel>()
    private var dialogComment: DialogComment? = null
    var mAdapterMyPost: AdapterMyPost? = null
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var position: Int? = null
    private var postId: Int? = null
    var totalLoadMoreResult: Int = AppConst.DEFAULT_TOTAL_RESULT
    private var statePositionScroll:Int=0
    private var isScrollToOldPosition = false
    private var positionRemoveItem = 0

    companion object {
        const val SCROLL_PERCENTAGE = 95
    }
    override fun loadControlsAndResize(binding: FrmMyPostBinding?) {
        getBinding()?.apply {
            ltRetry.apply {
                tvErrMsg.setTextSizePX(getSizeWithScaleFloat(14.0))
                btnRetry.setOnClickListener {
                    if (parentFragment is FrmMyPage){
                        (parentFragment as FrmMyPage).apply {
                            callApiListMyPost()
                            callApiDetailUser()
                        }
                        this.clRetryRoot.visibility = View.GONE
                    }

                }
            }
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmMyPostBinding {
        return FrmMyPostBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        prepareList()
        observerListPostUser()
        observerHandleAPILoadingAndErr()
        observerDelete()
        observerLike()
        eventLoadMore()
        observerSharePost()
    }

    private fun prepareList() {
        mAdapterMyPost =
            AdapterMyPost(myPageViewModel.listMyPost, object : AdapterMyPost.OnClickItemListener {
                override fun onClickMenu(entityMyPage: ListPostUserData?, position: Int?) {
                    if (position != null) {
                        showBottomDialog(entityMyPage, position)
                        positionRemoveItem = position
                    }
//                    this@FrmMyPost.position = position
//                    showDialogDelete(entityMyPage?.id)

                }

                override fun onClickItem(entityMyPage: ListPostUserData?,position: Int) {
                    try {
                        entityMyPage?.let {
                            mActivity?.showDetail(
                                    it.id,
                                    it,
                                    position,
                                    itemDeleteListener = {position:Int ->deleteItemFrmDetailCallBack(position)},itemHideListener = {})
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
                        authViewModel.positionPostShareNeedNotifyChange(postIdShareParam = it.id,positionPostShareParam = position,currentFragmentParam = AppConst.FRM_MY_POST)
                    }
                    EventBus.getDefault().post("dialogShare")
                }
            }, mActivity?.getUserId())

        getBinding()?.rvMyPost?.apply {
            adapter = mAdapterMyPost
            val ltManager = LinearLayoutManager(mActivity)
            layoutManager = ltManager
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (!myPageViewModel.isCallingApiGetListMyPost && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val totalItemCount: Int = mAdapterMyPost?.itemCount ?: 0
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
    }
    @SuppressLint("SetTextI18n")
    private fun showBottomDialog(entityNew: ListPostUserData?, position: Int) {
        try {
            mActivity?.showBottomSheet(entityNew, this@FrmMyPost, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun callApiDeletePost(idPost: Int?){
        try {
            val requestParam = mActivity!!.getRequestParamWithToken()
            requestParam[ApiConst.PARAM_API_POST_ID] = idPost.toString()
            myPageViewModel.requestDeletePost(requestParam, idPost.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun observerDelete() {
        myPageViewModel.deletePostResult.observe(viewLifecycleOwner, {
            try {
                shareViewModel.showOrHideLoading(it)
                if (it.status == ApiResult.Status.SUCCESS) {
                    mAdapterMyPost?.removeItem(positionRemoveItem)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun showDialogComment(postId: Int,position: Int) {
        dialogComment = DialogComment()
        dialogComment!!.show(childFragmentManager, "DialogComment")
        dialogComment?.apply {
            mActivity?.getAvatarId()?.let { setData(it, mActivity, postId, shareViewModel, this@FrmMyPost,position = position) }
        }
    }
    private fun observerListPostUser() {
        myPageViewModel.listPostUserResult.observe(viewLifecycleOwner, {
            try {
                if (it.status != ApiResult.Status.CONSUMED) {
                    if (it.status == ApiResult.Status.LOADING) {
                        if (shareViewModel.isNeedRefreshPostDataInMyPage.value == true) {
                            mAdapterMyPost?.notifyDataSetChanged()
                        }
                    }
                    myPostViewModel.showOrHideLoading(it)
                    if (it.status == ApiResult.Status.SUCCESS) {
                        myPageViewModel.isLoadMoreMyPost=false
                        getBinding()?.apply {
                            ltRetry.clRetryRoot.visibility = View.GONE
                            stopShimmerLayout()
                        }
                        if (it.data?.response is ListPostUserResponse) {
                            (it.data.response as ListPostUserResponse).let { response ->
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
//
                                            val startRange = if (myPageViewModel.listMyPost.size == 0) 0 else (myPageViewModel.listMyPost.size)

                                            showOrHideLoadingInItem(false)
                                            val itemInsertCount = list.size
                                            myPageViewModel.listMyPost.addAll(list)
                                            mAdapterMyPost?.notifyItemRangeInserted(
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
                    }
                }
                it.status = ApiResult.Status.CONSUMED
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun showOrHideLoadingInItem(isShown : Boolean) {
        if (myPageViewModel.listMyPost.size > 0) {
            myPageViewModel.listMyPost.last().isShowLoading = isShown
            mAdapterMyPost?.notifyItemChanged(myPageViewModel.listMyPost.lastIndex)
        }
    }

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
                                mAdapterMyPost?.apply {
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
                                mAdapterMyPost?.removeItem(authViewModel.positionPostShareAndLike)
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
                                mAdapterMyPost?.apply {
                                    val position =  listMyPost.withIndex().filter { (_, value)->
                                        value.id==it.post_id
                                    }.map { (i, _)->i }.single()
                                    listMyPost[position].post_shares=true
                                    listMyPost[position].share_count = it.share_count
                                    notifyItemChanged(position)
                                }
                            }
                        }
                        mActivity?.showDialogNotify(resources.getString(R.string.str_sharefb_success))
                    }
                    ApiResult.Status.ERROR,ApiResult.Status.ERROR_TOKEN->shareViewModel.isNeedShowErr.value = ApiNoticeEntity(it.status, it.message)
                    ApiResult.Status.ERROR_NETWORK->shareViewModel.isNeedShowErr.value == ApiNoticeEntity(it.status,null)

                }
            }catch (e:Exception){
                e.printStackTrace()
            }


        })
    }

    private fun observerHandleAPILoadingAndErr() {
        myPostViewModel.isNeedShowErr.observe(viewLifecycleOwner, {
            when (it.status) {

                ApiResult.Status.ERROR_TOKEN -> {
                    shareViewModel.isNeedShowErr.value = it
                    stopShimmerLayout()
                }
                ApiResult.Status.ERROR, ApiResult.Status.ERROR_NETWORK -> {
                        stopShimmerLayout()
                        shareViewModel.isNeedShowErr.value = it
                        if (myPageViewModel.listMyPost.size > 0) {
                            showOrHideLoadingInItem(false)
                        } else {
                            getBinding()?.apply {
                                ltRetry.clRetryRoot.visibility = View.VISIBLE
                                ltRetry.tvErrMsg.text =
                                    if (it.status == ApiResult.Status.ERROR_NETWORK) this@FrmMyPost.getString(
                                        R.string.no_internet_connection
                                    ) else it.message
                            }
                        }


//                    myPageViewModel.syncPageNumberMyPostFailed()
                }
            }
        })
    }
    private fun onClickShareListener(content:String){
        mActivity?.onShareLinkFacebook(content)
    }
    fun deleteItemFrmDetailCallBack(position: Int){
        mAdapterMyPost?.removeItem(position)
    }
    override fun onNumberComment(numberComment: Int?) {
        mAdapterMyPost?.apply {
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
            mAdapterMyPost?.removeItem(position)
        }
    }

    override fun getCurrentFragment(): Int {
        return 0
    }

    override fun finish() {

    }

    override fun followUnFollowSuccess(
        follow: ResponseFollow.FollowResponse?,
        entityNew: ListPostUserData?,
    ) {
    }

    override fun badReport(entityNew: ListPostUserData?, position: Int?) {
    }

    override fun hidePostSuccess(position: Int?) {
    }

    override fun editPost(entityNew: ListPostUserData?) {
        if (entityNew?.name_locations.isNullOrEmpty()){
            FrmPushPosts.getInstance(entityNew, typeFragment = AppConst.FRM_POST_UTENSILS, true).show(childFragmentManager,"FrmPushPost")
        }else{
            FrmPushPosts.getInstance(entityNew, typeFragment = AppConst.FRM_POST_LOCATION, true).show(childFragmentManager,"FrmPushPost")
        }
    }

    override fun showDialogConfirmDelete(entityNew: ListPostUserData?) {
        val dialogDelete: DialogDelete? = DialogDelete(requireContext())
        dialogDelete?.apply {
            show(true, resources.getString(R.string.msgDeletePost), resources.getString(R.string.msgConfirmDeletePost), object :DialogDelete.OnDeleteAgreeOrCancel{
                override fun onAgree() {
                    callApiDeletePost(entityNew?.id)
                }
            })
        }
    }

    fun startShimmerLayout(){
        getBinding()?.apply {
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
                rvMyPost.visibility = View.GONE
            if (ltRetry.clRetryRoot.isShown) ltRetry.clRetryRoot.visibility = View.GONE
        }

    }
    fun stopShimmerLayout(){
        getBinding()?.apply {

            // scroll to old position before load more
            try{
                if (isScrollToOldPosition){
                    nestedScrollView.postDelayed(Runnable {
                        nestedScrollView.scrollTo(0, statePositionScroll) //0 is x position

                    },100)
                    isScrollToOldPosition = false
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
           

            shimmerFrameLayout.visibility = View.GONE
            rvMyPost.visibility = View.VISIBLE

        }
    }
    private fun eventLoadMore(){

        getBinding()?.nestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            val verticalScrollableHeight = v.getChildAt(0).measuredHeight - v.measuredHeight
            val verticalPercentage = scrollY.toFloat()*100/ verticalScrollableHeight
            
            if (verticalPercentage>=SCROLL_PERCENTAGE && (scrollY-oldScrollY)>0 && totalLoadMoreResult == AppConst.DEFAULT_ITEM_PER_PAGE &&!myPageViewModel.isLoadMoreMyPost){ // load more
                // save position before load more
                isScrollToOldPosition = true
                statePositionScroll=scrollY
                myPageViewModel.isLoadMoreMyPost=true

                val nextPage = (myPageViewModel.pageNumberMyPost.value ?: 1) + 1
                myPageViewModel.pageNumberMyPost.value = nextPage

            }

        })
    }

}