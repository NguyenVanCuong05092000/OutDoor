package brite.outdoor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.constants.AppConst
import brite.outdoor.utils.resizeLayout

class DialogDelete(context: Context): Dialog(context, R.style.dialogWithoutBox){
    private var isCancelAble = false
    private var tvTitle: TextView? = null
    private var tvConfirm: TextView? = null
    private var btnAgree: TextView? = null
    private var btnCancel: TextView? = null
    private var rlRoot: View? = null
    private var onDeleteAgreeOrCancel: OnDeleteAgreeOrCancel? = null

    interface OnDeleteAgreeOrCancel{
        fun onAgree()
    }

    fun show(isCancelAble: Boolean, title: String, confirm: String, mOnDeleteAgreeOrCancel: OnDeleteAgreeOrCancel) {
        try {
            this.isCancelAble = isCancelAble
            tvTitle?.text = title
            tvConfirm?.text = confirm
            onDeleteAgreeOrCancel = mOnDeleteAgreeOrCancel
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.dialog_delete)
        val displayMetrics = context.resources.displayMetrics
        tvTitle = findViewById(R.id.tvTitle)
        tvConfirm = findViewById(R.id.tvConfirm)
        btnAgree = findViewById(R.id.btnAgree)
        btnCancel = findViewById(R.id.btnCancel)

        rlRoot = findViewById(R.id.rlRoot)

        rlRoot?.resizeLayout(getSizeWithScale(351.0), getSizeWithScale(200.0))
        btnAgree?.resizeLayout(getSizeWithScale(140.0), getSizeWithScale(48.0))
        btnCancel?.resizeLayout(getSizeWithScale(140.0), getSizeWithScale(48.0))

        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
        btnCancel?.setOnClickListener {
           dismiss()
        }
        btnAgree?.setOnClickListener {
            onDeleteAgreeOrCancel?.onAgree()
            dismiss()
        }
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