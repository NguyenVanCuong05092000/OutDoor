package brite.outdoor.ui.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import brite.outdoor.R
import brite.outdoor.utils.resizeLayout

class DialogTwoChoose(context: Context) : BaseDialog(context) {
    private var isCancelAble = false

    interface OnClickListener {
        fun onClickRight()
        fun onClickLeft()
    }

    private var onClickListener: OnClickListener? = null
    fun show(message: Int, cancelAble: Boolean, onClickListener: OnClickListener) {
        try {
            isCancelAble = cancelAble
            this.onClickListener = onClickListener
            setCancelable(cancelAble)
            (findViewById<View>(R.id.tvMessage) as TextView).setText(message)
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(message: CharSequence?, cancelAble: Boolean, onClickListener: OnClickListener) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            this.onClickListener = onClickListener
            (findViewById<View>(R.id.tvMessage) as TextView).text = message
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(message: CharSequence?, lblRight : String?, lblLeft : String?, cancelAble: Boolean, onClickListener: OnClickListener) {
        try {
            isCancelAble = cancelAble
            setCancelable(cancelAble)
            this.onClickListener = onClickListener
            lblLeft?.let {
                findViewById<TextView>(R.id.btnLeft)?.text = it
            }
            lblRight?.let {
                findViewById<TextView>(R.id.btnRight)?.text = it
            }
            message?.let {
                findViewById<TextView>(R.id.tvMessage)?.text = it
            }
            show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setContentView(R.layout.dialog_two_choose)
        val displayMetrics = context.resources.displayMetrics
        val vContainer = findViewById<View>(R.id.rlContent)
        vContainer.layoutParams.width = displayMetrics.widthPixels
        vContainer.layoutParams.height = displayMetrics.heightPixels

        findViewById<View>(R.id.btnClose)?.apply {
            resizeLayout(getSizeWithScale(40.0), getSizeWithScale(40.0))
            setOnClickListener {
                dismiss()
            }
        }

        vContainer.setOnClickListener { if (isCancelAble) dismiss() }
        findViewById<View>(R.id.btnRight).setOnClickListener {
            onClickListener?.onClickRight()
            dismiss()
        }
        findViewById<View>(R.id.btnLeft).setOnClickListener {
            onClickListener?.onClickLeft()
            dismiss()
        }
    }
}