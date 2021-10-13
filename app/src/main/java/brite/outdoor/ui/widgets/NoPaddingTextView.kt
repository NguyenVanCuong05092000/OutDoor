package brite.outdoor.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class NoPaddingTextView : AppCompatTextView {
    private val mPaint = Paint()

    private val mBounds = Rect()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mPaint.typeface = typeface
        includeFontPadding = false
    }

    override fun onDraw(canvas: Canvas?) {
        val text = calculateTextParams()
        val left = mBounds.left
        val bottom = mBounds.bottom
        mBounds.offset(-mBounds.left, -mBounds.top)
        mPaint.isAntiAlias = true
        mPaint.color = currentTextColor
        canvas!!.drawText(text, -left.toFloat(), (mBounds.bottom - bottom).toFloat(), mPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculateTextParams()
        setMeasuredDimension(mBounds.width() + 1, mBounds.height())
    }

    private fun calculateTextParams(): String {
        val text = text.toString()
        val textLength = text.length
        mPaint.textSize = textSize
        mPaint.getTextBounds(text, 0, textLength, mBounds)
        if (textLength == 0) {
            mBounds.right = mBounds.left
        }
        return text
    }
}