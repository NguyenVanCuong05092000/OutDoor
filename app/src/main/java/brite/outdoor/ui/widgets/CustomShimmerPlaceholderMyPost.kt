package brite.outdoor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.databinding.ItemMypostShimmerPlaceholderBinding
import brite.outdoor.databinding.LayourImagePickedPreviewBinding
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.utils.setTextSizePX

class CustomShimmerPlaceholderMyPost: ConstraintLayout {

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
    private var btnLike: CustomLayoutInteractive? = null
    private var btnComment: CustomLayoutInteractive? = null
    private var btnShare: CustomLayoutInteractive? = null


    private fun init(context: Context){
        this.mContext = context
        val view = inflate(context, R.layout.item_mypost_shimmer_placeholder, this)
        clItemNews = view.findViewById(R.id.clItemNews)
        clTitleAndImg = view.findViewById(R.id.clTitleAndImg)
        btnLike = view.findViewById(R.id.btnLike)

        btnComment = view.findViewById(R.id.btnComment)
        btnShare = view.findViewById(R.id.btnShare)

        clItemNews?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(350.0)
        clTitleAndImg?.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(256.0)
        btnComment?.setSize(13F)
        btnLike?.setSize(13F)
        btnShare?.setSize(13F)



    }
}