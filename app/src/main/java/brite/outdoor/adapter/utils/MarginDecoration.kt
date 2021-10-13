package brite.outdoor.adapter.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class MarginDecoration() : RecyclerView.ItemDecoration() {

    private var mSizeGridSpacingPx = 0
    private var mGridSize = 0

    constructor(gridSpacingPx: Int, gridSize: Int) : this() {
        mSizeGridSpacingPx = gridSpacingPx
        mGridSize = gridSize
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {

        val frameWidth = (parent.width - mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize
        val padding = parent.width / mGridSize - frameWidth

        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition < mGridSize) {
            outRect.top = 0
        } else {
            outRect.top = mSizeGridSpacingPx
        }

        when {
            itemPosition % mGridSize == 0 -> {
                outRect.left = 0
                outRect.right = mSizeGridSpacingPx / 2
            }
            itemPosition % mGridSize == 1 -> {
                outRect.left = mSizeGridSpacingPx - padding
                outRect.right = mSizeGridSpacingPx - padding
            }
            itemPosition % mGridSize == 2 -> {
                outRect.right = 0
                outRect.left = padding
            }
        }

        outRect.bottom = 0
    }

}