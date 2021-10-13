package brite.outdoor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ResponseDetailPost
import brite.outdoor.data.api_entities.response.ResponseListBookMark
import brite.outdoor.ui.activities.MainActivity
import brite.outdoor.ui.widgets.CustomImageButton
import brite.outdoor.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import org.greenrobot.eventbus.EventBus
import java.io.File

class DialogShare (context: Context,val onClickShareListener :(String) ->Unit ={_:String->} ) : Dialog(context, R.style.dialogWithoutBox) {
    private var isCancelAble = false
    private var btnClose: CustomImageButton? = null
    private var imgPost: ImageView? = null
    private var tvTitlePost: TextView? = null
    private var imgAvatar: ImageView? = null
    private var tvDate: TextView? = null
    private var tvNamePostedBy: TextView? = null
    private var tvMessage: EditText? = null
    private var btnShare: TextView? = null
    private var rlRoot: View? = null
    private var clItemShare: View? = null
    private var clHeaderDialog: View? = null
    private var entity : ListPostUserData?=null

    fun show(entityNew: ResponseDetailPost.DetailPostResponse?, urlPrefixAvatar: String?, urlAvatar: String) {
            try {
                entityNew?.apply {
                    imgPost?.loadImageWithCustomCorners(urlImage(getListContent()?.get(0)?.listImg?.get(0), url_prefix), 50)
                    imgAvatar?.loadImageWithCustomCorners(urlImage(urlAvatar, urlPrefixAvatar), 120)
                    tvDate?.text = getDate(created_time)
                    tvTitlePost?.text = title

                    tvNamePostedBy?.text = user_name_created
                }
                show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun show(entityNew: ListPostUserData?, urlAvatar: String) {
        try {
            entityNew?.apply {
                if (getListContent()?.get(0)?.listImg?.size?:0 > 0){
                    imgPost?.loadImageWithCustomCorners(urlImage(getListContent()?.get(0)?.listImg?.get(0), url_prefix), 50)
                }else imgPost?.setImageResource(R.drawable.ic_no_image)
                imgAvatar?.loadImageWithCustomCorners(urlImage(urlAvatar, url_prefix_avatar), 120)
            tvDate?.text = getDate(created_time)
            tvTitlePost?.text = title
            tvNamePostedBy?.text = user_name_created
                this@DialogShare.entity=this
            }
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(entitySavedPost: ResponseListBookMark.ListBookMarkData?) {
        try {
            entitySavedPost?.apply {
                imgPost?.loadImageWithCustomCorners(urlImage(getListContent()?.get(0)?.listImg?.get(0), url_prefix), 50)
                imgAvatar?.loadImageWithCustomCorners(urlImage(avatar_user, url_prefix_avatar), 120)
                tvDate?.text = getDate(modified_time)
                tvTitlePost?.text = title
                tvNamePostedBy?.text = user_name_created
            }
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.dialog_share)
        val displayMetrics = context.resources.displayMetrics
        btnClose = findViewById(R.id.btnClose)
        imgPost = findViewById(R.id.imgPost)
        tvTitlePost = findViewById(R.id.tvTitlePost)
        imgAvatar = findViewById(R.id.imgAvatar)
        tvDate = findViewById(R.id.tvDate)
        tvNamePostedBy = findViewById(R.id.tvNamePostedBy)
        tvMessage = findViewById(R.id.tvMessage)
        btnShare = findViewById(R.id.btnShare)
        rlRoot = findViewById(R.id.rlRoot)
        clItemShare = findViewById(R.id.clItemShare)
        clHeaderDialog = findViewById(R.id.clHeaderDialog)

        rlRoot?.layoutParams?.width = getSizeWithScale(351.0)
        rlRoot?.layoutParams?.height = getSizeWithScale(414.0)

        clItemShare?.layoutParams?.height = getSizeWithScale(133.0)

        btnShare?.layoutParams?.width = getSizeWithScale(182.0)
        btnShare?.layoutParams?.height = getSizeWithScale(46.0)

        imgAvatar?.layoutParams?.width = getSizeWithScale(46.0)
        imgAvatar?.layoutParams?.height = getSizeWithScale(46.0)

        clHeaderDialog?.layoutParams?.height = getSizeWithScale(68.0)

        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
        btnShare?.setSingleClick {
            val title = tvMessage?.text.toString()
            onClickShareListener(title)
        }
        findViewById<View>(R.id.btnClose).setOnClickListener { dismiss() }
    }
    

    //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue =
                context.resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }

    fun getSizeWithScale(sizeDesign: Double): Int {
        return (sizeDesign * getScaleValue()).toInt()
    }
   
}