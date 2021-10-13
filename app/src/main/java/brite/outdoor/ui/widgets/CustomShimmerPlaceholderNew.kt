package brite.outdoor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.utils.setTextSizePX

class CustomShimmerPlaceholderNew: ConstraintLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context)
    }
    private var mContext: Context? = null
    private var clItemNews: ConstraintLayout? = null
    private var clTitleAndImg: ConstraintLayout? = null
    private var imgAvatar: CustomImageButton? = null
    private var imgMenu: CustomImageButton? = null
    private var btnLike: CustomLayoutInteractive? = null
    private var btnComment: CustomLayoutInteractive? = null
    private var btnShare: CustomLayoutInteractive? = null
    private var viewLine: View? = null
    private var tvMonth: NoPaddingTextView? = null
    private var tvDay: NoPaddingTextView? = null
    private fun init(context: Context) {
        this.mContext = context
        val view = inflate(context, R.layout.item_news_shimmer_placeholder, this)
        clItemNews = view.findViewById(R.id.clItemNews)
        clTitleAndImg = view.findViewById(R.id.clTitleAndImg)
        imgAvatar = view.findViewById(R.id.imgAvatar)
        imgMenu = view.findViewById(R.id.imgMenu)
        btnLike = view.findViewById(R.id.btnLike)

        btnComment = view.findViewById(R.id.btnComment)
        btnShare = view.findViewById(R.id.btnShare)
        viewLine = view.findViewById(R.id.viewLine)
        tvMonth = view.findViewById(R.id.tvMonth)
        tvDay = view.findViewById(R.id.tvDay)

        clItemNews?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(412.0)
        clTitleAndImg?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(256.0)
        imgAvatar?.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(46.0)
        imgAvatar?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(46.0)
        imgMenu?.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(23.0)
        imgMenu?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(28.0)
        btnComment?.setSize(13F)
        btnLike?.setSize(13F)
        btnShare?.setSize(13F)
        viewLine?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(95.0)

        tvMonth?.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(18.0))
        tvDay?.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(48.0))
    }

}