package brite.outdoor.ui.fragments.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.adapter.AdapterSearchWith
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseListUtensils
import brite.outdoor.data.api_entities.response.ResponseSearchLocations
import brite.outdoor.data.api_entities.response.ResponseSearchUtensils
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.local.room.SaveListLocationLocal
import brite.outdoor.data.local.room.SaveListUtensilsLocal
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmSearchUtensilsBinding
import brite.outdoor.di.AppModule
import brite.outdoor.ui.fragments.BaseFragment
import brite.outdoor.utils.KeyboardUtil
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.setSingleClick
import brite.outdoor.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class FrmSearchUtensils : BaseFragment<FrmSearchUtensilsBinding>() {
    private var saveListLocation : SaveListLocationLocal?=null
    private val listLocations= arrayListOf<ObjectSearch.ResultSearchEntity>()
    private val searchViewModel by viewModels<SearchViewModel>()
    private var saveListUtensils : SaveListUtensilsLocal?=null
    private var adapterSearch: AdapterSearchWith? = null
    private val listUtensils= arrayListOf<ObjectSearch.ResultSearchEntity>()
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmSearchUtensilsBinding {
        return FrmSearchUtensilsBinding.inflate(inflater, container, false)
    }
    companion object{
        const val FRM_SEARCH_UTENSILS=2
    }

    override fun initView(savedInstanceState: Bundle?) {
        val db = AppModule.provideDatabase(requireContext())
        saveListUtensils = SaveListUtensilsLocal(db.appDao())
        getBinding()?.mSearchViewModel = searchViewModel
        searchClick()
        observerSearchUtensils()
        getBinding()?.btnSearch?.setSingleClick {
            val key = getBinding()?.searchEdit?.text.toString()
            if (key.isNotEmpty()) {
                callApiSearch(key)
            }
            getBinding()?.searchEdit?.isCursorVisible=false
            KeyboardUtil.hideKeyboard(mActivity)
        }
        showDataUtensilsToList()
        listenerSearchView()
        listenerItemClicked()
    }

    override fun loadControlsAndResize(binding: FrmSearchUtensilsBinding?) {
        binding?.apply {
            tvDescFindWith.setTextSize(TypedValue.COMPLEX_UNIT_PX,getSizeWithScaleFloat(18.0))
            clFindBox.resizeHeight(getSizeWithScale(46.0))
            adapterSearch = AdapterSearchWith(getSizeWithScale(80.0), getSizeWithScale(140.0),requireActivity())
            rvSearchWith.apply {
                layoutManager =
                    LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)
                adapter = adapterSearch
            }
        }
    }

    private fun callApiSearch(key:String){
        val requestParam = HashMap<String,String>()
        requestParam[ApiConst.PARAM_API]= ApiConst.API_KEY
        requestParam[ApiConst.PARAM_API_SEARCH]=key
        val token= PrefManager.getInstance(requireContext()).getString(PrefConst.PREF_TOKEN)
        token?.let {
            requestParam[ApiConst.PARAM_API_TOKEN]=token
        }
        searchViewModel. requestSearchUtensils(requestParam)


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
    private fun observerSearchUtensils(){
        searchViewModel.apply {
            searchUtensilsResult.observe(viewLifecycleOwner,{
                try {
                    shareViewModel.showOrHideLoading(it)
                    if (it.status== ApiResult.Status.SUCCESS){
                        if (it.data?.response !is Boolean){
                            val list= arrayListOf<ObjectSearch.ResultSearchEntity>()
                            if (it.data?.response is ResponseSearchUtensils.SearchUtensilsResponse){
                                (it.data.response as ResponseSearchUtensils.SearchUtensilsResponse).let { response->
                                    listUtensils.clear()
                                    response.getListData()?.forEach { result ->
                                        listUtensils.add(ObjectSearch.ResultSearchEntity(ObjectSearch.ResultSearchEntity.TOOL,result))
                                    }
                                }
                                adapterSearch?.updateList(listUtensils)
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        }
    }
    private fun showDataUtensilsToList(){
        saveListUtensils?.getListUtensils()?.observe(viewLifecycleOwner,{
            try {
                val list= arrayListOf<ObjectSearch.ResultSearchEntity>()
                it.forEach { i->
                    list.add(ObjectSearch.ResultSearchEntity(ObjectSearch.ResultSearchEntity.TOOL,i))
                }
                adapterSearch?.updateList(list)
                adapterSearch?.initData(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }
    fun onRefreshData(){
        adapterSearch?.refreshData()
        getBinding()?.rvSearchWith?.scrollToPosition(0)
        getBinding()?.searchEdit?.text?.clear()
        getBinding()?.searchEdit?.clearFocus()
    }

    private fun listenerSearchView(){
        getBinding()?.searchEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterSearch?.filter?.filter(s)
                EventBus.getDefault().post("frgSearchUtensils")
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
    private fun listenerItemClicked(){
        adapterSearch?.setOnClickItem(object : AdapterSearchWith.OnClickItemListener{
            override fun onClicked(item: Any) {
                if (item is ResponseSearchUtensils.SearchUtensilsData){
                    item.apply {
                        mActivity!!.showListPostLocation(this.id.toString(),FRM_SEARCH_UTENSILS)
                    }
                }
                if (item is ResponseListUtensils.UtensilsData){
                    item.apply {
                        mActivity!!.showListPostLocation(this.id.toString(),FRM_SEARCH_UTENSILS)
                    }
                }
            }

        })
    }

    override fun getCurrentFragment(): Int {
        return 0
    }

    override fun finish() {

    }
}