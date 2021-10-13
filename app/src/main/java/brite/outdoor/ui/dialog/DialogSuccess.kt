package brite.outdoor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.entity.EntityMyPage

class DialogSuccess(context: Context) : Dialog(context, R.style.dialogWithoutBox) {
    private var isCancelAble = false
    fun show(message: Int,  cancelAble: Boolean) {
        try {
            isCancelAble = cancelAble
            (findViewById<View>(R.id.tvMessage) as TextView).setText(message)
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(message: CharSequence?,  cancelAble: Boolean) {
        try {
            isCancelAble = cancelAble
            (findViewById<View>(R.id.tvMessage) as TextView).text = message
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.dialog_success)
        val displayMetrics = context.resources.displayMetrics
        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels
        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
    }
}