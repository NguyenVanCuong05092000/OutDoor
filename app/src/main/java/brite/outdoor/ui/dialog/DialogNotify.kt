package brite.outdoor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.utils.resizeLayout

class DialogNotify(context: Context) : Dialog(context, R.style.dialogWithoutBox) {
    private var isCancelAble = false
    fun show(message: String, cancelAble: Boolean) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            (findViewById<View>(R.id.tvMessage) as TextView).text = message
            (findViewById<View>(R.id.btnOk) as TextView).text=context.resources.getString(R.string.str_close_dialog_notify)
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    init {
        setContentView(R.layout.dialog_notify)
        val displayMetrics = context.resources.displayMetrics
        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
        findViewById<View>(R.id.btnOk).setOnClickListener { dismiss() }
    }
}