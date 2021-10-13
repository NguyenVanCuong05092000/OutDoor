package brite.outdoor.utils

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlin.math.abs

abstract class AppBarStateChangeListener : OnOffsetChangedListener {
    private var mCurrentState = IDLE
    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        mCurrentState = when {
            i == 0 -> {
                if (mCurrentState != EXPANDED) {
                    onStateChanged(appBarLayout, EXPANDED)
                }
                EXPANDED
            }
            abs(i) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState != COLLAPSED) {
                    onStateChanged(appBarLayout, COLLAPSED)
                }
                COLLAPSED
            }
            else -> {
                if (mCurrentState != IDLE) {
                    onStateChanged(appBarLayout, IDLE)
                }
                IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: Int)

    companion object {
        const val EXPANDED = 0
        const val COLLAPSED = 1
        const val IDLE = 2
    }
}