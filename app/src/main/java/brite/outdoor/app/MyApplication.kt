package brite.outdoor.app

import android.app.Application
import brite.outdoor.BuildConfig
import brite.outdoor.constants.AppConst
import brite.outdoor.utils.ClickAbleController
import brite.outdoor.utils.ViewSize
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        private lateinit var mInstance: MyApplication
        fun getInstance(): MyApplication = synchronized(this) {
            mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        ClickAbleController.getInstance().release()
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

}