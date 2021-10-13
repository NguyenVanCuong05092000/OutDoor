package brite.outdoor.utils

class ClickAbleController {
    companion object {
        private var mInstance : ClickAbleController? = null
        fun getInstance() : ClickAbleController {
            synchronized(this) {
                if (mInstance == null) {
                    mInstance = ClickAbleController()
                }
                return mInstance!!
            }
        }



        const val TIME_DELAY = 350L
        private var prevClickedTime = 0L
    }


    fun release() {
        mInstance = null
        prevClickedTime = 0
    }

    fun isClickable() : Boolean {
        val isClickAble = (System.currentTimeMillis() - prevClickedTime) >= TIME_DELAY
        if (isClickAble) prevClickedTime = System.currentTimeMillis()
        return isClickAble
    }
}