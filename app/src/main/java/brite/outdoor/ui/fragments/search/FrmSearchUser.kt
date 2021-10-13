package brite.outdoor.ui.fragments.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.adapter.AdapterSearchWith
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ResponseFollow
import brite.outdoor.data.api_entities.response.ResponseSearchUser
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmSearchUserBinding
import brite.outdoor.ui.fragments.BaseFragment
import brite.outdoor.utils.KeyboardUtil
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.setSingleClick
import brite.outdoor.viewmodel.FollowViewModel
import brite.outdoor.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FrmSearchUser : BaseFragment<FrmSearchUserBinding>() {
    private val followViewModel by viewModels<FollowViewModel>()
    private val listLocations= arrayListOf<ObjectSearch.ResultSearchEntity>()
    private val searchViewModel by viewModels<SearchViewModel>()
    private var adapterSearch: AdapterSearchWith? = null
    private val listUser= arrayListOf<ObjectSearch.ResultSearchEntity>()
    override fun loadControlsAndResize(binding: FrmSearchUserBinding?) {
        binding?.apply {
            tvDescFindWith.setTextSize(TypedValue.COMPLEX_UNIT_PX,getSizeWithScaleFloat(18.0))
            clFindBox.resizeHeight(getSizeWithScale(46.0))
            adapterSearch = AdapterSearchWith(getSizeWithScale(46.0), getSizeWithScale(46.0),requireActivity())
            rvSearchWith.apply {
                layoutManager =
                    LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)
                adapter = adapterSearch
            }
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmSearchUserBinding {
       return FrmSearchUserBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        getBinding()?.mSearchViewModel = searchViewModel
        observerSearchUser()
        searchClick()
        getBinding()?.btnSearch?.setSingleClick {
            val key = getBinding()?.searchEdit?.text.toString()
            if (key.isNotEmpty()) {
                callApiSearch(key)
            }
            getBinding()?.searchEdit?.isCursorVisible=false
            KeyboardUtil.hideKeyboard(mActivity)
        }
        goToFrmPersonalPage()
        showDataUserList()
        observerFollowUser()
        followAndUnFollowUser()
    }
    private fun callApiSearch(key:String){
        val requestParam = HashMap<String,String>()
        requestParam[ApiConst.PARAM_API]= ApiConst.API_KEY
        requestParam[ApiConst.PARAM_API_SEARCH]=key
        val token= PrefManager.getInstance(requireContext()).getString(PrefConst.PREF_TOKEN)
        token?.let {
            requestParam[ApiConst.PARAM_API_TOKEN]=token
        }
        searchViewModel.requestSearchUser(requestParam)
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun searchClick() {
        getBinding()?.searchEdit?.setOnKeyListener { v, keyCode, event ->

            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val key = getBinding()?.searchEdit?.text.toString()
                if (key.isNotEmpty()) {
                    //  handler call api location,utensils
                    callApiSearch(key)
                }
                getBinding()?.searchEdit?.isCursorVisible=false
                KeyboardUtil.hideKeyboard(mActivity)
            }

            false
        }
        getBinding()?.searchEdit?.setOnTouchListener { _, _ ->
            getBinding()?.searchEdit?.isCursorVisible = true
            false
        }
    }

    private fun showDataUserList(){
        try {
            adapterSearch?.updateList(listUser)
            adapterSearch?.initData(listUser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observerSearchUser(){
        searchViewModel.apply {
            searchUserResult.observe(viewLifecycleOwner, {
                try {
                    shareViewModel.showOrHideLoading(it)
                    if (it.status==ApiResult.Status.SUCCESS){
                        if (it.data?.response !is Boolean){
                            if (it.data?.response is ResponseSearchUser.SearchUserResponse){
                                (it.data.response as ResponseSearchUser.SearchUserResponse).let { response->
                                    listUser.clear()
                                    response.getListData()?.forEach { result ->
                                        listUser.add(ObjectSearch.ResultSearchEntity(ObjectSearch.ResultSearchEntity.USER,result))
                                    }
                                }
                                adapterSearch?.updateList(listUser)
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
    }}
    private fun followAndUnFollowUser(){
        adapterSearch?.apply {
            setOnClickFollow(object : AdapterSearchWith.OnClickFollowListener{
                override fun onClickFollow(userData: ResponseSearchUser.SearchUserData) {
                    callApiFollow(userData.id)
                }

            })
        }
    }
    private fun observerFollowUser() {
        try {
            followViewModel.followUserResult.observe(viewLifecycleOwner, {
                try {
                    if (it.status != ApiResult.Status.CONSUMED) {
                        shareViewModel.showOrHideLoading(it)
                        if (it.status == ApiResult.Status.SUCCESS) {
                            if (it.data?.response !is Boolean) {
                                if (it.data?.response is ResponseFollow.FollowResponse) {
                                    (it.data.response as ResponseFollow.FollowResponse).let {
                                        for (i in 0 until listUser.size){
                                            if (listUser[i].content is ResponseSearchUser.SearchUserData){
                                                (listUser[i].content as ResponseSearchUser.SearchUserData).let {result->
                                                    if (result.id == it.follower_id){
                                                        if (it.follows==true) result.followed_flag = "1"
                                                        else result.followed_flag = "0"
                                                        adapterSearch?.notifyItemChanged(i)
                                                    }
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
    fun onRefreshData(){
        adapterSearch?.clearData()
        getBinding()?.rvSearchWith?.scrollToPosition(0)
        getBinding()?.searchEdit?.text?.clear()
        getBinding()?.searchEdit?.clearFocus()
    }
    private fun goToFrmPersonalPage(){
        adapterSearch?.apply {
            setOnClickItem(object : AdapterSearchWith.OnClickItemListener{
                override fun onClicked(item: Any) {
                   if (item is ResponseSearchUser.SearchUserData ){
                       item.apply {
                          this.id?.let { mActivity?.showPersonalPageOrMyPage(it) }
                       }
                   }
                }

            })
        }
    }

    override fun getCurrentFragment(): Int {
        return 0
    }

    override fun finish() {

    }
}