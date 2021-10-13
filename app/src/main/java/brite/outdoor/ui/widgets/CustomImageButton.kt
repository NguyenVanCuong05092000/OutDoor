package brite.outdoor.ui.widgets

import android.R
import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class CustomImageButton: AppCompatImageView {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        //setBackground(null);
    }
    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val drawable = drawable ?: return
        val states = drawableState
        val length = states.size
        for (i in 0 until length) {
            if (states[i] == R.attr.state_focused || states[i] == R.attr.state_pressed) {
                drawable.setColorFilter(0x7F000000, PorterDuff.Mode.SRC_ATOP)
                return
            }
        }
        drawable.setColorFilter(0, PorterDuff.Mode.SRC_ATOP)
    }
    fun reinitDrawableState() {
        val states = drawableState
        val length = states.size
        for (i in 0 until length) {
            if (states[i] == R.attr.state_focused || states[i] == R.attr.state_pressed) {
                drawable.setColorFilter(0x7F000000, PorterDuff.Mode.SRC_ATOP)
                return
            }
        }
        drawable.setColorFilter(0, PorterDuff.Mode.SRC_ATOP)
    }

}