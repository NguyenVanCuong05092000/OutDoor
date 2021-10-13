package brite.outdoor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView


class MultiClickRecyclerView : RecyclerView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && this.scrollState == SCROLL_STATE_SETTLING) {
            Log.e("TAG", "onInterceptTouchEvent: ", )
            stopScroll()
        }
        return super.onInterceptTouchEvent(event)
    }
}