package brite.outdoor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import brite.outdoor.R

class CustomLayoutInteractive: ConstraintLayout {
    constructor(context: Context?) : super(context!!) {
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs!!) {
        init(context)
    }
    init {
        init(context)
    }
    interface OnClickListener {
        fun onClick(isClick: Boolean)
    }
    private var textView: TextView? = null
    private var imageView: ImageView? = null
    private var mContext: Context? = null

    private fun init(context: Context){
        this.mContext = context
        val view = inflate(context, R.layout.layout_interactive, this)
        textView = view.findViewById(R.id.textView)
        imageView = view.findViewById(R.id.imageView)
    }
    fun setText(textView: String){
        this.textView!!.text = textView
    }
    fun setIconAndSize(resld: Int, w : Int, h: Int){
        imageView!!.setImageResource(resld)
        imageView!!.layoutParams.width = w
        imageView!!.layoutParams.height = h
    }
    fun setIcon(resld: Int){
        imageView!!.setImageResource(resld)
    }
    fun setColorText(color: Int){
        textView!!.setTextColor(resources.getColor(color))
    }
    fun setSize(size: Float){
        textView!!.textSize = size
    }
}