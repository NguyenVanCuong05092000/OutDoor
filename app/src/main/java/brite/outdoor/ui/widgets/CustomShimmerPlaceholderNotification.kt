package brite.outdoor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import brite.outdoor.R
import brite.outdoor.app.MyApplication

class CustomShimmerPlaceholderNotification: ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context)
    }
    private var mContext:Context?=null
    private var imageAvatar : CustomImageButton?=null
    private var tvTitle : TextView?=null
    private var tvDetail : TextView?=null
    private fun init(context: Context){
        this.mContext=context
        val view= inflate(context, R.layout.item_notification_shimmer_placeholder,this)
        imageAvatar  = view.findViewById(R.id.imgAvatar)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDetail = view.findViewById(R.id.tvDetail)

        imageAvatar?.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(46.0)
        imageAvatar?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(46.0)
    }
}