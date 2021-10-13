package brite.outdoor.ui.fragments

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Paint
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.AdapterPostContent
import brite.outdoor.constants.ApiConst
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_CONTENT
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.AppConst.Companion.FRM_POST_LOCATION
import brite.outdoor.constants.AppConst.Companion.FRM_POST_UTENSILS
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_EDIT_POST
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_IMAGE_PICK
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_IS_EDIT_POST
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_LOCATION_ENTITY
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_TYPE_FRAGMENT
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_UTENSILS_ENTITY
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ResponseEditPost
import brite.outdoor.data.entities.*
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmPushPostsBinding
import brite.outdoor.ui.dialog.*
import brite.outdoor.utils.*
import brite.outdoor.utils.compressor.*
import brite.outdoor.viewmodel.AuthViewModel
import brite.outdoor.viewmodel.PushPostViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FrmPushPosts : BaseDialogFragment<FrmPushPostsBinding>(), View.OnClickListener  {

    private var mAdapter: AdapterPostContent? = null

    private var idUser: String = ""
    private var mLat: Double? = null
    private var mLng: Double? = null
    private var locationId: String = ""
    private var locationName: String = ""
    private val pushPostViewModel by viewModels<PushPostViewModel>()
    private var listContent: ArrayList<PostContentEntity>? = null
    private var imagePicker : ImagePicker?=null
    private var utensilsEntity:UtensilsEntity?=null
    private var locationEntity:LocationEntity?=null
    private var typeFragment:Int=0
    private var entityPostNeedEdit: ListPostUserData?= null
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var isEditPost: Boolean = false
    private var listImageDelete: ArrayList<String?>? = ArrayList()
    private var nameAvatar: String? = null
    private var typeVideo : Int?=null
    private var isCheckPushPost = false
    private var language:Int = 1
    private fun showPopupAddImages() {
        try {
            val dlgAddImage = DialogFragmentAddImages()
            dlgAddImage.show(childFragmentManager, "DialogFragmentAddImages")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object{
        fun getInstance(location:LocationEntity?=null,imagePicker: ImagePicker?=null,utensils:UtensilsEntity?=null,typeFragment:Int): DialogFragment{
            val fragment = FrmPushPosts()
            val bundle = Bundle()
            imagePicker?.let {
                bundle.putParcelable(EXTRA_IMAGE_PICK,it)
            }
            location?.let {
                bundle.putParcelable(EXTRA_LOCATION_ENTITY,it)
            }
            utensils?.let {
                bundle.putParcelable(EXTRA_UTENSILS_ENTITY,it)
            }
            bundle.putInt(EXTRA_TYPE_FRAGMENT,typeFragment)
            fragment.arguments=bundle
            return fragment
        }
        fun getInstance(postNeedEdit: ListPostUserData?, typeFragment:Int, isEditPost: Boolean): DialogFragment{
            val fragment = FrmPushPosts()
            val bundle = Bundle()
            postNeedEdit?.let {
                bundle.putParcelable(EXTRA_EDIT_POST,it)
            }
            bundle.putInt(EXTRA_TYPE_FRAGMENT,typeFragment)
            bundle.putBoolean(EXTRA_IS_EDIT_POST, isEditPost)
            fragment.arguments=bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            locationEntity = this.getParcelable(EXTRA_LOCATION_ENTITY)
            imagePicker = this.getParcelable(EXTRA_IMAGE_PICK)
            utensilsEntity = this.getParcelable(EXTRA_UTENSILS_ENTITY)
            typeFragment = this.getInt(EXTRA_TYPE_FRAGMENT)
            entityPostNeedEdit = this.getParcelable(EXTRA_EDIT_POST)
            isEditPost = this.getBoolean(EXTRA_IS_EDIT_POST)
            pushPostViewModel.isEditPost = isEditPost
            locationId = locationEntity?.locationId.toString()
            locationName = locationEntity?.locationName.toString()
            mLat = locationEntity?.lat
            mLng = locationEntity?.long
            language = if (PrefManager.getInstance(requireContext()).getString(PrefConst.PREF_MULTI_LANGUAGE)=="vi") 1 else 2
        }
    }

    override fun finish() {
        showDialogCancel()
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmPushPostsBinding {
        return FrmPushPostsBinding.inflate(inflater, container, false)
    }

    override fun loadControlsAndResize(binding: FrmPushPostsBinding?) {
        binding?.apply {
            setupTopNavigation(this)
            setupContent(this)

            btnAddLocate.resizeHeight(getSizeWithScale(28.0))
            imgAddContent.resizeLayout(getSizeWithScale(20.0), getSizeWithScale(20.0))
            imgAvatar.resizeLayout(getSizeWithScale(40.0),getSizeWithScale(40.0))
            icPlace.resizeLayout(getSizeWithScale(18.0),getSizeWithScale(22.0))
            clTopNav.resizeHeight(getSizeWithScale(56.0))
            rlAddContent.resizeHeight(getSizeWithScale(46.0))

            imagePicker?.let {
                val uriImage = Uri.parse(it.uri)
                imgAvatar.setImageURI(uriImage)
            }
            btnDangBai.setOnClickListener(this@FrmPushPosts)
            btnAddLocate.setOnClickListener(this@FrmPushPosts)
            btnBack.setOnClickListener(this@FrmPushPosts)
            imgAvatar.setOnClickListener(this@FrmPushPosts)
            rlAddContent.setOnClickListener(this@FrmPushPosts)
//            btnAddLocate.setSingleClick {
//                if (typeFragment== FRM_POST_LOCATION) FrmSelectPlaces().show(childFragmentManager, "FrmPushPosts")
//                else if (typeFragment == FRM_POST_UTENSILS) FrmSelectUtensils().show(childFragmentManager, "FrmPushPosts")
//            }
//            rlAddContent.setSingleClick {
//                handleAddNewRecord()
//            }
//            btnBack.setSingleClick {
//                showDialogCancel()
//            }
//            imgAvatar.setSingleClick {
//                DialogFragmentAddImagesAvatar.getInstance(isChangeAvatar = true).show(childFragmentManager, "DialogFragmentAddImagesAvatar")
//            }

        }
    }
    private fun handlePushPost() {
        var msg = ""
        if (getBinding()?.edtTitle?.text.toString() == "") {
            msg += if (msg.isEmpty()) resources.getString(R.string.str_you_create_title) else resources.getString(R.string.str_create_title)
        }
        var isContentPost=false
        listContent?.forEach {
            if (it.content.isNotEmpty() || it.listImg.value?.size?:0 >0){
                isContentPost=true
            }
        }
        if (!isContentPost) msg=resources.getString(R.string.str_you_create_content)
        if (msg.isEmpty()) {
            if (pushPostViewModel.isEditPost){
                callApiEditPost()
            }else{
                callApiPushPost()
            }
        } else {
            mActivity?.showNotice(msg)
        }
    }

    private fun setEnableBtnPush(){
        try {
            getBinding()?.apply {
                if (edtTitle.text.isNullOrEmpty()){
                    btnDangBai.isEnabled = false
                    btnDangBai.setTextColor(requireContext().resources.getColor(R.color.colorBorder))
                }else{
                    btnDangBai.isEnabled = true
                    btnDangBai.setTextColor(requireContext().resources.getColor(R.color.colorMain))
                }
                edtTitle.addTextChangedListener(object : TextWatcher{
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (s.isNullOrEmpty()){
                            btnDangBai.isEnabled = false
                            btnDangBai.setTextColor(requireContext().resources.getColor(R.color.colorBorder))
                        }else{
                            btnDangBai.isEnabled = true
                            btnDangBai.setTextColor(requireContext().resources.getColor(R.color.colorMain))
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }

                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun handleAddNewRecord() {
        try {
            listContent?.add(PostContentEntity())
            val lastPosition = listContent?.lastIndex ?: -1
            if (lastPosition >= 0) {
                mAdapter?.notifyItemInserted(lastPosition)
                getBinding()?.rvPostContents?.scrollToPosition(lastPosition)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setupTopNavigation(frmPushPostsBinding: FrmPushPostsBinding) {
        try {
            frmPushPostsBinding.apply {
                tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))
                btnDangBai.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))
                btnBack.resizeLayout(getSizeWithScale(44.0), getSizeWithScale(56.0))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupContent(frmPushPostsBinding: FrmPushPostsBinding) {
        try {
            frmPushPostsBinding.apply {
                mAdapter = AdapterPostContent(
                    mActivity!!,
                    heightButton = getSizeWithScale(35.0),
                    sizeIconAddImages = getSizeWithScale(14.81),
                    sizeTextContent = getSizeWithScaleFloat(14.0),
                    minHeightContent = getSizeWithScale(50.0), {
                        rvPostContents.scrollToPosition(it)
                    }, {
                        pushPostViewModel.setContentSelected(it,typeFragment)
                        if (pushPostViewModel.isEditPost){
                            try {
                                entityPostNeedEdit?.getListContent()?.get(it.plus(1))?.listImg?.let { it1 ->
                                    if (!pushPostViewModel.listNameImage.isNullOrEmpty()) pushPostViewModel.listNameImage?.clear()
                                    pushPostViewModel.listNameImage?.addAll(it1)
                                }
                            } catch (e: Exception) {
                               e.printStackTrace()
                            }
                        }
                        showPopupAddImages()
                    }
                )
                rvPostContents.apply {
                    layoutManager =
                        LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)
                    adapter = mAdapter
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        try {
            setLatAndLng()
            setUtensils()
            pushPostViewModel.isHaveLocalData.observe(viewLifecycleOwner, {
                if (it) {
                    showDialogDeletePostIfNeed()
                }
            })
            observerListImageDelete()
            setEnableBtnPush()
            observerPushPost()
            observerEditPost()
            idUser = PrefManager.getInstance(mActivity!!).getString(PrefConst.PREF_USER_ID).toString()
            if (pushPostViewModel.isEditPost){
                entityPostNeedEdit?.apply {
                    if (typeFragment == FRM_POST_LOCATION){
                       val listConTent: MutableLiveData<ArrayList<PostContentEntity>> = MutableLiveData(arrayListOf())
                        for (i in 1 until this.getListContent()?.size!!) {
                            val imgPicker: MutableLiveData<ArrayList<ImagePicker>> =MutableLiveData(ArrayList())
                            for (j in 0 until this.getListContent()?.get(i)?.listImg?.size!!) {
                                val url = "${this.url_prefix}${
                                    this.getListContent()
                                        ?.get(i)?.listImg!![j]
                                }"
                                imgPicker.value?.add(ImagePicker(url, isVideo = AppUtils.isVideo(url)))
                            }
                                try {
                                    var index = i
                                    listConTent.value?.add(PostContentEntity((++index), this.getListContent()?.get(i)?.content!!, imgPicker))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                        }
                        nameAvatar = this.getListContent()?.get(0)?.listImg!![0]
                        val urlAvatar ="${this.url_prefix}${nameAvatar}"
                        val imgPickerAvatar = ImagePicker(urlAvatar,isVideo = AppUtils.isVideo(urlAvatar))
                        pushPostViewModel.postContentPlace.postValue(SelectPlace(idUser, this.title, listConTent, imgPickerAvatar,LocationEntity(0.0,0.0, this.location_id.toString(), this.name_locations.toString())  ))

                    }
                    else{
                        val listConTent: MutableLiveData<ArrayList<PostContentEntity>> = MutableLiveData(arrayListOf())
                        for (i in 1 until this.getListContent()?.size!!) {
                            val imgPicker: MutableLiveData<ArrayList<ImagePicker>> =MutableLiveData(ArrayList())
                            for (j in 0 until this.getListContent()?.get(i)?.listImg?.size!!) {
                                val url = "${this.url_prefix}${
                                    this.getListContent()
                                        ?.get(i)?.listImg!![j]
                                }"
                                imgPicker.value?.add(ImagePicker(url, isVideo = AppUtils.isVideo(url)))
                            }
                            try {
                                var index = i
                                listConTent.value?.add(PostContentEntity((++index), this.getListContent()?.get(i)?.content!!, imgPicker))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        nameAvatar = this.getListContent()?.get(0)?.listImg!![0]
                        val urlAvatar ="${this.url_prefix}${nameAvatar}"
                        val imgPickerAvatar = ImagePicker(urlAvatar,isVideo = AppUtils.isVideo(urlAvatar))
                        pushPostViewModel.postContentUtensils.postValue(SelectUtensils(idUser, this.title, listConTent, imgPickerAvatar,
                            utensil_id?.let { UtensilsEntity(it,name_utensils)}))

                    }
                }
            } else {
                pushPostViewModel.getPostContentInDB(idUser, typeFragment)
            }
            createPostContent()

            pushPostViewModel.actionState.observe(viewLifecycleOwner, {
                try {
                    when (it) {
                        PushPostViewModel.STATE.NOTIFY_DATA -> {
                            mAdapter?.notifyDataSetChanged()
                        }
                        PushPostViewModel.STATE.SAVED -> {
                            showProgressDialogSave()
                        }
                        PushPostViewModel.STATE.DELETED -> {
                            closeScreen()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
        }
//        setLatAndLng()
    }

    @SuppressLint("SetTextI18n")
    private fun setLatAndLng() {
        try {
            var place = ""
            GlobalScope.launch(Dispatchers.Unconfined) {
                if (mLat != null && mLng != null) {
                    try {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(mLat?:0.0, mLng?:0.0, 5)
                        if (addresses.isNotEmpty()) {
                            val address: Address = addresses[0]
                            place = "${address.getAddressLine(0)}"
                            if (typeFragment == FRM_POST_LOCATION){
                                if (!locationName.isNullOrEmpty()){
                                getBinding()?.tvLatAndLng?.text = locationName
                                }else {
                                    locationName = place.replace("Unnamed Road, ", "")
                                    getBinding()?.tvLatAndLng?.text = place.replace("Unnamed Road, ", "")
                                }
                            }
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setUtensils(){
        if (typeFragment== FRM_POST_UTENSILS){
            getBinding()?.icPlace?.setImageResource(R.drawable.ic_utensils_main)
            getBinding()?.tvAdd?.text = context?.resources?.getString(R.string.str_tools)
            getBinding()?.tvAdd?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
           utensilsEntity?.let {
               getBinding()?.tvLatAndLng?.text=it.name
           }
        }else{
            getBinding()?.tvAdd?.text = context?.resources?.getString(R.string.str_place)
            getBinding()?.tvAdd?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            getBinding()?.icPlace?.setImageResource(R.drawable.ic_place_push_post)
        }
    }

    private fun showDialogCancel() {
        if (pushPostViewModel.isEditPost){
            dismissAllowingStateLoss()
        }else{
            val title = getBinding()?.edtTitle?.text.toString()
            if (listContent?.size == 1 && listContent?.get(0)?.content == "" && listContent?.get(0)?.listImg?.value?.size == 0 && title == "") {
                onBackListener()

            } else {
                val dialogSave = DialogSavePushPost(requireContext())
                dialogSave.show(false,resources.getString(R.string.titleConfirm),resources.getString(R.string.msgConfirmExitPost),object : DialogSavePushPost.OnDeleteAgreeOrCancel{
                    override fun onAgree() {
                        if (typeFragment == FRM_POST_LOCATION) authViewModel.isHaveLocalDataSelectPlace = true
                        else authViewModel.isHaveLocalDataSelectUtensils = true
                        saveCachePoshPost()
                    }

                    override fun onCancel() {
                        if (idUser != "") {
                            if (typeFragment == FRM_POST_LOCATION) authViewModel.isHaveLocalDataSelectPlace = false
                            else authViewModel.isHaveLocalDataSelectUtensils = false
                            pushPostViewModel.deletePost(idUser, true,typeFragment)
                        } else{
                            closeScreen()
                        }
                    }

                })
//                AlertDialog.Builder(mActivity!!).apply {
//                    val title = resources.getString(R.string.titleConfirm)
//                    val message = resources.getString(R.string.msgConfirmExitPost)
//                    setTitle(title)
//                    setCancelable(false)
//                    setMessage(message)
//                    setNegativeButton(resources.getString(R.string.lblYes)) { _, _ ->
//                        if (isClickAble()) {
//                            if (typeFragment == FRM_POST_LOCATION) authViewModel.isHaveLocalDataSelectPlace = true
//                            else authViewModel.isHaveLocalDataSelectUtensils = true
//                            saveCachePoshPost()
//                        }
//                    }
//                    setPositiveButton(resources.getString(R.string.lblNo)) { _, _ ->
//                        if (isClickAble()) {
//                            if (idUser != "") {
//                                if (typeFragment == FRM_POST_LOCATION) authViewModel.isHaveLocalDataSelectPlace = false
//                                else authViewModel.isHaveLocalDataSelectUtensils = false
//                                pushPostViewModel.deletePost(idUser, true,typeFragment)
//                            } else{
//                                closeScreen()
//                            }
//                        }
//                    }.show()
//
//                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun showDialogDeletePostIfNeed() {
        val title = resources.getString(R.string.titleConfirm)
        val message = resources.getString(R.string.msgConfirmResetPost)
        mActivity?.showDialogExit(title,message,object : DialogExit.CallbackListenerExit{
            override fun onOk() {}

            override fun onCancel() {
                if (idUser.isNotEmpty()) {
                    if (typeFragment == FRM_POST_LOCATION){
                        pushPostViewModel.deletePost(idUser, false,typeFragment)
//
                        DialogFragmentAddImagesAvatar.getInstance(FrmHome.DIALOG_FRM_LOCATION).show(this@FrmPushPosts.childFragmentManager,"DialogFragmentAddImages")
                        authViewModel.isHaveLocalDataSelectPlace=false

                    }else{
                        pushPostViewModel.deletePost(idUser, false,typeFragment)
//
                        DialogFragmentAddImagesAvatar.getInstance(FrmHome.DIALOG_FRM_UTENSILS).show(this@FrmPushPosts.childFragmentManager,"DialogFragmentAddImages")
                        authViewModel.isHaveLocalDataSelectUtensils=false
                    }
                }
            }

        })
    }

    private fun saveCachePoshPost() {
        try {
            if (typeFragment == FRM_POST_LOCATION){
                val title = getBinding()?.edtTitle?.text.toString()
                pushPostViewModel.postContentPlace.value?.title = title
                imagePicker?.let {
                    pushPostViewModel.postContentPlace.value?.avatar = it
                }
                locationEntity?.let {
                    pushPostViewModel.postContentPlace.value?.location = it
                }
                pushPostViewModel.savePost()
            }else{
                val title = getBinding()?.edtTitle?.text.toString()
                pushPostViewModel.postContentUtensils.value?.title = title
                imagePicker?.let {
                    pushPostViewModel.postContentUtensils.value?.avatar = it
                }
                this.utensilsEntity?.let {
                    pushPostViewModel.postContentUtensils.value?.utensils = it

                }
                pushPostViewModel.savePost()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProgressDialogSave() {
        try {
            val toast =
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.str_save_post_success),
                    Toast.LENGTH_SHORT
                )
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            closeScreen()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiPushPost() {
        try {
            val requestParam = mActivity!!.getRequestParamWithToken()
            val title = getBinding()?.edtTitle?.text.toString()
            val listImg: ArrayList<MultipartBody.Part> = ArrayList()
            var indexPushPostImage :Int = 0
            var countVideo = 0
            var countImage = 0
            prepareFilePart("listImg_0[]", Uri.parse(imagePicker!!.uri),imagePicker?.path ?:"")?.let {
                listImg.add(it)
            }
            listContent?.let {
                for (i in 0 until it.size) {

                    val postContentEntity: PostContentEntity = it[i]
                    if (postContentEntity.content.isNotEmpty() ||  postContentEntity.listImg.value?.size?:0>0 ){
                        ++indexPushPostImage
                        for (j in 0 until it[i].listImg.value?.size!!) {
                            if (listContent?.get(i)?.listImg?.value?.get(j)?.isVideo==true){
                                countVideo++
                            }else countImage++
                            // check type Video post
                            typeVideo = if (listContent?.get(i)?.listImg?.value?.get(j)?.isVideo==true ){
                                if (listContent?.get(i)?.listImg?.value?.get(j)?.path?.isEmpty()==true) 1 //  video upload
                                else 2 // video quay
                            }else null // type video = null when not Video in list post
                            //
                            prepareFilePart(
                                "listImg_${indexPushPostImage}[]",
                                Uri.parse(listContent?.get(i)?.listImg?.value?.get(j)?.uri),
                                pathFile = listContent?.get(i)?.listImg?.value?.get(j)?.path!!
                            )?.let {
                                listImg.add(
                                    it
                                )
                            }
                        }
                    }

                }
            }
            typeVideo?.let {
                requestParam[ApiConst.PARAM_TYPE_VIDEO] = it.toString()
            }
            requestParam[ApiConst.PARAM_API_TITLE] = title
            requestParam[ApiConst.PARAM_API_CONTENT] = listToJson(listContent)
            if (typeFragment == FRM_POST_LOCATION){
                if (locationEntity !=null){
                    requestParam[ApiConst.PARAM_API_LOCATION_ID] = locationId
                }else{
                    requestParam[ApiConst.PARAM_API_LOCATION_ID] = ""
                }
                requestParam[ApiConst.PARAM_API_LOCATION_NAME] = locationName
                requestParam[ApiConst.PARAM_API_LAT] = mLat.toString()
                requestParam[ApiConst.PARAM_API_LNG] = mLng.toString()
                requestParam[ApiConst.PARAM_API_TYPE_PUSH_POST] = AppConst.TYPE_PUSH_POST_LOCATION
                requestParam[ApiConst.PARAM_LANGUAGE] = language.toString()
            }
            if (typeFragment == FRM_POST_UTENSILS){
                if (utensilsEntity !=null){
                    requestParam[ApiConst.PARAM_API_UTENSIL_ID] = utensilsEntity?.id.toString()
                }else{
                    requestParam[ApiConst.PARAM_API_UTENSIL_ID] = ""
                }
                requestParam[ApiConst.PARAM_API_UTENSIL_NAME] = utensilsEntity?.name.toString()
                requestParam[ApiConst.PARAM_API_TYPE_PUSH_POST] = AppConst.TYPE_PUSH_POST_UTENSIL
            }
            requestParam[ApiConst.PARAM_API_HASHTAG_ID] = ""
            requestParam[ApiConst.PARAM_API_HASHTAG_NAME] = ""
            if (countVideo>AppConst.DEFAULT_NUMBER_VIDEO_POST || countImage>AppConst.DEFAULT_NUMBER_IMAGE_POST){
                val dialogWarning = DialogWarning(requireContext())
                if (dialogWarning.isShowing)return
                dialogWarning.show(false,countImage.toString(),countVideo.toString(),(countImage+countVideo).toString())
            }else{
                pushPostViewModel.requestPushPost(requestParam, listImg)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiEditPost() {
        try {
            val requestParam = mActivity!!.getRequestParamWithToken()
            val title = getBinding()?.edtTitle?.text.toString()
            val listImg: ArrayList<MultipartBody.Part> = ArrayList()
            var indexPushPostImage :Int = 0
            var countVideo = 0
            var countImage = 0
            if (imagePicker?.isChecked == true){
                prepareFilePart("listImg_0[]", Uri.parse(imagePicker!!.uri),imagePicker?.path ?:"")?.let {
                    listImg.add(it)
                }
                if (!nameAvatar.isNullOrEmpty()) {
                    listImageDelete?.add(nameAvatar)
                }

            }
            listContent?.let {
                for (i in 0 until it.size) {
                    val postContentEntity: PostContentEntity = it[i]
                    if (postContentEntity.content.isNotEmpty() ||  postContentEntity.listImg.value?.size?:0>0 ){
                        ++indexPushPostImage
                        for (j in 0 until it[i].listImg.value?.size!!) {
                            if (listContent?.get(i)?.listImg?.value?.get(j)?.isVideo==true){
                                countVideo++
                            }else countImage++

                            if (listContent?.get(i)?.listImg?.value?.get(j)?.isChecked == true){
                                // check type Video post
                                typeVideo = if (listContent?.get(i)?.listImg?.value?.get(j)?.isVideo==true ){
                                    if (listContent?.get(i)?.listImg?.value?.get(j)?.path?.isEmpty()==true) 1 //  video upload
                                    else 2 // video quay
                                }else null // type video = null when not Video in list post
                                //

                                prepareFilePart("listImg_${indexPushPostImage}[]",
                                    Uri.parse(listContent?.get(i)?.listImg?.value?.get(j)?.uri),
                                    pathFile = listContent?.get(i)?.listImg?.value?.get(j)?.path!!
                                )?.let {
                                    listImg.add(
                                        it
                                    )
                                }
                            }
                        }
                    }

                }
            }

            requestParam[ApiConst.PARAM_API_TITLE] = title
            requestParam[ApiConst.PARAM_API_CONTENT] = listToJson(listContent)
            requestParam[ApiConst.PARAM_API_HASHTAG_ID] = ""
            requestParam[ApiConst.PARAM_LANGUAGE] = language.toString()

            if (typeFragment == FRM_POST_LOCATION){
                if (locationEntity !=null){
                    requestParam[ApiConst.PARAM_API_LOCATION_ID] = locationId
                }else{
                    requestParam[ApiConst.PARAM_API_LOCATION_ID] = ""
                }
                requestParam[ApiConst.PARAM_API_LOCATION_NAME] = locationName
                requestParam[ApiConst.PARAM_API_LAT] = mLat.toString()
                requestParam[ApiConst.PARAM_API_LNG] = mLng.toString()
            }
            if (typeFragment == FRM_POST_UTENSILS){
                if (utensilsEntity !=null){
                    requestParam[ApiConst.PARAM_API_UTENSIL_ID] = utensilsEntity?.id.toString()
                }else{
                    requestParam[ApiConst.PARAM_API_UTENSIL_ID] = ""
                }
                requestParam[ApiConst.PARAM_API_UTENSIL_NAME] = utensilsEntity?.name.toString()
            }
            requestParam[ApiConst.PARAM_API_HASHTAG_NAME] = ""
            if (!listImageDelete.isNullOrEmpty()){
                val gson = Gson()
                val jsonList = gson.toJson(listImageDelete)
                requestParam[ApiConst.PARAM_API_IMG_DELETE] = jsonList
            }

            if (countVideo>AppConst.DEFAULT_NUMBER_VIDEO_POST || countImage>AppConst.DEFAULT_NUMBER_IMAGE_POST){
                val dialogWarning = DialogWarning(requireContext())
                if (dialogWarning.isShowing)return
                dialogWarning.show(false,countImage.toString(),countVideo.toString(),(countImage+countVideo).toString())
            }else{
                pushPostViewModel.requestEdtPost(requestParam, listImg, entityPostNeedEdit?.id.toString())
            }
          
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observerPushPost() {
        try {
            pushPostViewModel.pushPostResult.observe(viewLifecycleOwner, {
                try {
                    if (it.status == ApiResult.Status.SUCCESS) {
                        it.data?.message?.let {
                            showDialogSuccess(it)
                        }
                        shareViewModel.setNeedRefreshPostDataAllScreen()
                        isCheckPushPost = true
                        //clear data local and isCheckdatalocal = false
                        if (typeFragment == FRM_POST_LOCATION)  authViewModel.isHaveLocalDataSelectPlace = false
                        else authViewModel.isHaveLocalDataSelectUtensils = false


                    }
                    shareViewModel.showOrHideLoading(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
        }
    }
    private fun showDialogSuccess(tvMessage: String){
        try {
            val dialogSuccess = DialogSuccess(requireContext())
            dialogSuccess.show(tvMessage, false)
            val timeDismiss = Timer()
            timeDismiss.schedule(object : TimerTask() {
                override fun run() {
                    dialogSuccess.dismiss()
                    dialogSuccess.cancel()
                }
            }, 2000)
            dialogSuccess.setOnDismissListener {
                pushPostViewModel.deletePost(idUser, true,typeFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun observerEditPost() {
        try {
            pushPostViewModel.editPostResult.observe(viewLifecycleOwner, {
                try {
                    shareViewModel.showOrHideLoading(it)
                    if (it.status == ApiResult.Status.SUCCESS) {
                        if (it.data?.response is ResponseEditPost.EditPostResponse){
                            (it.data.response as ResponseEditPost.EditPostResponse).let { response->
                                shareViewModel.editPost = response
                            }
                        }
                        shareViewModel.setNeedRefreshEditDataAllScreen()
                        if (typeFragment == FRM_POST_LOCATION)  authViewModel.isHaveLocalDataSelectPlace = false
                        else authViewModel.isHaveLocalDataSelectUtensils = false
                        it?.data?.message?.let {
                            showDialogSuccess(it)
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun listToJson(list: ArrayList<PostContentEntity>?): String {
        val jsonArray = JSONArray()
        val jsonObjectImage = JSONObject()
        jsonObjectImage.put(PARAM_API_CONTENT,"")
        jsonArray.put(jsonObjectImage)
        try {
            list?.let {
                for (i in 0 until it.size) {
                    val postContentEntity: PostContentEntity = it[i]
                    val jsonObject = JSONObject()
                    if (postContentEntity.content.isNotEmpty() ||  postContentEntity.listImg.value?.size?:0>0 ){
                        jsonObject.put(PARAM_API_CONTENT, postContentEntity.content)
                        jsonArray.put(jsonObject)
                    }

                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonArray.toString()
    }

    private fun getRealPathFromURI(uri: Uri): String {
        try {
            var path = ""
            val proj = arrayOf(MediaStore.MediaColumns.DATA)
            val cursor: Cursor? = mActivity?.contentResolver?.query(uri, proj, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    path = cursor.getString(columnIndex)
                }
            }
            cursor?.close()
            return path
        } catch (e: Exception) {
        }
        return ""
    }

    private  fun prepareFilePart(partName: String, fileUri: Uri, pathFile: String = ""): MultipartBody.Part? {
        try {
            var file = if (pathFile.isEmpty()) File(getRealPathFromURI(fileUri))
            else File(pathFile)

            if (file.getSizeInKB()>(5*1024)){
                runBlocking {
                    file = Compressor.compress(requireContext(),file){
                        resolution(1280, 720)
                        quality(80)
                        format(Bitmap.CompressFormat.JPEG)
                        size(5_242_880)
                    }

                }
            }
            val requestFile: RequestBody =
                RequestBody.create(
                    mActivity?.contentResolver?.getType(fileUri)?.toMediaTypeOrNull(),
                    file
                )
            return MultipartBody.Part.createFormData(
                partName, file.name.replace(" ".toRegex(), "%20"), requestFile
            )
        } catch (e: Exception) {
        }
        return null
    }

    private fun closeScreen() {

        if (parentFragment is FrmSelectPlaces){
            (parentFragment as FrmSelectPlaces).apply {
                this.dismissAllowingStateLoss()
                if (this.parentFragment is DialogFragmentAddImagesAvatar){
                    (this.parentFragment as DialogFragmentAddImagesAvatar).apply {
                        this.dismissAllowingStateLoss()
                        if (this.parentFragment is FrmPushPosts){
                            (this.parentFragment as FrmPushPosts).apply {
                                this.dismissAllowingStateLoss()
                            }
                        }
                    }
                }
            }
        } else if (parentFragment is FrmSelectUtensils){
            (parentFragment as FrmSelectUtensils).apply {
                this.dismissAllowingStateLoss()
                if (this.parentFragment is DialogFragmentAddImagesAvatar){
                    (this.parentFragment as DialogFragmentAddImagesAvatar).dismissAllowingStateLoss()
                }

            }

        }else if (parentFragment is FrmDetail){
            (parentFragment as FrmDetail).apply {
                this.backToPrevious()
            }
        }
        else{
            dismissAllowingStateLoss()
        }
    }
    private fun onBackListener(){
        val title = resources.getString(R.string.titleConfirm)
        val message = resources.getString(R.string.msgConfirmExit)
        mActivity?.showDialogExit(title,message,object :DialogExit.CallbackListenerExit{
            override fun onOk() {
                if (parentFragment is FrmSelectPlaces){
                    (parentFragment as FrmSelectPlaces).apply {
                        this.dismissAllowingStateLoss()
                        if (this.parentFragment is DialogFragmentAddImagesAvatar){
                            (this.parentFragment as DialogFragmentAddImagesAvatar).apply {
                                this.dismissAllowingStateLoss()
                                if (this.parentFragment is FrmPushPosts){
                                    (this.parentFragment as FrmPushPosts).apply {
                                        this.dismissAllowingStateLoss()
                                    }
                                }
                            }
                        }
                    }
                }
                if (parentFragment is FrmSelectUtensils){
                    (parentFragment as FrmSelectUtensils).apply {
                        this.dismissAllowingStateLoss()
                        if (this.parentFragment is DialogFragmentAddImagesAvatar){
                            (this.parentFragment as DialogFragmentAddImagesAvatar).apply {
                                this.dismissAllowingStateLoss()
                                if (this.parentFragment is FrmPushPosts){
                                    (this.parentFragment as FrmPushPosts).apply {
                                        this.dismissAllowingStateLoss()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancel() {}

        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    fun onEvent(event: Any) {
        if (event is LocationEntity){
            event.apply {
                this@FrmPushPosts.locationEntity=this
                this@FrmPushPosts.locationId= this.locationId
                this@FrmPushPosts.locationName= this.locationName
                this@FrmPushPosts.mLat=this.lat
                this@FrmPushPosts.mLng=this.long
            }
            setLatAndLng()
        }
        if (event is UtensilsEntity){
            event.apply {
                this@FrmPushPosts.utensilsEntity=this

            }
            setUtensils()
        }
        if (event is ImagePicker){
            event.apply {
                this@FrmPushPosts.imagePicker=this
                val uriImage = Uri.parse(imagePicker?.uri)
                getBinding()?.imgAvatar?.setImageURI(uriImage)
                Log.e(TAG, "onEvent: $imagePicker", )
            }
        }
    }
    private fun createPostContent(){
            if (typeFragment == FRM_POST_LOCATION){
                pushPostViewModel.postContentPlace.observe(viewLifecycleOwner, {
                    try {
                        it.listConTent.observe(viewLifecycleOwner, { listContent ->
                            this.listContent = listContent
                            mAdapter?.apply {
                                updateList(listContent)
                            }
                        })
                        getBinding()?.edtTitle?.setText(it.title)

                        it.avatar?.let {
                            getBinding()?.imgAvatar?.loadImageUrl(it.uri)
                            this.imagePicker = it
                        }
                        it.location?.let {
                            this.locationEntity = it
                            this.locationName = it.locationName
                            this.locationId= it.locationId
                            getBinding()?.tvLatAndLng?.text = this.locationName
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                })
            }else{
                pushPostViewModel.postContentUtensils.observe(viewLifecycleOwner, {
                    try {
                        it.listConTent.observe(viewLifecycleOwner, { listContent ->
                            this.listContent = listContent
                            mAdapter?.apply {
                                updateList(listContent)
                            }
                        })

                        getBinding()?.edtTitle?.setText(it.title)


                        it.avatar?.let {
                            getBinding()?.imgAvatar?.loadImageUrl(it.uri)
                            this.imagePicker = it
                        }
                        it.utensils?.let {
                            this.utensilsEntity = it
                            it.name?.let { name->
                                this.locationName = name
                            }
                            this.locationId=it.id.toString()
                            getBinding()?.tvLatAndLng?.text = this.locationName
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                })
            }
        }
    private fun observerListImageDelete(){
        pushPostViewModel.listImgDelete.observe(viewLifecycleOwner, {
            if (it != null){
                listImageDelete = it
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post(true)
    }

    override fun onClick(v: View?) {
        if (!isClickAble()) return
        when(v?.id){
            R.id.btnDangBai -> {
                if (!isCheckPushPost)
                handlePushPost()
            }
            R.id.btnAddLocate ->  {
                if (typeFragment== FRM_POST_LOCATION) FrmSelectPlaces().show(childFragmentManager, "FrmPushPosts")
                else if (typeFragment == FRM_POST_UTENSILS) FrmSelectUtensils().show(childFragmentManager, "FrmPushPosts")
            }
            R.id.btnBack ->  {
                showDialogCancel()
            }
            R.id.imgAvatar ->  {
                DialogFragmentAddImagesAvatar.getInstance(isChangeAvatar = true).show(childFragmentManager, "DialogFragmentAddImagesAvatar")
            }
            R.id.rlAddContent ->  {
                handleAddNewRecord()
            }
        }
    }
}