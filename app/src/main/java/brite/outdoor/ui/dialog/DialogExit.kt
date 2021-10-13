package brite.outdoor.ui.dialog


import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.constants.AppConst
import brite.outdoor.utils.resizeLayout

class DialogExit(context: Context): Dialog(context, R.style.dialogWithoutBox){
    private var isCancelAble = false
    private var tvTitle: TextView? = null
    private var tvConfirm: TextView? = null
    private var btnOk: TextView? = null
    private var btnCancel: TextView? = null
    private var rlRoot: View? = null
    private var listener: CallbackListenerExit? = null

    interface CallbackListenerExit{
        fun onOk()
        fun onCancel()
    }

    fun show(isCancelAble: Boolean, title: String, confirm: String, listener: CallbackListenerExit) {
        try {
            this.isCancelAble = isCancelAble
            tvTitle?.text = title
            tvConfirm?.text = confirm
            this.listener = listener
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.dialog_exit)
        val displayMetrics = context.resources.displayMetrics
        tvTitle = findViewById(R.id.tvTitle)
        tvConfirm = findViewById(R.id.tvConfirm)
        btnOk = findViewById(R.id.btnOk)
        btnCancel = findViewById(R.id.btnCancel)
        rlRoot = findViewById(R.id.rlRoot)

//        rlRoot?.resizeLayout(getSizeWithScale(351.0), getSizeWithScale(200.0))
        btnOk?.resizeLayout(getSizeWithScale(140.0), getSizeWithScale(48.0))
        btnCancel?.resizeLayout(getSizeWithScale(140.0), getSizeWithScale(48.0))
//        imgClose?.resizeLayout(getSizeWithScale(30.0), getSizeWithScale(30.0))

        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
        btnCancel?.setOnClickListener {
            listener?.onCancel()
            dismiss()
        }
        btnOk?.setOnClickListener {
            listener?.onOk()
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