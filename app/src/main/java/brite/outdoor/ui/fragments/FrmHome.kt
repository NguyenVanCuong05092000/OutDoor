package brite.outdoor.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import brite.outdoor.R
import brite.outdoor.adapter.HomePagerAdapter
import brite.outdoor.app.MyApplication
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.AppConst.Companion.DATE_FORMAT
import brite.outdoor.constants.AppConst.Companion.FRM_HOME
import brite.outdoor.constants.AppConst.Companion.FRM_POST_LOCATION
import brite.outdoor.constants.AppConst.Companion.FRM_POST_UTENSILS
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.local.room.SaveListLocationLocal
import brite.outdoor.data.local.room.SaveListUtensilsLocal
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmHomeBinding
import brite.outdoor.di.AppModule
import brite.outdoor.ui.dialog.DialogFragmentAddImagesAvatar
import brite.outdoor.ui.fragments.home.FrmFocus
import brite.outdoor.ui.fragments.home.FrmFollow
import brite.outdoor.ui.fragments.home.FrmNew
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.AuthViewModel
import brite.outdoor.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frm_home.*
import kotlinx.android.synthetic.main.frm_home.view.*
import kotlinx.android.synthetic.main.layout_bottom_navigation_home.*
import kotlinx.android.synthetic.main.layout_bottom_navigation_home.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FrmHome : BaseFragment<FrmHomeBinding>() {
    private var entityNews :ArrayList<ListPostUserResponse>? = null
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var saveListLocation : SaveListLocationLocal?=null
    private var saveListUtensils : SaveListUtensilsLocal?=null
    private var homePagerAdapter : HomePagerAdapter?=null
    private var fragmentNew: FrmNew?=null
    private var fragmentFollow: FrmFollow?=null
    private var fragmentFocus: FrmFocus?=null
    private var indicatorWidth:Int?=null
    private var userId: String = ""


    companion object{
        const val DIALOG_FRM_LOCATION=1
        const val DIALOG_FRM_UTENSILS=2
    }
    override fun loadControlsAndResize(binding: FrmHomeBinding?) {
        binding?.apply {
            rlTopNav.resizeHeight(getSizeWithScale(75.0))

            naviBottHome.btnHome.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
            naviBottHome.btnMyPage.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
            naviBottHome.btnNotification.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
            naviBottHome.btnNotificationUnread.resizeLayout(getSizeWithScale(18.0),getSizeWithScale(18.0))
            naviBottHome.btnNotificationUnread.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(10.0))

            naviBottHome.btnSearch.resizeLayout(getSizeWithScale(59.0),getSizeWithScale(59.0))
            naviBottHome.ivBackGround.resizeLayout(getSizeWithScale(375.0),getSizeWithScale(89.0))
            naviBottHome.btnAddHome.resizeLayout(getSizeWithScale(60.0),getSizeWithScale(60.0))
            naviBottHome.vFakeButton.resizeLayout(getSizeWithScale(72.0),getSizeWithScale(72.0))
            naviBottHome.btnHome.isChecked=true

            tab.resizeLayout(getSizeWithScale(302.0), getSizeWithScale(38.0))
            indicator2.resizeLayout(getSizeWithScale(302.0), getSizeWithScale(38.0))
            val rect = Rect()
            refreshLayout.getDrawingRect(rect)
            refreshLayout.setProgressViewOffset(false, 200, 350)

            naviBottHome.apply {

                btnAddHome.setOnClickListener {
                    showPopupWindow()
                }
                btnAddPlace.setSingleClickSwitchScreen{
                    if (authViewModel.isHaveLocalDataSelectPlace){
                        FrmPushPosts.getInstance(typeFragment = FRM_POST_LOCATION).show(childFragmentManager,"FrmPushPost")
                        hideBtnAddHomeBackToScreenHome()
                    }else{
                        DialogFragmentAddImagesAvatar.getInstance(DIALOG_FRM_LOCATION).show(this@FrmHome.childFragmentManager,"DialogFragmentAddImages")
                        hideBtnAddHomeBackToScreenHome()
                    }

                }
                btnAddUtensils.setSingleClickSwitchScreen {
                    if (authViewModel.isHaveLocalDataSelectUtensils){
                        FrmPushPosts.getInstance(typeFragment = FRM_POST_UTENSILS).show(childFragmentManager,"FrmPushPost")
                        hideBtnAddHomeBackToScreenHome()
                    }else{
                        DialogFragmentAddImagesAvatar.getInstance(DIALOG_FRM_UTENSILS).show(this@FrmHome.childFragmentManager,"DialogFragmentAddImages")
                        hideBtnAddHomeBackToScreenHome()
                    }

                }
            }

        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmHomeBinding {
        return FrmHomeBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView(savedInstanceState: Bundle?) {
        userId = PrefManager.getInstance(mActivity!!).getString(PrefConst.PREF_USER_ID).toString()
        authViewModel.getPostSelectPlaceContentInDB(userId)
        authViewModel.getPostSelectUtensilsContentInDB(userId)

        initViewPagerAdapter()
        val db = AppModule.provideDatabase(requireContext())
        saveListLocation = SaveListLocationLocal(db.appDao())
        saveListUtensils = SaveListUtensilsLocal(db.appDao())
        if (entityNews == null) entityNews = ArrayList()
        onListenerViewPager()
        mActivity?.callApiListLocation()
        observeListLocationUtensilsResult()
        onRefreshListener()
        onListenerTabLayout()
        onListenerNaviBottom()
        mActivity?.callApiRegisterTokenPush()
        observerNotifyUnread()
        observerNeedUpdateDataPostDataInFrmHot()
        observerNeedUpdateDataPostInFrmNews()
        observerNeedRefreshDataPostInFrmHot()
        observerNeedRefreshDataPostInFrmNews()

    }
    private fun initViewPagerAdapter(){
        fragmentNew = FrmNew()
        fragmentFollow = FrmFollow()
        fragmentFocus= FrmFocus()
        val listFragment= arrayListOf<Fragment>()
        fragmentFocus?.let {
            listFragment.add(it)
        }
        fragmentNew?.let {
            listFragment.add(it)
        }
        fragmentFollow?.let {
            listFragment.add(it)
        }
        val listFragmentTitle = arrayListOf<String>()
        listFragmentTitle.add(resources.getString(R.string.lblFocus))
        listFragmentTitle.add(resources.getString(R.string.lblNews))
        listFragmentTitle.add(resources.getString(R.string.lblFollow))

        homePagerAdapter= mActivity?.supportFragmentManager?.let { HomePagerAdapter(it,listFragment,listFragmentTitle) }
        getBinding()?.viewPager?.adapter=homePagerAdapter
        getBinding()?.viewPager?.offscreenPageLimit = 3
        getBinding()?.tab?.setupWithViewPager(getBinding()?.viewPager)
        //Determine indicator width at runtime
        getBinding()?.tab?.post {
            indicatorWidth = getBinding()?.tab?.let { getBinding()?.tab?.width?.div(it.tabCount) }

            //Assign new width
            val indicatorParams = getBinding()?.indicator?.layoutParams as FrameLayout.LayoutParams
            if (indicatorWidth != null) {
                indicatorParams.width = indicatorWidth as Int
                indicatorParams.height = getSizeWithScale(38.0)
            }
            getBinding()?.indicator?.layoutParams = indicatorParams
        }
        homeViewModel.currentTab = getBinding()?.viewPager?.currentItem?:-1
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun observeListLocationUtensilsResult(){
        authViewModel.listLocationResult.observe(viewLifecycleOwner,{
            try {
                if (it.status == ApiResult.Status.SUCCESS){
                    if (it.data?.response !is Boolean){
                        writeStringToLocal(PrefConst.PREF_LAST_SYNC,localTime())
                        if (it.data?.response is ResponseListLocation.LocationResponse){
                            (it.data.response as ResponseListLocation.LocationResponse).let { response ->
                                saveListLocation?.let { it1 -> response.getListData()?.let { it2 -> authViewModel.saveListLocationToLocal(it1, it2) } }
                                response.url_prefix?.let { it ->
                                    writeStringToLocal(PrefConst.PREF_PATH_IMAGE_LIST_LOCATION,it)
                                }
                            }
                        }


                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })

        authViewModel.listUtensilsResult.observe(viewLifecycleOwner,{
            try {
                if (it.status==ApiResult.Status.SUCCESS){
                    if (it.data?.response !is Boolean){
                        writeStringToLocal(PrefConst.PREF_LAST_SYNC,localTime())
                        if (it.data?.response is ResponseListUtensils.UtensilsResponse){
                            (it.data.response as ResponseListUtensils.UtensilsResponse).let { response ->
                                saveListUtensils?.let { it1 -> response.getListData()?.let { it2 -> authViewModel.saveListUtensilsToLocal(it1, it2) } }
                                response.url_prefix?.let { it ->
                                    writeStringToLocal(PrefConst.PREF_PATH_IMAGE_LIST_UTENSILS,it)
                                }

                            }
                        }
                    }

                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        })
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun localTime(): String {
        try {
            val time = System.currentTimeMillis()
            val sdf = SimpleDateFormat(DATE_FORMAT)
            sdf.timeZone = TimeZone.getTimeZone("GMT+7")
            return sdf.format(Date(time))
        } catch (e: Exception) {
        }
        return ""
    }
    private fun writeStringToLocal(key:String,value:String){
        try {
            PrefManager.getInstance(mActivity!!).writeString(key,value)
        } catch (e: Exception) {
        }
    }

    private fun onListenerTabLayout(){
        getBinding()?.apply {
            tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    when (viewPager.currentItem){
                        0 ->  fragmentFocus?.refreshAndScrollToFirstItem()
                        1 -> fragmentNew?.refreshAndScrollToFirstItem()
                        2 -> fragmentFollow?.refreshAndScrollToFirstItem()
                    }
                }

            })
        }
    }
    private fun onListenerViewPager(){
        try {
            getBinding()?.viewPager?.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    val params = getBinding()?.indicator?.layoutParams as FrameLayout.LayoutParams
                    //Multiply positionOffset with indicatorWidth to get translation
                    val translationOffset: Float = (positionOffset + position) * (indicatorWidth?:0) + (getBinding()?.tab?.x?:0).toFloat()
                    params.leftMargin = translationOffset.toInt()
                    getBinding()?.indicator?.layoutParams = params
                }

                override fun onPageSelected(position: Int) {
                    homeViewModel.currentTab = position
                    when(position){
                        0 -> {
                            hideBtnAddHome()
                            fragmentFocus?.callApiListPostFocus(AppConst.DEFAULT_FIRST_PAGE)
                            fragmentFocus?.hideLtRetry()
                        }
                        1 ->{
                             hideBtnAddHome()
                            fragmentNew?.callApiListPostNew(AppConst.DEFAULT_FIRST_PAGE)
                            fragmentNew?.hideLtRetry()
                        }
                        2 ->{
                             hideBtnAddHome()
                            fragmentFollow?.callApiListPostUserFollow(AppConst.DEFAULT_FIRST_PAGE)
                            fragmentFollow?.hideLtRetry()
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    try {
                        if (getBinding()?.refreshLayout != null && !getBinding()?.refreshLayout?.isRefreshing!!) {
                            getBinding()?.refreshLayout?.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }

            })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun onRefreshListener() {
        getBinding()?.refreshLayout?.setOnRefreshListener {
            onRefresherData()
            getBinding()?.refreshLayout?.isRefreshing=false
        }

    }
    private fun showPopupWindow(){
        try {
            getBinding()?.naviBottHome.apply {
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
                    btnHome.isChecked=false
                }
                else{
                    btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_ic_add_home_unchecked))
                    btnHome.isChecked=true
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
                        naviBottHome.btnHome.isChecked=true
                        naviBottHome.btnMyPage.isChecked=false
                        naviBottHome.btnNotification.isChecked=false
                        naviBottHome.btnSearch.isChecked=false
                        naviBottHome.btnAddHome.isChecked=false

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

    override fun getCurrentFragment(): Int {
        return FRM_HOME
    }

    override fun finish() {

    }
    private fun hideBtnAddHomeBackToScreenHome(){
        getBinding()?.apply {
            naviBottHome.btnAddHome.post {
                if (naviBottHome.btnAddHome.isChecked){
                    naviBottHome.btnAddPlace.visibility=View.GONE
                    naviBottHome.btnAddUtensils.visibility=View.GONE
                    naviBottHome.btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_ic_add_home_unchecked))
                }

            }
        }
    }
    private fun hideBtnAddHome(isRefresh :Boolean = false){
        getBinding()?.apply {
            naviBottHome.btnAddHome.post {
                if (naviBottHome.btnAddHome.isChecked){
                    naviBottHome.btnAddPlace.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.hide_item_left))
                    naviBottHome.btnAddPlace.visibility=View.GONE

                    naviBottHome. btnAddUtensils.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.hide_item_right))
                    naviBottHome.btnAddUtensils.visibility=View.GONE

                    naviBottHome.btnAddHome.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_ic_add_home_unchecked))
                    naviBottHome.btnAddHome.isChecked=false

                    if (isRefresh) naviBottHome.btnHome.isChecked = true
                    else naviBottHome.btnHome.isChecked=!naviBottHome.btnHome.isChecked
                }
            }
        }
    }
    private fun onListenerNaviBottom(){
        try {
            getBinding()?.naviBottHome?.apply {
                var currentSelected : AppCompatRadioButton? = this.btnHome
                listOf<AppCompatRadioButton>(
                        this.btnHome, this.btnMyPage, this.btnNotification,this.btnSearch
                ).forEach {
                it.setOnClickListener { view ->
                    currentSelected?.isChecked = false
                    currentSelected = it
                    currentSelected?.isChecked = true
                when(view.id){
                        btnHome.id->{
                            onRefresherData()

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
                            mActivity?.showSearch()
                            hideBtnAddHomeBackToScreenHome()
                        }
                    }
                  }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun onRefresherData(){
        when(getBinding()?.viewPager?.currentItem){
            0->{
                //number page reset default when layout refresh
                fragmentFocus?.numberPage=AppConst.DEFAULT_FIRST_PAGE
                fragmentFocus?.callApiListPostFocus(AppConst.DEFAULT_FIRST_PAGE,isRefresher = true)
                fragmentFocus?.hideLtRetry()
            }
            1->{
                //number page reset default when layout refresh
                fragmentNew?.numberPage=AppConst.DEFAULT_FIRST_PAGE
                fragmentNew?.callApiListPostNew(AppConst.DEFAULT_FIRST_PAGE,isRefresher = true)
                fragmentNew?.hideLtRetry()
            }
            2->{
                //number page reset default when layout refresh
                fragmentFollow?.numberPage=AppConst.DEFAULT_FIRST_PAGE
                fragmentFollow?.callApiListPostUserFollow(AppConst.DEFAULT_FIRST_PAGE,isRefresher = true)
                fragmentFollow?.hideLtRetry()
            }
        }
         hideBtnAddHome(true)
    }
    private fun observerNotifyUnread(){
        authViewModel.countNotifyUnread.observe(viewLifecycleOwner,{
            getBinding()?.naviBottHome?.apply {
                if (it!=0){

                    btnNotificationUnread.visibility = View.VISIBLE
                    btnNotificationUnread.text = it.toString()
                }else{
                    btnNotificationUnread.visibility = View.GONE

                }

            }
        })
    }
    private fun observerNeedUpdateDataPostDataInFrmHot(){
        shareViewModel.isNeedUpdateDataWhenEditPostInHome_Hot.observe(viewLifecycleOwner,{
            if (it){
                try {
                    fragmentFocus?.adapter?.apply {
                        val position =  arrayListNews.withIndex().filter { (_, value)->
                            value.id==shareViewModel.editPost?.getDataEditPost()?.id
                        }.map { (i, _)->i }.single()
                         shareViewModel.editPost?.getDataEditPost()?.let {
                             arrayListNews[position]=it
                         }
                        notifyItemChanged(position)
                    }
                    shareViewModel.isNeedUpdateDataWhenEditPostInHome_Hot.value=false
                }catch (e:Exception){
                    e.printStackTrace()
                }

//                getBinding()?.viewPager?.currentItem=0
            }
        })
    }
    private fun observerNeedUpdateDataPostInFrmNews(){
        shareViewModel.isNeedUpdateDataWhenEditPostInHome_News.observe(viewLifecycleOwner,{
            if (it){
                try {
                    fragmentNew?.adapter?.apply {
                        val position =  arrayListNews.withIndex().filter { (_, value)->
                            value.id==shareViewModel.editPost?.getDataEditPost()?.id
                        }.map { (i, _)->i }.single()
                        shareViewModel.editPost?.getDataEditPost()?.let {
                            arrayListNews[position]=it
                        }
                        notifyItemChanged(position)
                    }
                    shareViewModel.isNeedUpdateDataWhenEditPostInHome_News.value=false
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        })
    }
    private fun observerNeedRefreshDataPostInFrmHot(){
        shareViewModel.isNeedRefreshPostDataInHome_Hot.observe(viewLifecycleOwner,{
            if(it){
                if (getBinding()?.viewPager?.currentItem==0) fragmentFocus?.refreshAndScrollToFirstItem()

            }
        })
    }
    private fun observerNeedRefreshDataPostInFrmNews(){
        shareViewModel.isNeedRefreshPostDataInHome_News.observe(viewLifecycleOwner,{
            if(it){
                if (getBinding()?.viewPager?.currentItem==1) fragmentNew?.refreshAndScrollToFirstItem()

            }
        })
    }

}