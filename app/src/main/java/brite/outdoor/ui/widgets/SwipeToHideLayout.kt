package brite.outdoor.ui.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.Nullable
import brite.outdoor.R


 class SwipeToHideLayout @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr), SwipeHideable {
    private var _yDelta = 0
    private var _xDelta = 0
    private var hide = false
    private var animating = false
    private var _y: Int? = null
    private var _x: Int? = null
    /**
     * Returns current direction or 0 if not set
     *
     * @return direction
     * @see SwipeToHideLayout directions constants
     */
    /**
     * Sets direction of sliding
     *
     * @param direction direction constant from [SwipeToHideLayout]
     * @see SwipeToHideLayout directions constants
     */
    override var direction = DIRECTION_NOT_SET
     override fun show() {
         show(DEFAULT_SPEED)
     }

     /**
     * Check if view is enable for swiping (touching)
     *
     * @return is enabled
     */
    override var isSlideEnabled = true
        private set
    private var listener: OnSwipeChangeListener? = null
    /**
     * Shows view using defined speed
     * Changes [android.view.View.getVisibility] param using animation
     *
     * @param speed speed of sliding animation (ms)
     */
    /**
     * Shows view using default speed
     * Changes [android.view.View.getVisibility] param using animation
     */

    override fun show(speed: Int) {
        startShowAnimation(speed)
    }

     override fun hide() {
         hide(DEFAULT_SPEED)
     }
     /**
     * Hide view using defined speed
     * Changes [android.view.View.getVisibility] param using animation
     *
     * @param speed speed of sliding animation (speed)
     */
    /**
     * Hide view using default speed
     * Changes [android.view.View.getVisibility] param using animation
     */

    override fun hide(speed: Int) {
        startHideAnimation(speed)
    }

    /**
     * Check if view is visible
     *
     * @return is visible
     */
    override val isVisible: Boolean
        get() = visibility == VISIBLE

    /**
     * Enable/disable view for swiping (touching)
     *
     * @param enable enable/disable
     */
    override fun enable(enable: Boolean) {
        isSlideEnabled = enable
    }

    /**
     * Sets on swipe change listener
     *
     * @param listener listener
     */
    override fun setOnSwipeChangeListener(listener: OnSwipeChangeListener?) {
        this.listener = listener
    }

    private fun startHideAnimation(speed: Int) {
        if (direction == DIRECTION_NOT_SET) throw SwipeNoDirectionException("You must set direction!")
        val params = layoutParams as MarginLayoutParams
        var ofValue = 0
        when (direction) {
            DIRECTION_LEFT -> ofValue = params.leftMargin
            DIRECTION_TOP -> ofValue = params.topMargin
            DIRECTION_RIGHT -> ofValue = params.rightMargin
            DIRECTION_BOTTOM -> ofValue = params.bottomMargin
        }
        var valueTo = 0
        when (direction) {
            DIRECTION_LEFT -> valueTo = -width
            DIRECTION_TOP -> valueTo = -height
            DIRECTION_RIGHT -> valueTo = -width
            DIRECTION_BOTTOM -> valueTo = -height
        }
        val animator = ValueAnimator.ofInt(ofValue, valueTo)
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener(AnimatorUpdateListener { animation ->
            if (!animating) {
                animator.cancel()
                return@AnimatorUpdateListener
            }
            when (direction) {
                DIRECTION_LEFT -> params.leftMargin = (animation.animatedValue as Int)
                DIRECTION_TOP -> params.topMargin = (animation.animatedValue as Int)
                DIRECTION_RIGHT -> params.rightMargin = (animation.animatedValue as Int)
                DIRECTION_BOTTOM -> params.bottomMargin = (animation.animatedValue as Int)
            }
            requestLayout()
        })
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (animating) {
                    if (isVisible && listener != null) {
                        listener!!.onSwipeChange(false, this@SwipeToHideLayout)
                    }
                    visibility = GONE
                }
                animating = false
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                animating = true
            }
        })
        animator.duration = speed.toLong()
        animating = true
        animator.start()
    }

    private fun startShowAnimation(speed: Int) {
        if (direction == DIRECTION_NOT_SET) throw SwipeNoDirectionException("You must set direction!")
        val params = layoutParams as MarginLayoutParams
        val wasVisible = isVisible
        var ofValue = 0
        when (direction) {
            DIRECTION_LEFT -> ofValue = params.leftMargin
            DIRECTION_TOP -> ofValue = params.topMargin
            DIRECTION_RIGHT -> ofValue = params.rightMargin
            DIRECTION_BOTTOM -> ofValue = params.bottomMargin
        }
        val animator = ValueAnimator.ofInt(ofValue, 0)
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener(AnimatorUpdateListener { animation ->
            if (!animating) {
                animator.cancel()
                return@AnimatorUpdateListener
            }
            when (direction) {
                DIRECTION_LEFT -> params.leftMargin = (animation.animatedValue as Int)
                DIRECTION_TOP -> params.topMargin = (animation.animatedValue as Int)
                DIRECTION_RIGHT -> params.rightMargin = (animation.animatedValue as Int)
                DIRECTION_BOTTOM -> params.bottomMargin = (animation.animatedValue as Int)
            }
            requestLayout()
        })
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                visibility = VISIBLE
                animating = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (animating) {
                    if (!wasVisible && listener != null) {
                        listener!!.onSwipeChange(true, this@SwipeToHideLayout)
                    }
                }
                animating = false
            }
        })
        animator.duration = speed.toLong()
        animating = true
        animator.start()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!isVisible) {
            val lParams = layoutParams as MarginLayoutParams
            var hiden = true
            when (direction) {
                DIRECTION_LEFT -> hiden = lParams.leftMargin == -width
                DIRECTION_TOP -> hiden = lParams.topMargin == -height
                DIRECTION_RIGHT -> hiden = lParams.rightMargin == -width
                DIRECTION_BOTTOM -> hiden = lParams.bottomMargin == -height
            }
            if (!hiden) {
                hide(0)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (animating) {
            animating = false
        }
        if (isSlideEnabled && isEnabled) {
            val oldY = if (_y == null) ev.rawY.toInt() else _y!!
            _y = ev.rawY.toInt()
            val oldX = if (_x == null) ev.rawX.toInt() else _x!!
            _x = ev.rawX.toInt()
            when (direction) {
                DIRECTION_LEFT -> hide = if (oldX == _x) hide else oldX > _x!!
                DIRECTION_TOP -> hide = if (oldY == _y) hide else oldY > _y!!
                DIRECTION_RIGHT -> hide = if (oldX == _x) hide else oldX < _x!!
                DIRECTION_BOTTOM -> hide = if (oldY == _y) hide else oldY < _y!!
            }
            when (ev.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    val lParams = layoutParams as MarginLayoutParams
                    when (direction) {
                        DIRECTION_LEFT -> _xDelta = _x!! - lParams.leftMargin
                        DIRECTION_TOP -> _yDelta = _y!! - lParams.topMargin
                        DIRECTION_RIGHT -> _xDelta = _x!! + lParams.rightMargin
                        DIRECTION_BOTTOM -> _yDelta = _y!! + lParams.bottomMargin
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> if (hide) {
                    startHideAnimation(DEFAULT_SPEED)
                } else {
                    startShowAnimation(DEFAULT_SPEED)
                }
                MotionEvent.ACTION_MOVE -> {
                    val layoutParams = layoutParams as MarginLayoutParams
                    when (direction) {
                        DIRECTION_LEFT -> layoutParams.leftMargin =
                            if (_x!! - _xDelta > 0) 0 else _x!! - _xDelta
                        DIRECTION_TOP -> layoutParams.topMargin =
                            if (_y!! - _yDelta > 0) 0 else _y!! - _yDelta
                        DIRECTION_RIGHT -> layoutParams.rightMargin =
                            if (_x!! - _xDelta > 0) -(_x!! - _xDelta) else 0
                        DIRECTION_BOTTOM -> layoutParams.bottomMargin =
                            if (_y!! - _yDelta > 0) -(_y!! - _yDelta) else 0
                    }
                    setLayoutParams(layoutParams)
                }
            }
        }
        return true
    }

    companion object {
        private val TAG = SwipeToHideLayout::class.java.simpleName

        /**
         * default no direction
         */
        const val DIRECTION_NOT_SET = 0

        /**
         * left direction
         */
        const val DIRECTION_LEFT = 1

        /**
         * top direction
         */
        const val DIRECTION_TOP = 2

        /**
         * right direction
         */
        const val DIRECTION_RIGHT = 3

        /**
         * bottom direction
         */
        const val DIRECTION_BOTTOM = 4
        private const val DEFAULT_SPEED = 300
    }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SwipeToHideLayout, 0, 0)
        try {
            direction = a.getInteger(R.styleable.SwipeToHideLayout_direction, DIRECTION_NOT_SET)
            isSlideEnabled = a.getBoolean(R.styleable.SwipeToHideLayout_enabled, true)
        } finally {
            a.recycle()
        }
    }
}