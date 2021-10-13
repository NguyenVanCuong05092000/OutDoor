package brite.outdoor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.utils.resizeLayout

class DialogNotice(context: Context) : Dialog(context, R.style.dialogWithoutBox) {
    private var isCancelAble = false
    fun show(message: Int, cancelAble: Boolean, dismissListener: DialogInterface.OnDismissListener?) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            setOnDismissListener(dismissListener)
            (findViewById<View>(R.id.tvConfirm) as TextView).setText(message)
            (findViewById<View>(R.id.tvTitle) as TextView).text = context.getString(R.string.titlePostNoLongerExists)
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(message: CharSequence?, cancelAble: Boolean, dismissListener: DialogInterface.OnDismissListener?) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            setOnDismissListener(dismissListener)
            (findViewById<View>(R.id.tvConfirm) as TextView).text = message
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun show(title:CharSequence?,message: CharSequence?, cancelAble: Boolean, dismissListener: DialogInterface.OnDismissListener?) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            setOnDismissListener(dismissListener)
            (findViewById<View>(R.id.tvConfirm) as TextView).text = message
            (findViewById<View>(R.id.tvTitle) as  TextView).text=title
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.dialog_notice)
        val displayMetrics = context.resources.displayMetrics
        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
        findViewById<View>(R.id.btnOk).setOnClickListener { dismiss() }

        findViewById<View>(R.id.btnOk)?.resizeLayout(MyApplication.getInstance().getSizeWithScale(120.0),MyApplication.getInstance().getSizeWithScale(48.0))
    }
}