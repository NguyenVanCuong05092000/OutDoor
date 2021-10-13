package brite.outdoor.ui.widgets

import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import brite.outdoor.R

class MyClickableSpan(context: Context?, textColor: Int, private val onClick : () -> Unit) : ClickableSpan() {
    private var textColor = 0
    private var typeface: Typeface? = null
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.typeface = typeface
        ds.isUnderlineText = false // get rid of underlining
        ds.color = textColor // make links red
    }

    override fun onClick(widget: View) {
        Log.d("TAG", "onClick: ")
        onClick.invoke()
    }

    init {
        this.textColor = textColor
        typeface = ResourcesCompat.getFont(context!!, R.font.semi_bold)
    }
}