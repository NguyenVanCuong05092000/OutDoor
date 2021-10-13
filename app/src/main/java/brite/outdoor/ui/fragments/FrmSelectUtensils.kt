package brite.outdoor.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.AdapterSearchWith
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.AppConst.Companion.FRM_SELECT_UTENSILS
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_IMAGE_PICK
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseListUtensils
import brite.outdoor.data.api_entities.response.ResponseSearchLocations
import brite.outdoor.data.api_entities.response.ResponseSearchUtensils
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.entities.UtensilsEntity
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.local.room.SaveListUtensilsLocal
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmSelectUtensilsBinding
import brite.outdoor.di.AppModule
import brite.outdoor.ui.dialog.DialogExit
import brite.outdoor.ui.dialog.DialogFragmentAddImagesAvatar
import brite.outdoor.utils.KeyboardUtil
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.viewmodel.SearchViewModel
import brite.outdoor.viewmodel.SelectPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frm_select_places.*
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class FrmSelectUtensils : BaseDialogFragment<FrmSelectUtensilsBinding>(),View.OnClickListener {
    private val selectPlaceViewModel by viewModels<SelectPlaceViewModel>()
    private var saveListUtensils : SaveListUtensilsLocal?=null
    private var adapterSearch : AdapterSearchWith?=null
    private val searchViewModel by viewModels<SearchViewModel>()
    private val listUtensils= arrayListOf<ObjectSearch.ResultSearchEntity>()
    private var imagePicker : ImagePicker?=null
    companion object{
        const val  FRM_POST_UTENSILS=2
        fun getInstance(imagePicker: ImagePicker):DialogFragment{
            val dialog = FrmSelectUtensils()
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_IMAGE_PICK,imagePicker)
            dialog.arguments=bundle
            return  dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            imagePicker = this.getParcelable(EXTRA_IMAGE_PICK)
        }
    }
    override fun loadControlsAndResize(binding: FrmSelectUtensilsBinding?) {
        binding?.apply {
            val sizeButtonSearch = getSizeWithScale(40.0)
            setupNavigation(this)
            edtSearchPlace.resizeHeight(getSizeWithScale(46.0))
            btnSearch.resizeLayout(sizeButtonSearch,sizeButtonSearch)
            adapterSearch= AdapterSearchWith(getSizeWithScale(100.0),getSizeWithScale(140.0),requireContext(),false)
            rcPlace.apply {
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                adapter=adapterSearch
            }
            btnSearch.setOnClickListener(this@FrmSelectUtensils)
        }

    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmSelectUtensilsBinding {
        return FrmSelectUtensilsBinding.inflate(inflater,container,false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val db = AppModule.provideDatabase(requireContext())
        saveListUtensils = SaveListUtensilsLocal(db.appDao())
        showDataUtensilsToListLocation()
        searchClick()
        listenerSearchView()
        observerSearchUtensils()
        listenerItemClicked()
    }

    private fun showDataUtensilsToListLocation(){
        saveListUtensils?.getListUtensils()?.observe(viewLifecycleOwner, {
            try {
                val list = arrayListOf<ObjectSearch.ResultSearchEntity>()
                it.forEach { i ->
                    list.add(
                            ObjectSearch.ResultSearchEntity(
                                    ObjectSearch.ResultSearchEntity.TOOL,
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
    private fun setupNavigation(frmSelectPlaceUtensilsBinding: FrmSelectUtensilsBinding){
        try {
            frmSelectPlaceUtensilsBinding.apply {
                clTopNav.resizeHeight(getSizeWithScale(56.0))
                btnTopNavButton.resizeLayout(getSizeWithScale(38.67), getSizeWithScale(38.67))
                tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,getSizeWithScaleFloat(18.0))
                btnTopNavButton.setOnClickListener {
                   onBackListener()
                }
            }
        }catch (e:Exception){
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
    private fun observerSearchUtensils(){
        searchViewModel.apply {
            searchUtensilsResult.observe(viewLifecycleOwner,{
                try {
                    shareViewModel.showOrHideLoading(it)
                    if (it.status== ApiResult.Status.SUCCESS ){
                        if(it.data?.response !is Boolean){
                            if (it.data?.response is ResponseSearchLocations.SearchLocationsResponse){
                                (it.data.response as ResponseSearchLocations.SearchLocationsResponse).let { response->
                                    listUtensils.clear()
                                    response.getListData()?.forEach { result ->
                                        listUtensils.add(
                                                ObjectSearch.ResultSearchEntity(
                                                        ObjectSearch.ResultSearchEntity.TOOL,result))
                                    }
                                }
                                selectPlaceViewModel.listLocation.observe(viewLifecycleOwner, {
                                    it.clear()
                                    it.addAll(listUtensils)
                                })
                                adapterSearch?.updateList(listUtensils)
                                if (listUtensils.size == 0){
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
        if (!isClickAble()) return
        when(v?.id){
            R.id.btnTopNavButton->dismissAllowingStateLoss()
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
    }
    private fun listenerItemClicked(){
        try {
            adapterSearch?.setOnClickItem(object : AdapterSearchWith.OnClickItemListener{
                override fun onClicked(item: Any) {
                    if (item is ResponseSearchUtensils.SearchUtensilsData){
                        item.apply {
                            val utensils  = UtensilsEntity(this.id,this.name,this.description,this.imageName,this.cIndex)
                            onShowPushPost(utensils)
                        }
                    }
                    if (item is ResponseListUtensils.UtensilsData){
                        item.apply {
                            val utensils  = this.name?.let {
                                UtensilsEntity(this.id,
                                    it,this.description,this.imageName,this.cIndex)
                            }
                            if (utensils != null) {
                                onShowPushPost(utensils)
                            }

                        }
                    }
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun onShowPushPost(utensilsEntity: UtensilsEntity){
        if (parentFragment is FrmPushPosts){
            EventBus.getDefault().post(utensilsEntity)
            dismissAllowingStateLoss()
        }
        else{
            FrmPushPosts.getInstance(null,imagePicker,utensilsEntity,FRM_POST_UTENSILS).show(childFragmentManager,"FrmPushPost")
        }

    }
    private fun onBackListener(){
        if (parentFragment is FrmPushPosts){
            dismissAllowingStateLoss()
        }else{
            val title = resources.getString(R.string.titleConfirm)
            val message = resources.getString(R.string.msgConfirmExit)
            mActivity?.showDialogExit(title,message,object :DialogExit.CallbackListenerExit{
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

    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post(true)
    }


}