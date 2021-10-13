package brite.outdoor.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import brite.outdoor.R
import brite.outdoor.constants.AppConst
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.databinding.LayourImagePickedPreviewBinding
import brite.outdoor.utils.*
import brite.outdoor.utils.loadImageWithCustomCorners
import com.bumptech.glide.Glide

class LayoutImagePickedPreview : ConstraintLayout, View.OnClickListener {
    private var widthOne: Int = 0
    private var widthTwo: Int = 0
    private var widthMainThree: Int = 0
    private var heightChildThree: Int = 0
    private var widthMainFour: Int = 0
    private var widthChildFour: Int = 0

    private var canChange = false

    var listImage :List<ImagePicker>?=null

     fun getCountItem():Int{
         return  listImage?.size ?: 0
     }

    interface OnClickListener {
        fun onClickImage(position : Int)
        fun onClickDelete(position : Int)
    }

    private var onClickListener : OnClickListener? = null
    fun setOnClickListener(onClickListener : OnClickListener?) {
        this.onClickListener = onClickListener
    }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs!!) {
        initView(context, attrs)
    }

    var binding: LayourImagePickedPreviewBinding? = null
    private fun initView(context: Context, attrs: AttributeSet?) {

        if (attrs != null) {
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.LayoutImagePickedPreview)
            canChange = a.getBoolean(R.styleable.LayoutImagePickedPreview_canChange, false)
            a.recycle()
        }

        binding = LayourImagePickedPreviewBinding.inflate(LayoutInflater.from(context))
        binding?.clRoot?.resizeLayout(getSizeWithScale(351.0), getSizeWithScale(320.0))

        widthOne = getSizeWithScale(351.0)
        widthTwo = getSizeWithScale(171.5)

        widthMainThree = getSizeWithScale(171.5)
        heightChildThree = getSizeWithScale(155.5)

        widthMainFour = getSizeWithScale(242.0)
        widthChildFour = getSizeWithScale(101.0)

        val wBtnDelete = getSizeWithScale(36.0)
        val hBtnDelete = getSizeWithScale(30.0)

        val whIconVideo = getSizeWithScale(24.0)

        val whProgressLoading = getSizeWithScale(40.0)

        binding?.apply {
            imgDeleteMain.resizeLayout(wBtnDelete, hBtnDelete)
            imgDeleteChild1.resizeLayout(wBtnDelete, hBtnDelete)
            imgDeleteChild2.resizeLayout(wBtnDelete, hBtnDelete)
            imgDeleteChild3.resizeLayout(wBtnDelete, hBtnDelete)

            imgVideoMain.resizeLayout(whIconVideo, whIconVideo)
            imgVideo1.resizeLayout(whIconVideo, whIconVideo)
            imgVideo2.resizeLayout(whIconVideo, whIconVideo)
            imgVideo3.resizeLayout(whIconVideo, whIconVideo)

            imgDeleteMain.visibility = if (canChange) View.VISIBLE else View.GONE
            imgDeleteChild1.visibility = if (canChange) View.VISIBLE else View.GONE
            imgDeleteChild2.visibility = if (canChange) View.VISIBLE else View.GONE
            imgDeleteChild3.visibility = if (canChange) View.VISIBLE else View.GONE

            progressMain.resizeLayout(whProgressLoading,whProgressLoading)
            progressChild1.resizeLayout(whProgressLoading,whProgressLoading)
            progressChild2.resizeLayout(whProgressLoading,whProgressLoading)
            progressChild3.resizeLayout(whProgressLoading,whProgressLoading)

            if (canChange) {
                imgDeleteMain.setOnClickListener(this@LayoutImagePickedPreview)
                imgDeleteChild1.setOnClickListener(this@LayoutImagePickedPreview)
                imgDeleteChild2.setOnClickListener(this@LayoutImagePickedPreview)
                imgDeleteChild3.setOnClickListener(this@LayoutImagePickedPreview)
            }
        }

        this.addView(binding!!.root)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.imgDeleteMain -> {
                onClickListener?.onClickDelete(0)
            }
            R.id.imgDeleteChild1 -> {
                onClickListener?.onClickDelete(1)
            }
            R.id.imgDeleteChild2 -> {
                onClickListener?.onClickDelete(2)
            }
            R.id.imgDeleteChild3 -> {
                onClickListener?.onClickDelete(if (binding?.imgChild2?.isShown == true) 3 else 2)
            }
        }
    }

    //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue = resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }

    fun getSizeWithScale(sizeDesign: Double): Int {
        return (sizeDesign * getScaleValue()).toInt()
    }

    fun getSizeWithScaleFloat(sizeDesign: Double): Float {
        return (sizeDesign * getScaleValue()).toFloat()
    }

    fun getRealSize(sizeDesign: ViewSize): ViewSize {
        return ViewSize(sizeDesign.width * getScaleValue(), sizeDesign.height * getScaleValue())
    }

    //End Resize
    @SuppressLint("SetTextI18n")
    fun initData(
        mContext: Context,
        listImage: List<ImagePicker>?
    ) {
        listImage?.let {
            this.listImage=listImage
            binding?.apply {
//                tvFourPlus.visibility = if (it.size > 4) View.VISIBLE else View.GONE
                if (it.size>4){
                    tvFourPlus.visibility = View.VISIBLE
                    tvFourPlus.text = it.size.minus(4).toString() +"+"
                }else{
                    tvFourPlus.visibility = View.GONE
                }
                when (it.size) {
                    0 -> {
                        clMain.visibility = View.VISIBLE
                        clImgChild1.visibility = View.GONE
                        clImgChild2.visibility = View.GONE
                        clImgChild3.visibility = View.GONE
                        clBgIconVideoMain.visibility=View.GONE
                        if (canChange) {
                            imgDeleteMain.visibility = View.GONE
                            imgDeleteChild1.visibility = View.GONE
                            imgDeleteChild2.visibility = View.GONE
                            imgDeleteChild3.visibility = View.GONE
                        }

                        imgMain.resizeWidthWithHeightMatchParent(widthOne)
                        Glide.with(mContext).load(R.drawable.ic_no_images).into(imgMain)
                    }
                    1 -> {
                        clMain.visibility = View.VISIBLE
                        clImgChild1.visibility = View.GONE
                        clImgChild2.visibility = View.GONE
                        clImgChild3.visibility = View.GONE
                        clBgIconVideoMain.visibility = if (it[0].isVideo) View.VISIBLE else View.GONE

                        clMain.resizeWidthWithHeightMatchParent(widthOne)

                        if (canChange) {
                            imgDeleteMain.visibility = View.VISIBLE
                            imgDeleteChild1.visibility = View.GONE
                            imgDeleteChild2.visibility = View.GONE
                            imgDeleteChild3.visibility = View.GONE

                            imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)

                        }else{
                            progressMain.visibility= View.VISIBLE

                            if (it[0].isVideo){
                                if (it[0].thumbnail==null) imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                                else imgMain.loadImageWithProgressAndCustomCorners(it[0].thumbnail,1,progressMain)
                            }else{
                                imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                            }

                        }

                    }
                    2 -> {
                        clMain.visibility = View.VISIBLE
                        clImgChild1.visibility = View.VISIBLE
                        clImgChild2.visibility = View.GONE
                        clImgChild3.visibility = View.GONE
                        clBgIconVideoMain.visibility = if (it[0].isVideo) View.VISIBLE else View.GONE
                        clBgIconVideo1.visibility = if (it[1].isVideo) View.VISIBLE else View.GONE

                        clMain.resizeWidthWithHeightMatchParent(widthTwo)
                        clImgChild1.resizeWidthWithHeightMatchParent(widthTwo)
                        if (canChange) {
                            imgDeleteMain.visibility = View.VISIBLE
                            imgDeleteChild1.visibility = View.VISIBLE
                            imgDeleteChild2.visibility = View.GONE
                            imgDeleteChild3.visibility = View.GONE

                            imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                            imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                        }else{
                            progressMain.visibility= View.VISIBLE
                            progressChild1.visibility=View.VISIBLE

                            if (it[0].isVideo){
                                if (it[0].thumbnail==null) imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                                else imgMain.loadImageWithProgressAndCustomCorners(it[0].thumbnail,1,progressMain)
                            }else{
                                imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                            }
                            if (it[1].isVideo){
                                if (it[1].thumbnail==null) imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                                else imgChild1.loadImageWithProgressAndCustomCorners(it[1].thumbnail,1,progressChild1)

                            }else{
                                imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                            }
                        }


                    }
                    3 -> {
                        clMain.visibility = View.VISIBLE
                        clImgChild1.visibility = View.VISIBLE
                        clImgChild2.visibility = View.GONE
                        clImgChild3.visibility = View.VISIBLE
                        clBgIconVideoMain.visibility = if (it[0].isVideo) View.VISIBLE else View.GONE
                        clBgIconVideo1.visibility = if (it[1].isVideo) View.VISIBLE else View.GONE
                        clBgIconVideo2.visibility = if (it[2].isVideo) View.VISIBLE else View.GONE
                        clBgIconVideo3.visibility = if (it[2].isVideo) View.VISIBLE else View.GONE

                        clMain.resizeWidthWithHeightMatchParent(widthMainThree)
                        clImgChild1.resizeLayout(widthMainThree, heightChildThree)
                        clImgChild3.resizeLayout(widthMainThree, heightChildThree)

                        if (canChange) {
                            imgDeleteMain.visibility = View.VISIBLE
                            imgDeleteChild1.visibility = View.VISIBLE
                            imgDeleteChild2.visibility = View.GONE
                            imgDeleteChild3.visibility = View.VISIBLE

                            imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                            imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                            imgChild3.loadImageWithProgressAndCustomCorners(it[2].uri,1,progressChild3)

                        }else{
                            progressMain.visibility= View.VISIBLE
                            progressChild1.visibility=View.VISIBLE
                            progressChild3.visibility=View.VISIBLE

                            if (it[0].isVideo){
                                if (it[0].thumbnail==null) imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                                else imgMain.loadImageWithProgressAndCustomCorners(it[0].thumbnail,1,progressMain)
                            }else{
                                imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                            }
                            if (it[1].isVideo){
                                if (it[1].thumbnail==null) imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                                else imgChild1.loadImageWithProgressAndCustomCorners(it[1].thumbnail,1,progressChild1)
                            }else{
                                imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                            }
                            if (it[2].isVideo){
                                if (it[2].thumbnail==null) imgChild3.loadImageWithProgressAndCustomCorners(it[2].uri,1,progressChild3)
                                else imgChild3.loadImageWithProgressAndCustomCorners(it[2].thumbnail,1,progressChild3)
                            }else{
                                imgChild3.loadImageWithProgressAndCustomCorners(it[2].uri,1,progressChild3)
                            }
                        }


                    }
                    else -> {
                        clMain.visibility = View.VISIBLE
                        clImgChild1.visibility = View.VISIBLE
                        clImgChild2.visibility = View.VISIBLE
                        clImgChild3.visibility = View.VISIBLE
                        clBgIconVideoMain.visibility = if (it[0].isVideo) View.VISIBLE else View.GONE
                        clBgIconVideo1.visibility = if (it[1].isVideo) View.VISIBLE else View.GONE
                        clBgIconVideo2.visibility = if (it[2].isVideo) View.VISIBLE else View.GONE
                        clBgIconVideo3.visibility = if (it[3].isVideo) View.VISIBLE else View.GONE


                        clMain.resizeWidthWithHeightMatchParent(widthMainFour)
                        clImgChild1.resizeLayout(widthChildFour, widthChildFour)
                        clImgChild2.resizeLayout(widthChildFour, widthChildFour)
                        clImgChild3.resizeLayout(widthChildFour, widthChildFour)

                        if (canChange) {
                            imgDeleteMain.visibility = View.VISIBLE
                            imgDeleteChild1.visibility = View.VISIBLE
                            imgDeleteChild2.visibility = View.VISIBLE
                            imgDeleteChild3.visibility = View.VISIBLE

                            imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                            imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                            imgChild2.loadImageWithProgressAndCustomCorners(it[2].uri,1,progressChild2)
                            imgChild3.loadImageWithProgressAndCustomCorners(it[3].uri,1,progressChild3)
                        }else{
                            progressMain.visibility= View.VISIBLE
                            progressChild1.visibility=View.VISIBLE
                            progressChild2.visibility=View.VISIBLE
                            progressChild3.visibility=View.VISIBLE

                            if (it[0].isVideo){
                                if (it[0].thumbnail==null) imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                                else imgMain.loadImageWithProgressAndCustomCorners(it[0].thumbnail,1,progressMain)
                            }else{
                                imgMain.loadImageWithProgressAndCustomCorners(it[0].uri,1,progressMain)
                            }
                            if (it[1].isVideo){
                                if (it[1].thumbnail==null) imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                                else imgChild1.loadImageWithProgressAndCustomCorners(it[1].thumbnail,1,progressChild1)
                            }else{
                                imgChild1.loadImageWithProgressAndCustomCorners(it[1].uri,1,progressChild1)
                            }
                            if (it[2].isVideo){
                                if (it[2].thumbnail==null) imgChild2.loadImageWithProgressAndCustomCorners(it[2].uri,1,progressChild2)
                                else imgChild2.loadImageWithProgressAndCustomCorners(it[2].thumbnail,1,progressChild2)
                            }else{
                                imgChild2.loadImageWithProgressAndCustomCorners(it[2].uri,1,progressChild2)
                            }
                            if (it[3].isVideo){
                                if (it[3].thumbnail==null) imgChild3.loadImageWithProgressAndCustomCorners(it[3].uri,1,progressChild3)
                                else imgChild3.loadImageWithProgressAndCustomCorners(it[3].thumbnail,1,progressChild3)
                            }else{
                                imgChild3.loadImageWithProgressAndCustomCorners(it[3].uri,1,progressChild3)
                            }
                        }





                    }
                }
            }

        }
    }
}