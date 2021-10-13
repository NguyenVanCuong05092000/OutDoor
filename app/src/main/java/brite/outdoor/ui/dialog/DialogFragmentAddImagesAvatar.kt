package brite.outdoor.ui.dialog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.ImagePickAdapterAvatar
import brite.outdoor.adapter.utils.MarginDecoration
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.AppConst.Companion.DIALOG_AVATAR
import brite.outdoor.constants.ExtraConst
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_IS_CHANGE_AVATAR
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_TYPE_FRAGMENT
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.databinding.FrmPickImageBinding
import brite.outdoor.entity.Media
import brite.outdoor.ui.activities.CameraActivity
import brite.outdoor.ui.fragments.BaseDialogFragment
import brite.outdoor.ui.fragments.FrmPushPosts
import brite.outdoor.ui.fragments.FrmSelectPlaces
import brite.outdoor.ui.fragments.FrmSelectUtensils
import brite.outdoor.ui.widgets.LayoutImagePickedPreview
import brite.outdoor.utils.AppUtils
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.utils.setSingleClick
import brite.outdoor.viewmodel.PushPostViewModel
import brite.outdoor.viewmodel.ReadExternalStorageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class DialogFragmentAddImagesAvatar : BaseDialogFragment<FrmPickImageBinding>() {
    private lateinit var imagePickerAdapter: ImagePickAdapterAvatar
    private val pushPostViewModel by viewModels<PushPostViewModel>({ requireParentFragment() })
    private val postViewModel by viewModels<PushPostViewModel>()
    private val readExternalStorageViewModel by viewModels<ReadExternalStorageViewModel>()
    private var typeDialogFragment : Int= 0
    private var imagePicker : ImagePicker?=null
    private var isChangeAvatar : Boolean = false

    companion object {
        const val DIALOG_FRM_LOCATION=1
        const val DIALOG_FRM_UTENSILS=2
        private const val TAG = "DialogFragmentAvatar"
        fun getInstance(typeDialogFragment:Int=0,isChangeAvatar : Boolean = false):DialogFragment{
            val dialog=DialogFragmentAddImagesAvatar()
            val bundle = Bundle()
            bundle.putInt(EXTRA_TYPE_FRAGMENT,typeDialogFragment)
            bundle.putBoolean(EXTRA_IS_CHANGE_AVATAR,isChangeAvatar)
            dialog.arguments=bundle
            return dialog
        }
    }

    private var binding: FrmPickImageBinding? = null
    private var listImage: ArrayList<ImagePicker>? = null

    private val requestPermissionReadExternalStorage =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    GlobalScope.launch(Dispatchers.Main) {
                        readExternalStorageViewModel.readMediaExternalStorage.value=getPicturePaths()
                    }
//                    listImage = getPicturePaths()
//                    imagePickerAdapter.setListImage(listImage)
                } else {
                    Toast.makeText(activity, resources.getString(R.string.str_no_memory_assess), Toast.LENGTH_SHORT)
                            .show()
                }
            }
    private var startForResult =   registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        try {
            if (result.resultCode == AppConst.RESULT_CODE) {
                result.data?.let {
                    val media = it.getParcelableExtra<Media>(ExtraConst.EXTRA_MEDIA)
                    media?.let {
                        val imagePicker = ImagePicker(uri = it.uri.toString(),isChecked = true,path = it.path,isVideo = it.isVideo)
                        setDataToView(imagePicker)
                    }


                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FrmPickImageBinding.inflate(inflater, container, false)
        loadControlsAndResize(binding)
        initView()
        return binding!!.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        arguments?.apply {
            typeDialogFragment = getInt(EXTRA_TYPE_FRAGMENT)
            isChangeAvatar = getBoolean(EXTRA_IS_CHANGE_AVATAR)
        }
    }

    private fun initView() {
        setupListView()
        loadListImageWithPermission()
        setDataToView()
        observerReadExternalStorage()
    }

    override fun loadControlsAndResize(binding: FrmPickImageBinding?) {
        binding?.apply {
            setupTopNavigation(this)
            resizeButtonAndView(this)
            setupOnClickListener(this)
            resizeLoadingProgress(this)
        }
    }
    private fun setDataToView(imagePick:ImagePicker?=null) {
        if (imagePick?.isCamera==true){
            val intent = Intent(requireContext(), CameraActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(ExtraConst.EXTRA_MEDIA,DIALOG_AVATAR)
            intent.putExtras(bundle)
            startForResult.launch(intent)
        }else{
            val imageList = ArrayList<ImagePicker>()
            imagePick?.let {
                imageList.add(it)
                this.imagePicker =it
            }
            binding?.clImagePreview?.initData(requireContext(), imageList)
        }
    }

    private fun setupTopNavigation(frmPickImageBinding: FrmPickImageBinding) {
        frmPickImageBinding.apply {
            clTopNav.resizeHeight(getSizeWithScale(56.0))
            tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))
        }

    }
    private fun resizeLoadingProgress(frmPickImageBinding: FrmPickImageBinding){
        frmPickImageBinding.apply {
            val whProgressLoading = getSizeWithScale(60.0)
            progressLoadMedia.visibility = View.VISIBLE
            progressLoadMedia.setAnimation(12,800)
            progressLoadMedia.resizeLayout(whProgressLoading,whProgressLoading)
        }
    }
    private fun resizeButtonAndView(frmPickImageBinding: FrmPickImageBinding) {
        frmPickImageBinding.apply {
            clImagePreview.resizeHeight(getSizeWithScale(320.0))

            val wButton = getSizeWithScale(109.0)
            val hButton = getSizeWithScale(41.0)

            btnClear.resizeLayout(wButton, hButton)
            btnOver.resizeLayout(wButton, hButton)

            (clButtonContainer.layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = getSizeWithScale(28.0)
                bottomMargin = getSizeWithScale(23.0)
            }

        }
    }
    private fun setupOnClickListener(frmPickImageBinding: FrmPickImageBinding) {
        frmPickImageBinding.apply {
            clImagePreview.setOnClickListener(object : LayoutImagePickedPreview.OnClickListener {
                override fun onClickImage(position: Int) {}

                override fun onClickDelete(position: Int) {
                    setDataToView()
                }
            })
            btnOver.setSingleClick {
                if (clImagePreview.getCountItem()!=0) {
                    if (parentFragment is FrmPushPosts){
                        if (isChangeAvatar){
                            imagePicker?.let {
                                EventBus.getDefault().post(it)
                                dismissAllowingStateLoss()

                            }
                        }else{
                            imagePicker?.let {
                                if (typeDialogFragment == DIALOG_FRM_LOCATION)
                                    FrmSelectPlaces.getInstances(imagePick = it).show(childFragmentManager,"FrmSelectPlaces")
                                else if (typeDialogFragment == DIALOG_FRM_UTENSILS)
                                    FrmSelectUtensils.getInstance(imagePicker = it).show(childFragmentManager,"FrmSelectUtensils")
                                }
                            }

                    }else{
                        imagePicker?.let {
                            if (typeDialogFragment == DIALOG_FRM_LOCATION)
                                FrmSelectPlaces.getInstances(imagePick = it).show(childFragmentManager,"FrmSelectPlaces")
                            else if (typeDialogFragment == DIALOG_FRM_UTENSILS)
                                FrmSelectUtensils.getInstance(imagePicker = it).show(childFragmentManager,"FrmSelectUtensils")
                        }
                    }


                }else showConfirmChooseImage()

            }
            btnClear.setSingleClick {
                dismiss()
            }
            btnBack.setSingleClick {
                if (parentFragment is FrmPushPosts){
                    if (isChangeAvatar){
                        dismissAllowingStateLoss()
                    }else{
                       (parentFragment as FrmPushPosts).apply {
                           this.dismissAllowingStateLoss()
                       }
                    }

                }else {
                    val title = resources.getString(R.string.titleConfirm)
                    val message = resources.getString(R.string.msgConfirmExitAvatar)
                    mActivity?.showDialogExit(title,message,object :DialogExit.CallbackListenerExit{
                        override fun onOk() {
                            dismissAllowingStateLoss()
                        }

                        override fun onCancel(){}

                    })
                }

            }
        }
    }
    private fun setupListView() {
        activity?.let {
            imagePickerAdapter = ImagePickAdapterAvatar(
                    it,
                    getSizeWithScale(111.0),
                    getSizeWithScale(16.0),
                    getSizeWithScale(6.0),
                    itemClickListener = {imagePicker: ImagePicker -> setDataToView(imagePicker) },
                    getSizeWithScaleFloat(10.0)
            )
            binding?.rvImagePicker?.apply {
                addItemDecoration(MarginDecoration(getSizeWithScale(9.0), 3))
                layoutManager = GridLayoutManager(
                        it, 3,
                        LinearLayoutManager.VERTICAL, false
                )
                adapter = imagePickerAdapter
            }
        }

    }
    private fun loadListImageWithPermission() {
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionReadExternalStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                   readExternalStorageViewModel.readMediaExternalStorage.value=getPicturePaths()
                }
//                listImage = getPicturePaths()
//                imagePickerAdapter.setListImage(listImage)

            }
        }

    }

    private fun observerReadExternalStorage(){
        readExternalStorageViewModel.readMediaExternalStorage.observe(viewLifecycleOwner,{
            listImage=it
            imagePickerAdapter.setListImage(listImage)

            binding?.apply {
                progressLoadMedia.visibility=View.GONE
                progressLoadMedia.animation=null
            } // stop animation loading

        })
    }
    private suspend fun getPicturePaths():ArrayList<ImagePicker>{
        val listImagePicker: ArrayList<ImagePicker> = ArrayList()

        //create item camera
        listImagePicker.add(ImagePicker(isCamera = true))

            return withContext(Dispatchers.IO) {
                val uriExternal = MediaStore.Files.getContentUri("external")

                //read file local image
                val collectionImage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val projectionImage = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                )

                // check file image <5mb
                val thresholdImage = 5 * 1024 * 1024
                val selectionImage = "${MediaStore.Images.ImageColumns.SIZE} <= ?"
                val selectionArgsImage = arrayOf(thresholdImage.toString())
                val cursorImage: Cursor? =
                    activity?.contentResolver?.query(
                        collectionImage,
                        projectionImage,
                        null,
                        null,
                        null
                    )
                var columnIndexIDImage: String
                try {
                    cursorImage?.let {
                        cursorImage.moveToFirst()
                        do {
                            val name =
                                cursorImage.getString(cursorImage.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                            columnIndexIDImage =
                                cursorImage.getString(cursorImage.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                            val uriImage =
                                Uri.withAppendedPath(uriExternal, "" + columnIndexIDImage)
                            val imgPicker = ImagePicker(uriImage.toString())
                            activity?.apply {
                                imgPicker.isVideo =
                                    AppUtils.isVideo(AppUtils.getRealPathFromURI(this, uriImage))
                            }

                            listImagePicker.add(imgPicker)
                        } while (cursorImage.moveToNext())
                        cursorImage.close()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                listImagePicker
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post(true)
    }
    //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue = resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }
//
//    fun getSizeWithScale(sizeDesign: Double): Int {
//        return (sizeDesign * getScaleValue()).toInt()
//    }
//
//    fun getSizeWithScaleFloat(sizeDesign: Double): Float {
//        return (sizeDesign * getScaleValue()).toFloat()
//    }
//
//    fun getRealSize(sizeDesign: ViewSize): ViewSize {
//        return ViewSize(sizeDesign.width * getScaleValue(), sizeDesign.height * getScaleValue())
//    }
    //End Resize
    private fun showConfirmChooseImage(){
        val dialog = DialogNotice(requireContext())
        dialog.show(resources.getString(R.string.titlePostNoLongerExists),resources.getString(R.string.msgConfirmChooseImage),false,null)
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmPickImageBinding {
        return FrmPickImageBinding.inflate(inflater,container,false)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

}