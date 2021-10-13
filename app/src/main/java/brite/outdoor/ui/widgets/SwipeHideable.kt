package brite.outdoor.ui.widgets

import android.view.View

interface SwipeHideable {
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
    var direction: Int

    /**
     * Shows view using default speed
     * Changes [View.getVisibility] param using animation
     */
    fun show()

    /**
     * Shows view using defined speed
     * Changes [View.getVisibility] param using animation
     *
     * @param speed speed of sliding animation (ms)
     */
    fun show(speed: Int)

    /**
     * Hide view using default speed
     * Changes [View.getVisibility] param using animation
     */
    fun hide()

    /**
     * Hide view using defined speed
     * Changes [View.getVisibility] param using animation
     *
     * @param speed speed of sliding animation (speed)
     */
    fun hide(speed: Int)

    /**
     * Check if view is visible
     *
     * @return is visible
     */
    val isVisible: Boolean

    /**
     * Enable/disable view for swiping (touching)
     *
     * @param enable enable/disable
     */
    fun enable(enable: Boolean)

    /**
     * Check if view is enable for swiping (touching)
     *
     * @return is enabled
     */
    val isSlideEnabled: Boolean

    /**
     * Sets on swipe change listener
     *
     * @param listener listener
     */
    fun setOnSwipeChangeListener(listener: OnSwipeChangeListener?)
}