package brite.outdoor.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.AdapterPeopleInteractive
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_LASTEST_ID
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_USER_ID
import brite.outdoor.constants.AppConst.Companion.FRM_LIST_PEOPLE_INTERACTIVE
import brite.outdoor.constants.AppConst.Companion.STATUS_PEOPLE_FOLLOW
import brite.outdoor.constants.AppConst.Companion.STATUS_PEOPLE_FOLLOWERS
import brite.outdoor.constants.ExtraConst
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_STATUS_PEOPLE_INTERACTIVE
import brite.outdoor.data.api_entities.response.ResponseFollow
import brite.outdoor.data.api_entities.response.ResponseFollowUser
import brite.outdoor.data.entities.BackStackData
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmListPeopleInteractiveBinding
import brite.outdoor.utils.resizeHeight
import brite.outdoor.viewmodel.FollowViewModel
import brite.outdoor.viewmodel.ListPeopleInteractiveViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class FrmListPeopleInteractive : BaseFragment<FrmListPeopleInteractiveBinding>(), View.OnClickListener {
    private var adapterPeopleInteractive: AdapterPeopleInteractive? = null
    private val listPeopleInteractiveViewModel by viewModels<ListPeopleInteractiveViewModel>()
    private val followViewModel by viewModels<FollowViewModel>()
    private var statusPeople: Int? = null
    companion object {
        fun getInstance(arrayList: ArrayList<BackStackData>?, status: Int): FrmListPeopleInteractive {
            val dialog = FrmListPeopleInteractive()
            val bundle = Bundle()
            bundle.putInt(EXTRA_STATUS_PEOPLE_INTERACTIVE,status)
            if (arrayList != null) bundle.putSerializable(ExtraConst.EXTRA_BACK_STACK, arrayList)
            dialog.arguments=bundle
            return dialog
        }
    }
    override fun loadControlsAndResize(binding: FrmListPeopleInteractiveBinding?) {
        binding?.apply {
            clTopNav.resizeHeight(getSizeWithScale(56.0))
            tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(15.0))
            statusPeople?.let {
                when(statusPeople){
                    STATUS_PEOPLE_FOLLOW->{tvTopNavTitle.text = resources.getString(R.string.str_title_frmListPeople_follow)}
                    STATUS_PEOPLE_FOLLOWERS->{tvTopNavTitle.text = resources.getString(R.string.str_title_frmListPeople_followers)}
                }
            }
            btnBack.setOnClickListener(this@FrmListPeopleInteractive)
        }
    }
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmListPeopleInteractiveBinding {
        return FrmListPeopleInteractiveBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            statusPeople = getInt(ExtraConst.EXTRA_STATUS_PEOPLE_INTERACTIVE)
        }
    }
    override fun initView(savedInstanceState: Bundle?) {
        initAdapter()
        observerFollowUser()
        if (statusPeople == STATUS_PEOPLE_FOLLOW){
            callApiListUserFollow()
            observerListUserFollow()
        }
        if (statusPeople == STATUS_PEOPLE_FOLLOWERS){
            callApiListFollower()
            observerListFollower()
        }
    }

    override fun isBackPreviousEnable(): Boolean {
        return true
    }

    override fun backToPrevious() {
        finish()
    }
    override fun finish() {
        mActivity?.closeListPeopleInteractive(this)
    }

    override fun onClick(v: View?) {
        try {
            if (!isClickAble()) return
            when(v?.id){
                R.id.btnBack-> finish()
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    private fun initAdapter(){
        getBinding()?.apply {
            adapterPeopleInteractive = AdapterPeopleInteractive(requireContext(), mActivity?.getUrlPrefixAvatar(), object : AdapterPeopleInteractive.OnClickItemListener{
                override fun onClickItem(entityFollowUser: ResponseFollowUser.FollowUserData?) {
                    entityFollowUser?.user_id?.let {
                        mActivity?.showPersonalPageOrMyPage(it)
                    }
                }

                override fun onClickFollow(entityFollowUser: ResponseFollowUser.FollowUserData?) {
                    callApiFollow(entityFollowUser?.user_id)
                }
            })
            rcPeopleInteractive.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterPeopleInteractive
            }
        }
    }

    private fun callApiListUserFollow(){
        try {
            val requestParam = mActivity!!.getRequestParamWithToken()
            requestParam[PARAM_API_USER_ID] = mActivity?.getUserId().toString()
            requestParam[PARAM_API_LASTEST_ID] = ""
            listPeopleInteractiveViewModel.requestListUserFollow(requestParam)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observerListUserFollow(){
        try {
            listPeopleInteractiveViewModel.listUserFollowResult.observe(viewLifecycleOwner, {
                shareViewModel.showOrHideLoading(it)
                if (it.status == ApiResult.Status.SUCCESS){
                    if (it.data?.response is ResponseFollowUser.FollowUserResponse) {
                        (it.data.response as ResponseFollowUser.FollowUserResponse).let {
                            adapterPeopleInteractive?.apply {
                                updateList(it.getListData())
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun callApiListFollower(){
        try {
            val requestParam = mActivity!!.getRequestParamWithToken()
            requestParam[PARAM_API_LASTEST_ID] = ""
            requestParam[PARAM_API_USER_ID] = mActivity?.getUserId().toString()
            listPeopleInteractiveViewModel.requestListFollower(requestParam)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observerListFollower(){
        try {
            listPeopleInteractiveViewModel.listFollowerResult.observe(viewLifecycleOwner, {
                shareViewModel.showOrHideLoading(it)
                if (it.status == ApiResult.Status.SUCCESS){
                    if (it.data?.response is ResponseFollowUser.FollowUserResponse) {
                        (it.data.response as ResponseFollowUser.FollowUserResponse).let {
                            adapterPeopleInteractive?.apply {
                                updateList(it.getListData())
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post(true)
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
                                        adapterPeopleInteractive?.apply {
                                            for (i in 0 until getListFollowUser()?.size!!){
                                                getListFollowUser()?.get(i).let { result->
                                                    if (result?.user_id == it.follower_id){
                                                        if (result?.followed_flag == 0)
                                                            result.followed_flag = 1
                                                        else if (result?.followed_flag == 1){
                                                            result.followed_flag = 0
                                                        }
                                                        notifyItemChanged(i)
                                                    }
                                                }
                                            }
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
        return FRM_LIST_PEOPLE_INTERACTIVE
    }
}