package brite.outdoor.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import brite.outdoor.R
import brite.outdoor.constants.ApiConst
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ListPostUserResponse
import brite.outdoor.data.api_entities.response.ResponseFollow
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.LayoutDialogBottomItemFrmhomeBinding
import brite.outdoor.ui.dialog.DialogDelete
import brite.outdoor.ui.dialog.DialogNotice
import brite.outdoor.ui.dialog.DialogNotify
import brite.outdoor.utils.setSingleClick
import brite.outdoor.viewmodel.BottomSheetDialogFrmHomeViewModel
import brite.outdoor.viewmodel.ShareViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class BottomSheetDialogFrHome: BottomSheetDialogFragment() {

    private lateinit var binding: LayoutDialogBottomItemFrmhomeBinding

    private val viewModel by viewModels<BottomSheetDialogFrmHomeViewModel>()

    private val shareViewModel by activityViewModels<ShareViewModel>()

    private lateinit var callBackListener:CallBackListener

    private var requestParam : HashMap<String,String>?=null
    private var entityNew: ListPostUserData?=null
    private var position: Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.layout_dialog_bottom_item_frmhome,container,false)
        this.isCancelable=true
        initView()
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations=R.style.MyDialogAnimation
    }
    @SuppressLint("SetTextI18n")
    private fun initView(){
        observerFollowUser()
        observerHidePost()
        entityNew?.state_follow?.let {
            when(it){
                ListPostUserResponse.STATE_USER -> {
                    binding.tvFollow.visibility = View.GONE
                    binding.tvBadReport.visibility=View.GONE
                    binding.tvHideActivity.text = this.resources.getString(R.string.lblDeleteActivity)
                    binding.tvEditPost.visibility = View.VISIBLE
                    binding.viewLine1.visibility=View.GONE
                }
                ListPostUserResponse.STATE_UNFOLLOW ->{
                    binding.tvFollow.text = this.resources.getString(R.string.lblFollow) +" @${entityNew?.user_name_created}"
                    binding.tvEditPost.visibility = View.GONE
                    binding.tvHideActivity.text = this.resources.getString(R.string.lblHideActivity)
                }
                ListPostUserResponse.STATE_FOLLOW ->{
                    binding.tvFollow.text = this.resources.getString(R.string.lblUnFollow) +" @${entityNew?.user_name_created}"
                    binding.tvEditPost.visibility = View.GONE
                    binding.tvHideActivity.text = this.resources.getString(R.string.lblHideActivity)
                }
            }
        }
        binding.tvEditPost.setSingleClick {
            callBackListener.editPost(entityNew)
            this.dismiss()
        }
        binding.tvCancel.setSingleClick {
            this.dismiss()
        }
        binding.tvFollow.setSingleClick {
            callApiFollow()
        }
        binding.tvBadReport.setSingleClick {
            callBackListener.badReport(entityNew,position)
        }
        binding.tvHideActivity.setSingleClick {
            entityNew?.state_follow?.let {
                when(it){
                    ListPostUserResponse.STATE_USER ->{
                        callBackListener.showDialogConfirmDelete(entityNew)
                        this.dismiss()
                    }
                    else -> callApiHidePost()
                }
            }
        }
    }
    fun setData(entityNew: ListPostUserData?, position: Int?){
        this.entityNew=entityNew
        this.position=position

    }
    fun onCallBackListener(callBackListener: CallBackListener){
        this.callBackListener=callBackListener
    }
    fun setRequestParam(requestParam:HashMap<String,String>){
        this.requestParam=requestParam
    }

    private fun callApiFollow(){
        try {
            requestParam?.let {
                it[ApiConst.PARAM_API_FOLLOW_ID]=entityNew?.created_id.toString()
                viewModel.requestFollowUser(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun callApiHidePost(){
        try {
            requestParam?.let {
                it[ApiConst.PARAM_API_POST_ID] = entityNew?.id.toString()
                viewModel.requestHidePost(it)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun callApiDeletePost(){
        try {
            requestParam?.let {
                viewModel.requestDeletePost(it,entityNew?.id.toString())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun observerFollowUser(){
        try {
            viewModel.followUserResult.observe(viewLifecycleOwner,{
                try {
                    if (it.status!= ApiResult.Status.CONSUMED) {
                        shareViewModel.showOrHideLoading(it)
                        if (it.status== ApiResult.Status.SUCCESS){
                            this.dismiss()
                            if (it.data?.response !is Boolean){
                                if (it.data?.response is ResponseFollow.FollowResponse){
                                    (it.data.response as ResponseFollow.FollowResponse).let {
//                                        callApiListPostFocus()
                                        val dialog= DialogNotify(requireContext())
                                        if (it.follows==true){
                                            val message = "${resources.getString(R.string.str_follow)} @${entityNew?.user_name_created}"
                                            dialog.show(message,false)
                                        }
                                        else{
                                            val message = "${resources.getString(R.string.str_un_follow)} @${entityNew?.user_name_created}"
                                            dialog.show(message,false)
                                        }
                                        callBackListener.followUnFollowSuccess(it,entityNew)
                                    }
                                }

                            }
                        } else if (it.status== ApiResult.Status.ERROR || it.status== ApiResult.Status.ERROR_TOKEN || it.status== ApiResult.Status.ERROR_NETWORK) this.dismiss()
                    }
                    it.status= ApiResult.Status.CONSUMED
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
        }
    }
    private fun observerHidePost(){
        try {
            viewModel.hidePostResult.observe(viewLifecycleOwner,{
                if (it.status!= ApiResult.Status.CONSUMED){
                    shareViewModel.showOrHideLoading(it)
                    if (it.status==ApiResult.Status.SUCCESS){
                        val dialog= DialogNotify(requireContext())
                        dialog.show(resources.getString(R.string.str_hide_post),false)
                        callBackListener.hidePostSuccess(position)
                    }else if(it.status== ApiResult.Status.ERROR || it.status== ApiResult.Status.ERROR_TOKEN || it.status== ApiResult.Status.ERROR_NETWORK) this.dismiss()
                }
                it.status= ApiResult.Status.CONSUMED
            })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    interface CallBackListener{
        fun followUnFollowSuccess(follow: ResponseFollow.FollowResponse?,entityNew: ListPostUserData?)
        fun badReport(entityNew: ListPostUserData?,position: Int?)
        fun hidePostSuccess(position: Int?)
        fun editPost(entityNew: ListPostUserData?)
        fun showDialogConfirmDelete(entityNew: ListPostUserData?)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post("bottomSheet")
    }

}
