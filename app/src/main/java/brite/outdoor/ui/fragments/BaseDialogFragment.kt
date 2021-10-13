package brite.outdoor.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import brite.outdoor.R
import brite.outdoor.ui.activities.MainActivity
import brite.outdoor.utils.ViewSize
import brite.outdoor.viewmodel.ShareViewModel
import timber.log.Timber


abstract class BaseDialogFragment<T> : DialogFragment() {
    protected val TAG = this.javaClass.simpleName
    private var binding: T? = null

    protected abstract fun loadControlsAndResize(binding: T?)
    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T
    protected fun getBinding() = binding
    protected abstract fun initView(savedInstanceState: Bundle?)

    //
    protected val shareViewModel by activityViewModels<ShareViewModel>()

    //
    protected var mActivity: MainActivity? = null

    //
    protected open fun finish() {
        dismissAllowingStateLoss()
    }

    //============================
    //======  Override  ==========
    //============================
    override fun onAttach(activity: Activity) {
        Timber.d("=>> $TAG: [onAttach]")
        super.onAttach(activity)
        this.mActivity = activity as MainActivity
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dlg = object : Dialog(mActivity!!, theme) {
            override fun onBackPressed() {
                finish()
            }
        }
        dlg.setCancelable(false)
        dlg.setCanceledOnTouchOutside(false)
        return dlg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("=>> $TAG: onCreateView")
        binding = setBinding(inflater, container)
        loadControlsAndResize(binding)
        initView(savedInstanceState)
        return (binding as ViewBinding).root
    }

    override fun onDestroyView() {
        Timber.d("=>> $TAG: onDestroyView")
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("=>> $TAG: onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        if (mActivity == null) mActivity = activity as MainActivity
    }

    override fun onDestroy() {
        Timber.d("=>> $TAG: onDestroy")
        try {
            mHandlerClick.removeCallbacks(changeStateClickAble)
            System.gc()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onResume() {
        Timber.d("=>> $TAG: onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.d("=>> $TAG: onPause")
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("=>> $TAG: onCreate")
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Fullscreen)
    }

    override fun onStart() {
        Timber.d("=>> $TAG: onStart")
        super.onStart()
    }

    override fun onStop() {
        Timber.d("=>> $TAG: onStop")
        super.onStop()
    }

    override fun onDetach() {
        Timber.d("=>> $TAG: [onDetach]")
        super.onDetach()
    }
    //============================
    //======  Resize Manager  ====
    //============================
    protected fun getSizeWithScale(sizeDesign: Double): Int {
        return mActivity!!.getSizeWithScale(sizeDesign)
    }

    protected fun getSizeWithScaleFloat(sizeDesign: Double): Float {
        return mActivity!!.getSizeWithScaleFloat(sizeDesign)
    }

    protected fun getRealWidth(sizeDesign: ViewSize): Int {
        return mActivity!!.getSizeWithScale(sizeDesign.width)
    }

    protected fun getRealHeight(sizeDesign: ViewSize): Int {
        return mActivity!!.getSizeWithScale(sizeDesign.height)
    }

    protected fun getRealSize(sizeDesign: ViewSize): ViewSize {
        return mActivity!!.getRealSize(sizeDesign)
    }

    //============================
    //======  Click Manager  =====
    //============================
    private var mIsClickAble = true
    private val mHandlerClick = Handler()
    private val changeStateClickAble = Runnable { mIsClickAble = true }
    fun isClickAble(): Boolean {
        if (mIsClickAble) {
            mIsClickAble = false
            mHandlerClick.removeCallbacks(changeStateClickAble)
            mHandlerClick.postDelayed(changeStateClickAble, 350)
            return true
        }
        return false
    }
}