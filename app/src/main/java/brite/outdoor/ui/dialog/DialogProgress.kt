package brite.outdoor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.ui.widgets.ProgressView
import brite.outdoor.utils.resizeLayout

class DialogProgress(context: Context) : Dialog(context, R.style.dialogNotice) {
    private var isCancelAble = false
    private var progressView : ProgressView?=null
    fun show(cancelAble: Boolean) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            progressView?.visibility=View.VISIBLE
            progressView?.setAnimation(12,800)
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun stopAnimation(){
        progressView?.animation=null
        progressView?.visibility=View.GONE
    }
    init {
        setContentView(R.layout.progress_dialog_loading)
        val displayMetrics = context.resources.displayMetrics
        val vContainer = findViewById<View>(R.id.rlContent)
        progressView = findViewById<ProgressView>(R.id.progressbar)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
        progressView?.resizeLayout(MyApplication.getInstance().getSizeWithScale(50.0),MyApplication.getInstance().getSizeWithScale(50.0))

    }
}