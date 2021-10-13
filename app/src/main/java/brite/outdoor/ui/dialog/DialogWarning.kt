package brite.outdoor.ui.dialog


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import brite.outdoor.R
import brite.outdoor.constants.AppConst
import brite.outdoor.utils.resizeLayout
import brite.outdoor.utils.setSingleClick

class DialogWarning(context: Context): Dialog(context, R.style.dialogWithoutBox){
    private var isCancelAble = false
    private var btnClose: TextView? = null
    private var tvNumberPhoto:TextView?=null
    private var tvNumberVideo:TextView?=null
    private var tvResult:TextView?=null
    private var tvMaximum:TextView?=null
    private var imgWarning : AppCompatImageView?=null
    private var numberResult : TextView?=null




    @SuppressLint("SetTextI18n")
    fun show(isCancelAble: Boolean, numberPhoto: String, numberVideo:String, result:String) {
        try {
            this.isCancelAble = isCancelAble
            tvNumberPhoto?.text = context.resources.getString(R.string.str_total_image_selected) + " $numberPhoto"
            tvNumberVideo?.text = context.resources.getString(R.string.str_total_video_selected) + " $numberVideo/1"
            numberResult?.text ="$result/10"
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.dialog_warning_push_post)
        val displayMetrics = context.resources.displayMetrics
        tvNumberPhoto = findViewById(R.id.tvNumberPhoto)
        tvNumberVideo = findViewById(R.id.tvNumberVideo)
        tvResult = findViewById(R.id.tvResult)
        tvMaximum = findViewById(R.id.tvMaximumPhotoVideo)
        btnClose= findViewById(R.id.btnClose)
        imgWarning=findViewById(R.id.img_warning)
        numberResult=findViewById(R.id.numberResult)


        btnClose?.resizeLayout(getSizeWithScale(120.0), getSizeWithScale(48.0))
        imgWarning?.resizeLayout(getSizeWithScale(30.0), getSizeWithScale(30.0))
//
        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }

        btnClose?.setSingleClick { dismiss() }
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