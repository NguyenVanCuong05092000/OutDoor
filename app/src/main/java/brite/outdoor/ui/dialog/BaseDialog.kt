package brite.outdoor.ui.dialog

import android.app.Dialog
import android.content.Context
import brite.outdoor.R
import brite.outdoor.constants.AppConst
import brite.outdoor.utils.ViewSize

abstract class BaseDialog(val mContext : Context) : Dialog(mContext, R.style.dialogWithoutBox)  {
    //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue = mContext.resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
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
}