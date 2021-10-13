package brite.outdoor.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.AdapterSearchWith
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_IMAGE_PICK
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseSearchLocations
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.data.entities.LocationEntity
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.local.room.SaveListLocationLocal
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmSelectPlacesBinding
import brite.outdoor.di.AppModule
import brite.outdoor.ui.dialog.DialogExit
import brite.outdoor.ui.dialog.DialogFragmentAddImagesAvatar
import brite.outdoor.ui.fragments.select_place.FrmMap
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.PushPostViewModel
import brite.outdoor.viewmodel.SearchViewModel
import brite.outdoor.viewmodel.SelectPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class FrmSelectPlaces : BaseDialogFragment<FrmSelectPlacesBinding>(), View.OnClickListener {
    private val selectPlaceViewModel by viewModels<SelectPlaceViewModel>()
    private var saveListLocation : SaveListLocationLocal?=null
    private val listLocations= arrayListOf<ObjectSearch.ResultSearchEntity>()
    private val searchViewModel by viewModels<SearchViewModel>()
    private var adapterSearch: AdapterSearchWith? = null
    private val postViewModel by viewModels<PushPostViewModel>({ requireParentFragment() })
    private var imagePicker : ImagePicker?=null
    companion object{
        const val FRM_POST_LOCATION=1
        fun getInstances(imagePick:ImagePicker):DialogFragment{
            val dialog = FrmSelectPlaces()
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_IMAGE_PICK,imagePick)
            dialog.arguments=bundle
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply { 
             imagePicker = this.getParcelable(EXTRA_IMAGE_PICK)

        }
    }
    override fun loadControlsAndResize(binding: FrmSelectPlacesBinding?) {
        binding?.apply {
            val sizeButtonAdd = getSizeWithScale(52.0)
            val sizeButtonSearch = getSizeWithScale(40.0)
            setupTopNavigation(this)
            edtSearchPlace.resizeHeight(getSizeWithScale(46.0))
            btnSearch.resizeLayout(sizeButtonSearch, sizeButtonSearch)
            adapterSearch = AdapterSearchWith(getSizeWithScale(100.0), getSizeWithScale(140.0),requireActivity(),false)
            btnAdd.resizeLayout(sizeButtonAdd, sizeButtonAdd)
            rcPlace.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = adapterSearch
            }
            tvSuggest.resizeWidth(getSizeWithScale(140.0))
            tvSuggest.setTextSize(TypedValue.COMPLEX_UNIT_PX,getSizeWithScaleFloat(11.0))

            btnAdd.setOnClickListener(this@FrmSelectPlaces)
            btnTopNavButton.setOnClickListener(this@FrmSelectPlaces)
            btnSearch.setOnClickListener(this@FrmSelectPlaces)

        }

    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FrmSelectPlacesBinding {
        return FrmSelectPlacesBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        selectPlaceViewModel.locationSelected.observe(viewLifecycleOwner, {
            if (it!= null) {
                postViewModel.location.value = it
            }
        })
        val db = AppModule.provideDatabase(requireContext())
        saveListLocation = SaveListLocationLocal(db.appDao())
        showDataLocationToListLocal()
        searchClick()
        listenerSearchView()
        observerSearchLocation()
        listenerItemClicked()
        listenerFocusEdt()
//        getBinding()?.edtSearchPlace?.clearFocus()
//        KeyboardUtil.hideKeyboard(mActivity)
        getBinding()?.edtSearchPlace?.clearFocus();
        getBinding()?.edtSearchPlace?.isCursorVisible = false;


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

    private fun setupTopNavigation(frmSelectsPlaceBinding: FrmSelectPlacesBinding) {
        try {
            frmSelectsPlaceBinding.apply {
                clTopNav.resizeHeight(getSizeWithScale(56.0))
                btnTopNavButton.resizeLayout(getSizeWithScale(38.67), getSizeWithScale(38.67))
                tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))
                btnTopNavButton.setOnClickListener {
                    dismissAllowingStateLoss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
    private fun onShowPushPost(location:LocationEntity){
        try {
            if (parentFragment is DialogFragmentAddImagesAvatar){
                FrmPushPosts.getInstance(location,imagePicker,null,FRM_POST_LOCATION).show(childFragmentManager,"FrmPushPost")
            }else{
                EventBus.getDefault().post(location)
                dismissAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onBackListener(){
        try {
            if (parentFragment is FrmPushPosts){
                dismissAllowingStateLoss()
            }else{
                val title = resources.getString(R.string.titleConfirm)
                val message = resources.getString(R.string.msgConfirmExit)
                mActivity?.showDialogExit(title,message,object : DialogExit.CallbackListenerExit{
                    override fun onOk() {
                        if (parentFragment is DialogFragmentAddImagesAvatar){
                            (parentFragment as DialogFragmentAddImagesAvatar).apply {
                                this.dismissAllowingStateLoss()
                                if (this.parentFragment is FrmPushPosts){
                                    (this.parentFragment as FrmPushPosts).apply {
                                        this.dismissAllowingStateLoss()
                                    }
                                }
                            }
                        }else dismissAllowingStateLoss()
                    }

                    override fun onCancel() {}

                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        try {
            val requestParam = HashMap<String,String>()
            requestParam[ApiConst.PARAM_API]= ApiConst.API_KEY
            requestParam[ApiConst.PARAM_API_SEARCH]=key
            val token= PrefManager.getInstance(requireContext()).getString(PrefConst.PREF_TOKEN)
            token?.let {
                requestParam[ApiConst.PARAM_API_TOKEN]=token
            }
            searchViewModel. requestSearchLocations(requestParam)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

                                //clearFocus edt
                                KeyboardUtil.hideKeyboard(mActivity)
                                getBinding()?.edtSearchPlace?.clearFocus()

                                adapterSearch?.updateList(listLocations)
                                if (listLocations.size == 0){
                                    getBinding()?.tvNoLocation?.visibility = View.VISIBLE
                                    showTvSuggest()
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
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            if (!isClickAble()) return
            when(v?.id){
                R.id.btnAdd -> {
                    hideTvSuggest()
                    FrmMap(onShowPushPost={location:LocationEntity->onShowPushPost(location)}).show(this@FrmSelectPlaces.childFragmentManager,"DialogFragmentAddImages")
                }
                R.id.btnTopNavButton->onBackListener()
                R.id.btnSearch->{
                    getBinding()?.apply {
                        val key = edtSearchPlace.text.toString()
                        if (key.isNotEmpty()) {
                            callApiSearch(key)
                        }
                        edtSearchPlace.isCursorVisible=false
                        KeyboardUtil.hideKeyboard(mActivity)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun showTvSuggest(){
        try {
            getBinding()?.tvSuggest?.apply {
                if (visibility==View.GONE) {
                    visibility = View.VISIBLE
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show_tv_suggest))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun hideTvSuggest(){
        try {
            getBinding()?.tvSuggest?.apply {
                if (visibility==View.VISIBLE) {
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide_tv_suggest))
                    visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun listenerFocusEdt(){
        try {
            getBinding()?.edtSearchPlace?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)  {
                    hideTvSuggest()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}