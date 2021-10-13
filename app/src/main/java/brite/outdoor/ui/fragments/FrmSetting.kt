package brite.outdoor.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import brite.outdoor.R
import brite.outdoor.constants.AppConst.Companion.FRM_SETTING
import brite.outdoor.constants.AppConst.Companion.NOTIFICATION_USER
import brite.outdoor.constants.AppConst.Companion.URL_POLICY
import brite.outdoor.constants.AppConst.Companion.URL_REPORT_VIOLATIONS
import brite.outdoor.constants.AppConst.Companion.URL_TERM
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmSettingBinding
import brite.outdoor.ui.dialog.DialogDelete
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.util.*


@AndroidEntryPoint
class FrmSetting : BaseFragment<FrmSettingBinding>(), View.OnClickListener {
    override fun loadControlsAndResize(binding: FrmSettingBinding?) {
        binding?.apply {
            btnBack.resizeLayout(getSizeWithScale(44.00), getSizeWithScale(56.00))
            switchCompat.resizeLayout(getSizeWithScale(53.0), getSizeWithScale(24.0))
            clTopNav.resizeHeight(getSizeWithScale(56.0))

            btnBack.setOnClickListener(this@FrmSetting)
            clLogout.setOnClickListener(this@FrmSetting)
            clTranslation.setOnClickListener(this@FrmSetting)
            clReport.setOnClickListener(this@FrmSetting)
            clTerm.setOnClickListener(this@FrmSetting)
            clPolicy.setOnClickListener(this@FrmSetting)

            switchCompat.setOnCheckedChangeListener { _, _ ->
                if (!isCheckedChangeByCode) {
                    turnOnOffNotification()
                } else {
                    isCheckedChangeByCode = false
                }
            }
        }
    }

    private val authViewModel by viewModels<AuthViewModel>()
    private val viewModel by activityViewModels<AuthViewModel>()
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmSettingBinding {
        return FrmSettingBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
         observeLogoutResult()

    }
    override fun onClick(v: View?) {
        if (!isClickAble()) return
        when (v?.id) {
            R.id.btnBack -> backToPrevious()
            R.id.clLogout -> {
                showDialogLogout()
            }
            R.id.clTranslation ->{
                mActivity?.showLanguage()
            }
            R.id.clReport -> {
                val statusLanguage = PrefManager.getInstance(mActivity!!).getInt(PrefConst.PREF_LANGUAGE)
                openBrowser("$URL_REPORT_VIOLATIONS$statusLanguage")
            }
            R.id.clPolicy -> {
                openBrowser(URL_POLICY)
            }
            R.id.clTerm -> {
                openBrowser(URL_TERM)
            }
        }
    }
    private fun openBrowser(link : String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }
    private fun showDialogLogout() {
        val dialogDelete = DialogDelete(requireContext())
        dialogDelete.apply {
            show(true,
                resources.getString(R.string.lblExit),
                resources.getString(R.string.msgConfirmLogout),
                object :
                    DialogDelete.OnDeleteAgreeOrCancel {
                    override fun onAgree() {
                        callApiLogout()
                    }
                })
        }
    }
    private fun callApiLogout() {
        val requestParam = mActivity!!.getRequestParamWithToken()
        authViewModel.requestLogout(requestParam)
    }

    private fun observeLogoutResult() {
        authViewModel.logoutResult.observe(viewLifecycleOwner, {
            try {
                shareViewModel.showOrHideLoading(it)
                if (it.status == ApiResult.Status.SUCCESS) {
                    mActivity?.logoutSNS()

                    //remove notification
                    NotificationManagerCompat.from(requireContext()).cancel(NOTIFICATION_USER)

                    // change locale
                    val language = PrefManager.getInstance(mActivity!!).getString(PrefConst.PREF_MULTI_LANGUAGE)
                    if (language != null) {
                        viewModel.locale=language
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!isShowOnOffNotification) {
            changeStateNotificationSetting()
        }
    }

    private var isShowOnOffNotification = false
    private fun turnOnOffNotification() {
        try {
            mActivity?.let { nonNullActivity ->
                isShowOnOffNotification = true
                AlertDialog.Builder(nonNullActivity).apply {
                    val message = resources.getString(R.string.msgNotificationTurnOn)
                    setMessage(message)
                    setPositiveButton(resources.getString(R.string.lblNavSetting)) { _, _ ->
                        try {
                            isShowOnOffNotification = false
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", nonNullActivity.packageName, null)
                            intent.data = uri
                            nonNullActivity.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    setNegativeButton(resources.getString(R.string.lblDeny)) { _, _ ->
                        isShowOnOffNotification = false
                        changeStateNotificationSetting()
                    }
                    setOnCancelListener {
                        isShowOnOffNotification = false
                        changeStateNotificationSetting()
                    }
                }.show()
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * check and change state notification setting
     */
    private var isCheckedChangeByCode = false
    private fun changeStateNotificationSetting() {
        if (mActivity != null && getBinding() != null) {
            try {
                val newState = if (NotificationManagerCompat.from(mActivity!!)
                        .areNotificationsEnabled()
                ) 1 else 0

                getBinding()?.switchCompat?.apply {
                    val currentState = if (isChecked) 1 else 0
                    if (currentState != newState) {
                        isCheckedChangeByCode = true
                    }
                    isChecked = newState == 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getCurrentFragment(): Int {
        return FRM_SETTING
    }

    override fun isBackPreviousEnable(): Boolean {
        return true
    }

    override fun backToPrevious() {
        finish()
    }


    override fun finish() {
        mActivity?.closeFuncChildScreen(this)
        val language = PrefManager.getInstance(mActivity!!).getString(PrefConst.PREF_MULTI_LANGUAGE)
        language?.let {
            if (viewModel.locale != language){
                mActivity?.showMyPage(true)
                viewModel.locale=language
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().post(true)
    }

}
