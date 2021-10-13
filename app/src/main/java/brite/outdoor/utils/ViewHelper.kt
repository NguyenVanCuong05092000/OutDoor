package brite.outdoor.utils

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


fun View.resizeLayout(w: Int, h: Int) {
    try {
        layoutParams.apply {
            width = w
            height = h
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeWidth(w: Int) {
    try {
        layoutParams.apply {
            width = w
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeWidthWithHeightMatchParent(w: Int) {
    try {
        layoutParams.apply {
            width = w
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeHeight(h: Int) {
    try {
        layoutParams.apply {
            height = h
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.resizeLayout(size: ViewSize) {
    try {
        layoutParams.apply {
            width = size.width.toInt()
            height = size.height.toInt()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.findAndResizeLayout(resId: Int, size: ViewSize) {
    findViewById<View>(resId).resizeLayout(size)
}

fun View.findAndResizeLayout(resId: Int, w: Int, h: Int) {
    findViewById<View>(resId).resizeLayout(w, h)
}

fun View.findAndResizeWidth(resId: Int, w: Int) {
    findViewById<View>(resId).resizeWidth(w)
}

fun View.findAndResizeHeight(resId: Int, h: Int) {
    findViewById<View>(resId).resizeHeight(h)
}

fun TextView.setTextSizePX(textSize: Float) {
    try {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}