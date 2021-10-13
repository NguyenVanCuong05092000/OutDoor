package brite.outdoor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.utils.resizeLayout

class CustomShimmerPlaceholderBookMart: ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context)
    }
    private var viewFooter: View? = null
    private var btnUnBookMark: CustomImageButton? = null
    private var btnShare: CustomImageButton? = null
    private var clContent: ConstraintLayout? = null
    private var clImgPost: CardView? = null
    private var imgLocation: ImageView? = null
    private var mContext:Context?=null
    private fun init(context: Context){
        this.mContext=context
        val view = inflate(context, R.layout.item_bookmart_shimmer_placeholder, this)
            val sizeIcon = MyApplication.getInstance().getSizeWithScale(37.0)

        viewFooter = view.findViewById(R.id.viewFooter)
        btnUnBookMark = view.findViewById(R.id.btnUnBookMark)
        btnShare = view.findViewById(R.id.btnShare)
        clContent = view.findViewById(R.id.clContent)
        clImgPost = view.findViewById(R.id.clImgPost)
        imgLocation = view.findViewById(R.id.imgLocation)

        viewFooter?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(65.0)

        btnUnBookMark?.resizeLayout(sizeIcon, sizeIcon)
        btnShare?.resizeLayout(sizeIcon, sizeIcon)

        clContent?.resizeLayout(MyApplication.getInstance().getSizeWithScale(375.0),
            MyApplication.getInstance().getSizeWithScale(160.0))

        clImgPost?.resizeLayout(MyApplication.getInstance().getSizeWithScale(170.0),
            MyApplication.getInstance().getSizeWithScale(122.0))
        imgLocation?.resizeLayout(MyApplication.getInstance().getSizeWithScale(20.0),
            MyApplication.getInstance().getSizeWithScale(20.0))


    }
}