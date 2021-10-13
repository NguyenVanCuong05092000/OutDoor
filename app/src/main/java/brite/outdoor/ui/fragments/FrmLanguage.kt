package brite.outdoor.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import brite.outdoor.R
import brite.outdoor.constants.AppConst.Companion.FRM_LANGUAGE
import brite.outdoor.constants.AppConst.Companion.STATUS_LANGUAGE_EN
import brite.outdoor.constants.AppConst.Companion.STATUS_LANGUAGE_VI
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.databinding.FrmLanguageBinding
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.frm_language.*
import java.util.*

class FrmLanguage : BaseFragment<FrmLanguageBinding>(), View.OnClickListener {

    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun loadControlsAndResize(binding: FrmLanguageBinding?) {
        binding?.apply {
            btnBack.resizeLayout(getSizeWithScale(30.00), getSizeWithScale(20.00))
            clTopNav.resizeHeight(getSizeWithScale(56.0))

            btnBack.setOnClickListener(this@FrmLanguage)
//            clLogout.setOnClickListener(this@FrmSetting)
            clLanguageEnglish.setOnClickListener(this@FrmLanguage)
            clLanguageVietnam.setOnClickListener(this@FrmLanguage)

//            switchCompat.setOnCheckedChangeListener { _, _ ->
//                if (!isCheckedChangeByCode) {
//                    turnOnOffNotification()
//                } else {
//                    isCheckedChangeByCode = true
//                }
//            }
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmLanguageBinding {
        return  FrmLanguageBinding.inflate(inflater,container,false)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }
    private fun setLocale(lang:String){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale=locale

        PrefManager.getInstance(mActivity!!).writeString(PrefConst.PREF_MULTI_LANGUAGE,lang)

        mActivity!!.baseContext.resources.updateConfiguration(config,mActivity!!.baseContext.resources.displayMetrics)

        mActivity?.showSetting()


    }

    override fun getCurrentFragment(): Int {
        return FRM_LANGUAGE
    }

    override fun finish() {
        mActivity?.closeFuncChildScreenLanguage(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.clLanguageVietnam->{
                PrefManager.getInstance(mActivity!!).writeInt(PrefConst.PREF_LANGUAGE,STATUS_LANGUAGE_VI)
                val language = PrefManager.getInstance(mActivity!!).getString(PrefConst.PREF_MULTI_LANGUAGE)
                language?.let {
                    if (it != "vi"){
                        setLocale("vi")
                    }
                    finish()
                }
            }
            R.id.clLanguageEnglish->{
                PrefManager.getInstance(mActivity!!).writeInt(PrefConst.PREF_LANGUAGE, STATUS_LANGUAGE_EN)
                val language = PrefManager.getInstance(mActivity!!).getString(PrefConst.PREF_MULTI_LANGUAGE)
                language?.let {
                    if (it != "en"){
                        setLocale("en")
                    }
                    finish()
                }
            }
            R.id.btnBack->{
                finish()

            }
        }
    }
}