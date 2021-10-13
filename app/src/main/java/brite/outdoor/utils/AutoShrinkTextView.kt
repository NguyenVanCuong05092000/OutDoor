package brite.outdoor.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView

class AutoShrinkTextView : AppCompatTextView {
    private val mPaint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth.toFloat()
        if (measuredWidth > 0) {
            shrinkTextSize()
        }
    }

    private fun shrinkTextSize() {
        // テキストサイズを取得します。
        var tempTextSize = textSize
        var text: String? = text.toString()
        if (!TextUtils.isEmpty(text) && text!!.contains("\n")) {
            val arrText = text.split("\n".toRegex()).toTypedArray()
            var currentWidth = 0f
            text = null
            for (child in arrText) {
                val textWidth = getTextWidth(MIN_TEXT_SIZE, child)
                if (currentWidth < textWidth) {
                    text = child
                    currentWidth = textWidth
                }
            }
        }

        while (measuredWidth < getTextWidth(tempTextSize, text)) {
            tempTextSize--
            if (tempTextSize <= MIN_TEXT_SIZE) {
                tempTextSize = MIN_TEXT_SIZE
                break
            }
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, tempTextSize)
    }

    fun getTextWidth(textSize: Float, text: String?): Float {
        mPaint.textSize = textSize
        mPaint.typeface = typeface
        return mPaint.measureText(text)
    }

    fun getTextHeight(textSize: Float, text: String): Float {
        mPaint.textSize = textSize
        val bounds = Rect()
        mPaint.getTextBounds(text, 0, text.length, bounds)
        return bounds.height().toFloat()
    }

    companion object {
        private const val MIN_TEXT_SIZE = 6.0f
    }
}