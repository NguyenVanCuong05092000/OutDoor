package brite.outdoor.ui.fragments.select_place

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.AdapterSearchWith
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseSearchLocations
import brite.outdoor.data.entities.LocationEntity
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.local.room.SaveListLocationLocal
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmListPlaceBinding
import brite.outdoor.di.AppModule
import brite.outdoor.ui.fragments.BaseDialogFragment
import brite.outdoor.utils.KeyboardUtil
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.utils.setSingleClick
import brite.outdoor.viewmodel.PushPostViewModel
import brite.outdoor.viewmodel.SearchViewModel
import brite.outdoor.viewmodel.SelectPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FrmListPlace(var onShowPushPost:(LocationEntity) -> Unit={_:LocationEntity->}) : BaseDialogFragment<FrmListPlaceBinding>(), View.OnClickListener {

    private var saveListLocation : SaveListLocationLocal?=null
    private val listLocations= arrayListOf<ObjectSearch.ResultSearchEntity>()
    private val searchViewModel by viewModels<SearchViewModel>()
    private val selectPlaceViewModel by viewModels<SelectPlaceViewModel>({ requireParentFragment() })
    private var adapterSearch: AdapterSearchWith? = null
    override fun loadControlsAndResize(binding: FrmListPlaceBinding?) {
        binding?.apply {
            edtSearchPlace.resizeHeight(getSizeWithScale(46.0))
            btnSearch.resizeLayout(getSizeWithScale(40.0), getSizeWithScale(40.0))
            adapterSearch = AdapterSearchWith(getSizeWithScale(46.0), getSizeWithScale(46.0),requireActivity())
            rcPlace.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = adapterSearch
            }
            btnSearch.setOnClickListener(this@FrmListPlace)
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmListPlaceBinding {
       return FrmListPlaceBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val db = AppModule.provideDatabase(requireContext())
        saveListLocation = SaveListLocationLocal(db.appDao())
        showDataLocationToListLocal()
        searchClick()
        listenerSearchView()
        observerSearchLocation()
        listenerItemClicked()
    }
    private fun showDataLocationToListLocal() {
        saveListLocation?.getListLocation()?.observe(viewLifecycleOwner, {
            try {
                val list = arrayListOf<ObjectSearch.ResultSearchEntity>()
                it.forEach { i ->
                    list.add(
                        ObjectSearch.ResultSearchEntity(
                            ObjectSearch.ResultSearchEntity.LOCATE,
                            i
                        )
                    )
                }
                selectPlaceViewModel.listLocation.observe(viewLifecycleOwner, {
                    it.addAll(list)
                    adapterSearch?.updateList(it)
                    adapterSearch?.initData(it)
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun searchClick() {
        getBinding()?.apply {
            edtSearchPlace.setOnKeyListener { v, keyCode, event ->

                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    KeyboardUtil.hideKeyboard(mActivity)
                    val key = edtSearchPlace.text.toString()
                    if (key.isNotEmpty()) {
                        callApiSearch(key)
                    }
                    edtSearchPlace.isCursorVisible=false
                }

                false
            }
            edtSearchPlace.setOnTouchListener { _, _ ->
                edtSearchPlace.isCursorVisible = true
                false
            }
        }
    }

    private fun listenerItemClicked(){
        try {
            adapterSearch?.setOnClickItem(object : AdapterSearchWith.OnClickItemListener{
                override fun onClicked(item: Any) {
                    if (item is ResponseSearchLocations.SearchLocationData){
                        item.apply {
                           val location  = LocationEntity(lat?.toDouble()?:0.0, lng?.toDouble()?:0.0, id.toString(), name)
//                            mActivity?.showPushPost()
//                            FrmPushPosts().show(childFragmentManager,"FrmPusPost")
//                            FrmPushPosts.getInstance(location).show(childFragmentManager,"FrmPushPost")
                            onShowPushPost(location)
                        }
                    }
                    if (item is ResponseListLocation.LocationData){
                        item.apply {
                            val location  = LocationEntity(lat?.toDouble() ?:0.0, lng?.toDouble() ?:0.0, id.toString(), name?:"")
//                            mActivity?.showPushPost()
//                            FrmPushPosts().show(childFragmentManager,"FrmPusPost")
                            onShowPushPost(location)
//                            FrmPushPosts.getInstance(location).show(childFragmentManager,"FrmPushPost")
                       
                        }
                    }
//                    if (parentFragment is FrmSelectPlaces) {
//                        (parentFragment as FrmSelectPlaces).dismissAllowingStateLoss()
//                    }
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
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
        searchViewModel. requestSearchLocations(requestParam)
    }

    private fun observerSearchLocation(){
        searchViewModel.apply {
            searchLocationsResult.observe(viewLifecycleOwner,{
                try {
                    shareViewModel.showOrHideLoading(it)
                    if (it.status== ApiResult.Status.SUCCESS ){
                        if(it.data?.response !is Boolean){
                            if (it.data?.response is ResponseSearchLocations.SearchLocationsResponse){
                                (it.data.response as ResponseSearchLocations.SearchLocationsResponse).let { response->
                                    listLocations.clear()
                                    response.getListData()?.forEach { result ->
                                        listLocations.add(
                                            ObjectSearch.ResultSearchEntity(
                                                ObjectSearch.ResultSearchEntity.LOCATE,result))
                                    }
                                }
                                selectPlaceViewModel.listLocation.observe(viewLifecycleOwner, Observer {
                                    it.clear()
                                    it.addAll(listLocations)
                                })
                                adapterSearch?.updateList(listLocations)
                                if (listLocations.size == 0){
                                    getBinding()?.tvNoLocation?.visibility = View.VISIBLE
                                }else{
                                    getBinding()?.tvNoLocation?.visibility = View.GONE
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        }
    }


    private fun listenerSearchView(){
        getBinding()?.edtSearchPlace?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterSearch?.filter?.filter(s)
                getBinding()?.tvNoLocation?.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun onClick(v: View?) {
        try {
            if (!isClickAble())return
            when(v?.id){
                R.id.btnSearch->{
                    val key = getBinding()?.edtSearchPlace?.text.toString()
                    if (key.isNotEmpty()) {
                        callApiSearch(key)
                    }
                    getBinding()?.edtSearchPlace?.isCursorVisible=false
                    KeyboardUtil.hideKeyboard(mActivity)
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}