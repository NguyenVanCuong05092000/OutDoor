package brite.outdoor.ui.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import brite.outdoor.R


class ProgressView : AppCompatImageView {
    constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(context!!, attrs, defStyle) {
        setAnimation(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context!!, attrs) {
        setAnimation(attrs)
    }

    constructor(context: Context?) : super(context!!) {}

    private fun setAnimation(attrs: AttributeSet) {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressView)
        val frameCount = a.getInt(R.styleable.ProgressView_frameCount, 8)
        val duration = a.getInt(R.styleable.ProgressView_duration, 800)
        a.recycle()
        setAnimation(frameCount, duration)
    }

    fun setAnimation(frameCount: Int, duration: Int) {
        val a = AnimationUtils.loadAnimation(context,R.anim.anim_loading_video)
        a.duration = duration.toLong()
        a.setInterpolator(object : Interpolator{
            override fun getInterpolation(input: Float): Float {
                return Math.floor((input * frameCount).toDouble()).toFloat() / frameCount
            }
        })
        startAnimation(a)
    }
}