package brite.outdoor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.utils.resizeLayout

class CustomShimmerPlaceholderMypage: ConstraintLayout {

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
    private var tvNumberFollow : TextView?=null
    private var tvNumberFollowDes : TextView?=null
    private var tvNumberFollowers : TextView?=null
    private var tvNumberFollowersDes : TextView?=null
    private var tvNumberLike : TextView?=null
    private var imgNumberLike : ImageView?=null

    private fun init(context: Context){
        this.mContext=context
        val view= inflate(context, R.layout.item_mypage_shimmer_placeholder,this)
        tvNumberFollow = view.findViewById(R.id.tvNumberFollow)
        tvNumberFollowDes = view.findViewById(R.id.tvNumberFollowDes)

        tvNumberFollowers = view.findViewById(R.id.tvNumberFollowers)
        tvNumberFollowersDes = view.findViewById(R.id.tvNumberFollowersDes)

        tvNumberLike = view.findViewById(R.id.tvNumberLike)
        imgNumberLike = view.findViewById(R.id.imgNumberLike)

        val textSizeH1 = MyApplication.getInstance().getSizeWithScaleFloat(30.0)
        val textSizeH2 = MyApplication.getInstance().getSizeWithScaleFloat(14.0)

//        tvNumberFollowers?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH1)
//        tvNumberFollowersDes?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH2)
//
//        tvNumberLike?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH1)
//        imgNumberLike?.resizeLayout( MyApplication.getInstance().getSizeWithScale(20.0), MyApplication.getInstance().getSizeWithScale(20.0))

        tvNumberFollow?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH1)
        tvNumberFollowDes?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeH2)

//        tvNumberFollowers?.resizeLayout( MyApplication.getInstance().getSizeWithScale(60.0), MyApplication.getInstance().getSizeWithScale(20.0))
//        tvNumberFollowersDes?.resizeLayout( MyApplication.getInstance().getSizeWithScale(20.0), MyApplication.getInstance().getSizeWithScale(20.0))
        tvNumberFollow?.resizeLayout( MyApplication.getInstance().getSizeWithScale(20.0), MyApplication.getInstance().getSizeWithScale(35.0))
        tvNumberFollowDes?.resizeLayout( MyApplication.getInstance().getSizeWithScale(60.0), MyApplication.getInstance().getSizeWithScale(20.0))

        tvNumberFollowers?.resizeLayout( MyApplication.getInstance().getSizeWithScale(20.0), MyApplication.getInstance().getSizeWithScale(35.0))
        tvNumberFollowersDes?.resizeLayout( MyApplication.getInstance().getSizeWithScale(120.0), MyApplication.getInstance().getSizeWithScale(20.0))
//
        tvNumberLike?.resizeLayout( MyApplication.getInstance().getSizeWithScale(20.0), MyApplication.getInstance().getSizeWithScale(35.0))
        imgNumberLike?.resizeLayout( MyApplication.getInstance().getSizeWithScale(50.0), MyApplication.getInstance().getSizeWithScale(20.0))





    }
}