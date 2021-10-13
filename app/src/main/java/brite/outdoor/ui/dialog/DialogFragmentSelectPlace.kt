package brite.outdoor.ui.dialog

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.AdapterPlace
import brite.outdoor.adapter.AdapterSearchWith
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ResponseSearchLocations
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.local.room.SaveListLocationLocal
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmSelectPlaceBinding
import brite.outdoor.di.AppModule
import brite.outdoor.ui.activities.MainActivity
import brite.outdoor.utils.KeyboardUtil
import brite.outdoor.utils.ViewSize
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.viewmodel.SearchViewModel
import brite.outdoor.viewmodel.ShareViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogFragmentSelectPlace : DialogFragment() {
    private var saveListLocation : SaveListLocationLocal?=null
    private var adapterPlace: AdapterPlace? = null
    private val listLocations= arrayListOf<ObjectSearch.ResultSearchEntity>()
    private val searchViewModel by viewModels<SearchViewModel>()
    private var shareViewModel : ShareViewModel? = null
    private var adapterSearch: AdapterSearchWith? = null
    private var listPlace: ArrayList<String>? = null
    private var mActivity : MainActivity? = null
    companion object {
        private const val TAG = "TestDialogFragment"
    }

    private var binding: FrmSelectPlaceBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val db = AppModule.provideDatabase(requireContext())
        saveListLocation = SaveListLocationLocal(db.appDao())
        binding = FrmSelectPlaceBinding.inflate(inflater, container, false)
        loadControlsAndResize(binding)
        showDataLocationToList()
        searchClick(binding)
        listenerSearchView(binding)
        observerSearchLocation()
        return binding!!.root
    }

    fun loadControlsAndResize(binding: FrmSelectPlaceBinding?) {
        binding?.apply {
            setupTopNavigation(this)
            edtSearchPlace.resizeHeight(getSizeWithScale(46.0))
            btnSearch.resizeLayout(getSizeWithScale(40.0), getSizeWithScale(40.0))
            adapterSearch = AdapterSearchWith(getSizeWithScale(46.0), getSizeWithScale(46.0),requireActivity())
            rcPlace.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = adapterSearch
            }
        }
    }
    fun initData(activity: MainActivity?, shareViewModel : ShareViewModel?){
        mActivity = activity
        this.shareViewModel = shareViewModel
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun searchClick(frmSelectPlaceBinding: FrmSelectPlaceBinding?) {
        frmSelectPlaceBinding?.apply {
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
        searchViewModel. requestSearchLocations(requestParam)
    }

    private fun observerSearchLocation(){
        searchViewModel.apply {
            searchLocationsResult.observe(viewLifecycleOwner,{
                try {
                    shareViewModel?.showOrHideLoading(it)
                    if (it.status== ApiResult.Status.SUCCESS ){
                        if(it.data?.response !is Boolean){
                            if (it.data?.response is ResponseSearchLocations.SearchLocationsResponse){
                                (it.data.response as ResponseSearchLocations.SearchLocationsResponse).let { response->
                                    listLocations.clear()
                                    response.getListData()?.forEach { result ->
                                        listLocations.add(ObjectSearch.ResultSearchEntity(ObjectSearch.ResultSearchEntity.LOCATE,result))
                                    }
                                }
                                adapterSearch?.updateList(listLocations)
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        }
    }

    private fun setupTopNavigation(frmSelectPlaceBinding: FrmSelectPlaceBinding) {
        try {
            frmSelectPlaceBinding.apply {
                clTopNav.resizeHeight(getSizeWithScale(80.0))
                btnTopNavButton.resizeLayout(getSizeWithScale(38.67), getSizeWithScale(38.67))
                tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))
                btnTopNavButton.setOnClickListener { dismiss() }
            }
        } catch (e: Exception) {
        }

    }

    private fun showDataLocationToList() {
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
                adapterSearch?.updateList(list)
                adapterSearch?.initData(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun listenerSearchView(frmSelectPlaceBinding: FrmSelectPlaceBinding?){
        frmSelectPlaceBinding?.edtSearchPlace?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterSearch?.filter?.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
        //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue = resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }

    fun getSizeWithScale(sizeDesign: Double): Int {
        return (sizeDesign * getScaleValue()).toInt()
    }

    fun getSizeWithScaleFloat(sizeDesign: Double): Float {
        return (sizeDesign * getScaleValue()).toFloat()
    }

    fun getRealSize(sizeDesign: ViewSize): ViewSize {
        return ViewSize(sizeDesign.width * getScaleValue(), sizeDesign.height * getScaleValue())
    }
    //End Resize
}