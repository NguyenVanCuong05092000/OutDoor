package brite.outdoor.ui.widgets

interface OnSwipeChangeListener {
    /**
     * Called when [SwipeHideable] is swiped (show/hide)
     *
     * @param visible       is now visible?
     * @param swipeHideable swipeHideable
     */
    fun onSwipeChange(visible: Boolean, swipeHideable: SwipeHideable?)
}