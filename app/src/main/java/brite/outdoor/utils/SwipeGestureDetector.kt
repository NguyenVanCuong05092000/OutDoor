package brite.outdoor.utils

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class SwipeGestureDetector : GestureDetector.SimpleOnGestureListener() {
    companion object {
        private const val MIN_SWIPE_DISTANCE_X = 100
        private const val MIN_SWIPE_DISTANCE_Y = 100
    }

    var swipeCallback: SwipeCallback? = null

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || e2 == null) return super.onFling(e1, e2, velocityX, velocityY)
        val deltaX = e1.x - e2.x
        val deltaXAbs = abs(deltaX)
        val deltaY = e1.y - e2.y
        val deltaYAbs = abs(deltaY)

        if (deltaXAbs >= MIN_SWIPE_DISTANCE_X) {
            if (deltaX > 0) {
                swipeCallback?.onLeftSwipe()
            } else {
                swipeCallback?.onRightSwipe()
            }

        }
        if (deltaYAbs>=MIN_SWIPE_DISTANCE_Y){
            if (deltaYAbs>0) {
                swipeCallback?.onTopSwipe()
            }
        }

        return true
    }

    interface SwipeCallback {
        fun onLeftSwipe()

        fun onRightSwipe()

        fun onTopSwipe()
    }

    fun setSwipeCallback(left: ()-> Unit = {}, right: ()-> Unit = {},top:()->Unit={}) {
        swipeCallback = object : SwipeCallback {
            override fun onLeftSwipe() {
                left()
            }

            override fun onRightSwipe() {
                right()
            }

            override fun onTopSwipe() {
                top()
            }
        }
    }
}