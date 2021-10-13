package brite.outdoor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import brite.outdoor.R
import brite.outdoor.adapter.AdapterViewPageIntro
import brite.outdoor.constants.AppConst.Companion.FRM_INTRO
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.databinding.FrmIntroBinding

class FrmIntro : BaseFragment<FrmIntroBinding>() {
    private var counterPageScroll = 0
    private val mTextIntro = intArrayOf(
            R.string.lblTextIntro1,
            R.string.lblTextIntro2,
            R.string.lblTextIntro3,
            R.string.lblTextIntro4,
            R.string.lblTextIntro5
    )

    override fun loadControlsAndResize(binding: FrmIntroBinding?) {
        binding?.apply {
            clTextIntro.layoutParams.height = getSizeWithScale(175.0)
            tvIntro.layoutParams.width = getSizeWithScale(293.0)
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmIntroBinding {
        return FrmIntroBinding.inflate(inflater, container, false)

    }

    override fun initView(savedInstanceState: Bundle?) {
//        val mWidthImages = getSizeWithScale(375.5)
//        val mHeightImages = getSizeWithScale(812.0)
        getBinding()?.apply {
            vpIntroduction.adapter = AdapterViewPageIntro(mActivity!!)
            vpIntroduction.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    tvIntro.text = resources.getString(mTextIntro[position])
                    if (position == mTextIntro.size - 1) {
                        if (counterPageScroll != 0) {
                            gotoLogin()
                        }
                        counterPageScroll++
                    } else {
                        counterPageScroll = 0
                    }

                }

                override fun onPageSelected(position: Int) {}
                override fun onPageScrollStateChanged(state: Int) {}
            })
            tvSkip.setOnClickListener {
                gotoLogin()
            }
        }
    }

    private fun gotoLogin() {
        PrefManager.getInstance(mActivity!!).writeBoolean(PrefConst.PREF_IS_FIRST_OPEN_APP, true)
        mActivity?.showLogin()
    }

    override fun getCurrentFragment(): Int {
        return FRM_INTRO
    }

    override fun finish() {

    }
}
