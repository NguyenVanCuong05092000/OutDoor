package brite.outdoor.ui.dialog

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.R
import brite.outdoor.adapter.ImagePickerAdapter
import brite.outdoor.adapter.utils.MarginDecoration
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.ExtraConst
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.databinding.FrmPickImageBinding
import brite.outdoor.entity.Media
import brite.outdoor.ui.activities.CameraActivity
import brite.outdoor.ui.widgets.LayoutImagePickedPreview
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.PushPostViewModel
import brite.outdoor.viewmodel.ReadExternalStorageViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DialogFragmentAddImages : DialogFragment() {

    companion object {
        private const val TAG = "TestDialogFragment"
    }

    private var listImagePickerOld : ArrayList<ImagePicker>? = ArrayList()
    private var binding: FrmPickImageBinding? = null
    private var imagePickerAdapter: ImagePickerAdapter? = null
    private var lisImageDelete: ArrayList<String?>? = ArrayList()
    private var listSelect: ArrayList<ImagePicker>? = ArrayList()
    private val pickImageViewModel by viewModels<PushPostViewModel>({ requireParentFragment() })
    private val readExternalStorageViewModel by viewModels<ReadExternalStorageViewModel>()
    private val requestPermissionReadExternalStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                GlobalScope.launch(Dispatchers.Main) {
                    readExternalStorageViewModel.readMediaExternalStorage.value=getPicturePaths()
                }
//                listImage = getPicturePaths()
//                imagePickerAdapter?.updateListImage(listImage)
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
                        var imagePicker:ImagePicker?=null
                        if (it.path.isNotEmpty()) imagePicker = ImagePicker(uri = it.uri.toString(),isChecked = true,numberSelect = (listDataDialogFrmImagePager.size+1),path = it.path,isVideo = it.isVideo)
                        else imagePicker = ImagePicker(uri = it.uri.toString(),isChecked = true,numberSelect = (listDataDialogFrmImagePager.size+1),isVideo = it.isVideo)
                        pickImageViewModel.contentSelected.value?.listImg?.value?.add(imagePicker)
                        listDataDialogFrmImagePager.add(imagePicker)
                        setDataToView(listDataDialogFrmImagePager)
                    }


                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private var dlgShowImages: DialogFragmentImagePager? = null
    private fun showPopupImages() {
        if (dlgShowImages == null)
            dlgShowImages = DialogFragmentImagePager()
        dlgShowImages!!.show(childFragmentManager, "DialogFragmentShowmages")
        dlgShowImages?.let {
            it.setData(listDataDialogFrmImagePager)
        }

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
    private var listDataDialogFrmImagePager = arrayListOf<ImagePicker>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrmPickImageBinding.inflate(inflater, container, false)
        loadControlsAndResize(binding)
        initView()
        return binding!!.root
    }

    fun loadControlsAndResize(binding: FrmPickImageBinding?) {
        binding?.apply {
            setupTopNavigation(this)
            resizeButtonAndView(this)
            setupOnClickListener(this)
            resizeLoadingProgress(this)
        }
    }

    fun initView() {
        pickImageViewModel.contentSelected.value?.listImg?.let { list ->
            listImagePickerOld = list.value
            setupListView(list)
            loadListImageWithPermission()
            observeListImageSelected(list)
        }
        observerReadExternalStorage()
    }
    private fun observerReadExternalStorage(){
        readExternalStorageViewModel.readMediaExternalStorage.observe(viewLifecycleOwner,{
            listImage=it
            imagePickerAdapter?.updateListImage(listImage)

            binding?.apply {
                progressLoadMedia.visibility=View.GONE
                progressLoadMedia.animation=null
            } // stop animation loading

        })
    }

    private fun observeListImageSelected(list: MutableLiveData<ArrayList<ImagePicker>>) {
        list.observe(viewLifecycleOwner, {
            try {
                it?.let {
                    if (!listSelect.isNullOrEmpty()) listSelect?.clear()
                    listSelect?.addAll(it)
                    setDataToView(it)
                    listDataDialogFrmImagePager.clear()
                    listDataDialogFrmImagePager.addAll(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun setupListView(list: MutableLiveData<ArrayList<ImagePicker>>) {
        activity?.let {
            imagePickerAdapter = ImagePickerAdapter(
                pickImageViewModel.isEditPost,
                it,
                list,
                getSizeWithScale(111.0),
                getSizeWithScale(16.0),
                getSizeWithScale(6.0),
                getSizeWithScaleFloat(10.0)
            ){
                openCamera()
            }
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
            }
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
                override fun onClickImage(position: Int) {
                    if (listDataDialogFrmImagePager.size > 0) showPopupImages()
                }

                override fun onClickDelete(position: Int) {
                    if (pickImageViewModel.isEditPost){
                        try {
                            if (listSelect?.get(position)?.isChecked == false){
                                lisImageDelete?.let {
                                    if (!it.contains(pickImageViewModel.listNameImage?.get(position))){
                                        if (!pickImageViewModel.listImgDelete.value.isNullOrEmpty()){
                                            for (j in 0 until  pickImageViewModel.listImgDelete.value?.size!!){
                                                if(pickImageViewModel.listNameImage?.contains( pickImageViewModel.listImgDelete.value?.get(j)) == true){
                                                    pickImageViewModel.listNameImage?.remove(pickImageViewModel.listImgDelete.value?.get(j))
                                                }
                                            }
                                        }
                                        it.add(pickImageViewModel.listNameImage?.get(position))
                                        pickImageViewModel.listNameImage?.removeAt(position)
                                        pickImageViewModel.listImgDelete.value = it
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    imagePickerAdapter?.findAndRemoveImageSelected(position)
                }
            })

            btnOver.setSingleClick {
                pickImageViewModel.actionState.value = PushPostViewModel.STATE.NOTIFY_DATA
                dismiss()
            }
            btnClear.setSingleClick {
                pickImageViewModel.contentSelected.value?.listImg?.value = listImagePickerOld
                dismiss()
            }
            btnBack.setSingleClick {
                dismiss()
            }
        }
    }

    private fun setupTopNavigation(frmPickImageBinding: FrmPickImageBinding) {
        frmPickImageBinding.apply {
            clTopNav.resizeHeight(getSizeWithScale(56.0))
            tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))
            btnBack.resizeLayout(getSizeWithScale(44.0), getSizeWithScale(56.0))
        }

    }

    private fun setDataToView(list: ArrayList<ImagePicker>) {
        binding?.clImagePreview?.initData(requireContext(), list)
    }

    private var listImage: ArrayList<ImagePicker>? = null

    private suspend fun getPicturePaths(): ArrayList<ImagePicker> {

        val listImagePicker: ArrayList<ImagePicker> = ArrayList()

        //create item camera
        listImagePicker.add(ImagePicker(isCamera = true))

        return GlobalScope.async(Dispatchers.IO){
            val uriExternal = MediaStore.Files.getContentUri("external")

            //read file local image
            val collectionImage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projectionImage = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
            )

            // check file image <5mb
            val thresholdImage = 5 * 1024 *1024
            val selectionImage = "${MediaStore.Images.ImageColumns.SIZE} <= ?"
            val selectionArgsImage = arrayOf(thresholdImage.toString())
            val cursorImage: Cursor? =
                activity?.contentResolver?.query(collectionImage, projectionImage, null, null, null)
            var columnIndexIDImage: String
            try {
                cursorImage?.let {
                    cursorImage.moveToFirst()
                    do {
                        val name =
                            cursorImage.getString(cursorImage.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        columnIndexIDImage =
                            cursorImage.getString(cursorImage.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        val uriImage = Uri.withAppendedPath(uriExternal, "" + columnIndexIDImage)
                        val imgPicker = ImagePicker(uriImage.toString())
                        activity?.apply {
                            imgPicker.isVideo = AppUtils.isVideo(AppUtils.getRealPathFromURI(this,uriImage))
                        }

                        listImagePicker.add(imgPicker)
                    } while (cursorImage.moveToNext())
                    cursorImage.close()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            // read file video
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
            )
            // check file video < 100mb and duration <=30s
            val threshold = 100 * 1024 *1024
            val selection = "${MediaStore.Video.VideoColumns.SIZE} <=? AND ${MediaStore.Video.Media.DURATION} <=?"
            val selectionArgs = arrayOf(threshold.toString(),
                TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS).toString())
            val cursor: Cursor? =
                activity?.contentResolver?.query(collection, projection, selection, selectionArgs, null)
            var columnIndexID: String
            try {
                cursor?.let {
                    cursor.moveToFirst()
                    do {
                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        columnIndexID =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        val uriImage = Uri.withAppendedPath(uriExternal, "" + columnIndexID)
                        val imgPicker = ImagePicker(uriImage.toString())
                        activity?.apply {
                            imgPicker.isVideo = AppUtils.isVideo(AppUtils.getRealPathFromURI(this,uriImage))
                        }
                        listImagePicker.add(imgPicker)
                    } while (cursor.moveToNext())
                    cursor.close()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            listImagePicker
        }.await()

    }
    private fun openCamera(){
        startForResult.launch(Intent(requireContext(),CameraActivity::class.java))
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