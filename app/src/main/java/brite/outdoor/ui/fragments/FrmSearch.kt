package brite.outdoor.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import brite.outdoor.R
import brite.outdoor.adapter.AdapterSearchWith
import brite.outdoor.adapter.HomePagerAdapter
import brite.outdoor.app.MyApplication
import brite.outdoor.constants.AppConst.Companion.FRM_POST_LOCATION
import brite.outdoor.constants.AppConst.Companion.FRM_POST_UTENSILS
import brite.outdoor.constants.AppConst.Companion.FRM_SEARCH
import brite.outdoor.constants.ExtraConst
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.databinding.FrmSearchBinding
import brite.outdoor.ui.dialog.DialogFragmentAddImagesAvatar
import brite.outdoor.ui.fragments.my_page.FrmMyPage
import brite.outdoor.ui.fragments.search.FrmSearchLocation
import brite.outdoor.ui.fragments.search.FrmSearchUser
import brite.outdoor.ui.fragments.search.FrmSearchUtensils
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.AuthViewModel
import brite.outdoor.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class FrmSearch : BaseFragment<FrmSearchBinding>() {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var homePagerAdapter : HomePagerAdapter?=null
    private val listUser= arrayListOf<ObjectSearch.ResultSearchEntity>()
    private lateinit var frmSearchLocation : FrmSearchLocation
    private lateinit var frmSearchUtensils: FrmSearchUtensils
    private lateinit var frmSearchUser: FrmSearchUser
    override fun loadControlsAndResize(binding: FrmSearchBinding?) {
        binding?.apply {
            setupTopNavigation(this)
            clButtonContainer.resizeHeight(getSizeWithScale(46.0))
            val wButton = getSizeWithScale(93.0)
            val hButton = getSizeWithScale(27.0)
            btnSearchLocate.resizeLayout(wButton, hButton)
            btnSearchTool.resizeLayout(wButton, hButton)
            btnSearchUser.resizeLayout(wButton, hButton)

//            btnSearch.resizeLayout(getSizeWithScale(25.0), getSizeWithScale(44.0))

            naviBottomHome.btnHome.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
            naviBottomHome.btnMyPage.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
            naviBottomHome.btnNotification.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
            naviBottomHome.ivBackGround.resizeLayout(getSizeWithScale(375.0),getSizeWithScale(89.0))
//            btnNotification1.resizeLayout(getSizeWithScale(61.0),getSizeWithScale(87.0))

            naviBottomHome.btnSearch.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
//            naviBottHome.imgBgIcAddHome.resizeLayout(getSizeWithScale(71.0),getSizeWithScale(100.0))
            naviBottomHome.btnAddHome.resizeLayout(getSizeWithScale(60.0),getSizeWithScale(60.0))
            naviBottomHome.vFakeButton.resizeLayout(getSizeWithScale(72.0),getSizeWithScale(72.0))
            naviBottomHome.btnSearch.isChecked=true
            naviBottomHome.btnNotificationUnread.resizeLayout(getSizeWithScale(18.0),getSizeWithScale(18.0))
            naviBottomHome.btnNotificationUnread.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(10.0))

            naviBottomHome.apply {
                btnAddHome.setOnClickListener {
                    showPopupWindow()
                }
                btnAddPlace.setSingleClickSwitchScreen {
                    if (authViewModel.isHaveLocalDataSelectPlace){
                        FrmPushPosts.getInstance(typeFragment = FRM_POST_LOCATION).show(childFragmentManager,"FrmPushPost")
                        hideBtnAddHomeBackToScreenHome()
                    }else{
                        DialogFragmentAddImagesAvatar.getInstance(FrmHome.DIALOG_FRM_LOCATION).show(this@FrmSearch.childFragmentManager,"DialogFragmentAddImages")
                        hideBtnAddHomeBackToScreenHome()
                    }
                }
                btnAddUtensils.setSingleClickSwitchScreen {
                    if (authViewModel.isHaveLocalDataSelectUtensils){
                        FrmPushPosts.getInstance(typeFragment = FRM_POST_UTENSILS).show(childFragmentManager,"FrmPushPost")
                        hideBtnAddHomeBackToScreenHome()
                    }else{
                        DialogFragmentAddImagesAvatar.getInstance(FrmHome.DIALOG_FRM_UTENSILS).show(this@FrmSearch.childFragmentManager,"DialogFragmentAddImages")
                        hideBtnAddHomeBackToScreenHome()
                    }
                }
            }

            adapterSearch = AdapterSearchWith(getSizeWithScale(46.0), getSizeWithScale(46.0),requireActivity())
        }
    }
    private fun setupTopNavigation(frmSearchBinding: FrmSearchBinding) {
        frmSearchBinding.apply {
            clTopNav.resizeHeight(getSizeWithScale(56.0))
            tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))

        }
    }
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmSearchBinding {
        return FrmSearchBinding.inflate(inflater, container, false)
    }


    private val settingViewModel by viewModels<SearchViewModel>()
    private lateinit var adapterSearch: AdapterSearchWith
    override fun initView(savedInstanceState: Bundle?) {

        getBinding()?.mSettingViewModel = settingViewModel
        getBinding()?.lifecycleOwner = this
        settingViewModel.listResult.observe(viewLifecycleOwner, {
//            it?.let { list ->
////                adapterSearch.updateList(list)
//            }
        })
        observerSearch()
        hideKeyboard()
        initViewPagerAdapter()
        onListenerViewPager()
        onListenerNaviBottom()
        observerNotifyUnread()

    }

    private fun observerSearch(){
        settingViewModel.apply {
            searchType.observe(viewLifecycleOwner,{
                try {
                    adapterSearch.clearData()
                    when (it){
                        ObjectSearch.ResultSearchEntity.LOCATE -> {
                            getBinding()?.viewPager?.currentItem=0
                        }
                        ObjectSearch.ResultSearchEntity.TOOL -> {
                            getBinding()?.viewPager?.currentItem=1
                        }
                        ObjectSearch.ResultSearchEntity.USER -> {
                            getBinding()?.viewPager?.currentItem=2
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        }
    }
    private fun initViewPagerAdapter(){
        val listFragment= arrayListOf<Fragment>()
        frmSearchLocation = FrmSearchLocation()
        frmSearchUtensils = FrmSearchUtensils()
        frmSearchUser = FrmSearchUser()
        listFragment.add(frmSearchLocation)
        listFragment.add(frmSearchUtensils)
        listFragment.add(frmSearchUser)
        homePagerAdapter= childFragmentManager?.let { HomePagerAdapter(it,listFragment) }
        getBinding()?.viewPager?.adapter=homePagerAdapter
        getBinding()?.viewPager?.offscreenPageLimit = 3
    }

    private fun onListenerViewPager(){
        try {
            getBinding()?.viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    when(position){
                        0 ->{
                            settingViewModel.setSearchType(ObjectSearch.ResultSearchEntity.LOCATE)
                            hideBtnAddHome()
                        }
                        1 ->{
                            settingViewModel.setSearchType(ObjectSearch.ResultSearchEntity.TOOL)
                            hideBtnAddHome()
                        }
                        2 ->{
                            settingViewModel.setSearchType(ObjectSearch.ResultSearchEntity.USER)
                            hideBtnAddHome()
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    try {
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }

            })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun hideKeyboard(){
        getBinding()?.container?.setOnClickListener {
            KeyboardUtil.hideKeyboard(mActivity!!)
        }
    }

    override fun getCurrentFragment(): Int {
        return FRM_SEARCH
    }

    override fun finish() {
        mActivity?.closeMainFuncScreen(this)
        authViewModel.apply {
            if (!isUpdateLocaleFrmHome){
                mActivity?.showHome()
                authViewModel.isUpdateLocaleFrmHome = true
            }
        }

    }

    override fun isBackPreviousEnable(): Boolean {
        return true
    }

    override fun backToPrevious() {
        finish()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post(true)
    }
    private fun onListenerNaviBottom(){
        try {
            getBinding()?.naviBottomHome?.apply {
                var currentSelected = this.btnMyPage
                listOf<AppCompatRadioButton>(
                        this.btnHome, this.btnMyPage, this.btnNotification,this.btnSearch
                ).forEach {
                    it.setOnClickListener { view ->
                        currentSelected.isChecked = false
                        currentSelected = it
                        currentSelected.isChecked = true

                        when(view.id){
                            btnHome.id->{
                                finish()
                            }
                            btnMyPage.id->{
                                mActivity?.showMyPage()
                            hideBtnAddHomeBackToScreenHome()
                            }
                            btnNotification.id->{
                                mActivity?.showNotification()
                                hideBtnAddHomeBackToScreenHome()
                            }
                           btnSearch.id->{
//                            mActivity?.showSearch()
//                            hideBtnAddHomeBackToScreenHome()
                               when(getBinding()?.viewPager?.currentItem){
                                   0->{
                                        frmSearchLocation.onRefreshData()
                                   }
                                   1->{
                                        frmSearchUtensils.onRefreshData()
                                   }
                                   2->{
                                        frmSearchUser.onRefreshData()
                                   }
                               }
                               hideBtnAddHome(true)
                            }
                        }

                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun hideBtnAddHomeBackToScreenHome(){
        try {
            getBinding()?.apply {
                naviBottomHome.btnAddHome.post {
                    if (naviBottomHome.btnAddHome.isChecked){
                        naviBottomHome.btnAddPlace.visibility=View.GONE
                        naviBottomHome.btnAddUtensils.visibility=View.GONE
                        naviBottomHome.btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_ic_add_home_unchecked))
                    }

                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    private fun hideBtnAddHome(isRefresh :Boolean = false){
        try {
            getBinding()?.apply {
                naviBottomHome.btnAddHome.post {
                    if (naviBottomHome.btnAddHome.isChecked){
                        naviBottomHome.btnAddPlace.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide_item_left))
                        naviBottomHome.btnAddPlace.visibility=View.GONE

                        naviBottomHome. btnAddUtensils.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide_item_right))
                        naviBottomHome.btnAddUtensils.visibility=View.GONE

                        naviBottomHome.btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_ic_add_home_unchecked))
                        naviBottomHome.btnAddHome.isChecked=false
                        if (isRefresh) naviBottomHome.btnSearch.isChecked = true
                       else naviBottomHome.btnSearch.isChecked=!naviBottomHome.btnSearch.isChecked
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    private fun showPopupWindow(){
        try {
            getBinding()?.naviBottomHome?.apply {
                btnAddPlace.apply {
                    if (isVisible){
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.hide_item_left))
                        visibility=View.GONE

                    }else{
                        visibility=View.VISIBLE
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.show_item_left))
                    }
                }
                btnAddUtensils.apply {
                    if (isVisible){
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.hide_item_right))
                        visibility=View.GONE

                    }else{
                        visibility=View.VISIBLE
                        startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.show_item_right))
                    }
                }
                if (btnAddHome.isChecked){
                    btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_ic_add_home_checked))
                    btnSearch.isChecked=false
                }
                else{
                    btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_ic_add_home_unchecked))
                    btnSearch.isChecked=true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe
    fun onEvent(event: Any) {
        when(event){
            is Boolean ->{
                event.apply {
                    getBinding()?.apply {
                        naviBottomHome.btnHome.isChecked=false
                        naviBottomHome.btnMyPage.isChecked=false
                        naviBottomHome.btnNotification.isChecked=false
                        naviBottomHome.btnSearch.isChecked=true
                        naviBottomHome.btnAddHome.isChecked=false
                    }
                }
            }
            is String ->{
                event.apply {
                    hideBtnAddHome()
                }
            }
        }
    }
    private fun observerNotifyUnread(){
        authViewModel.countNotifyUnread.observe(viewLifecycleOwner,{
            getBinding()?.naviBottomHome?.apply {
                if (it!=0){
                    btnNotificationUnread.visibility = View.VISIBLE
                    btnNotificationUnread.text = it.toString()
                }else{
                    btnNotificationUnread.visibility = View.GONE

                }
            }
        })
    }

}