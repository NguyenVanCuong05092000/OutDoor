package brite.outdoor.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import brite.outdoor.R
import brite.outdoor.constants.AppConst.Companion.FRM_LOGIN
import brite.outdoor.constants.AppConst.Companion.LOGIN_WITH_FACEBOOK
import brite.outdoor.constants.AppConst.Companion.LOGIN_WITH_GOOGLE
import brite.outdoor.constants.AppConst.Companion.LOGIN_WITH_UNKNOWN
import brite.outdoor.constants.AppConst.Companion.URL_POLICY
import brite.outdoor.constants.AppConst.Companion.URL_TERM
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_BACK_STACK
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ResponseCreateHash
import brite.outdoor.data.api_entities.response.ResponseLogin
import brite.outdoor.data.entities.BackStackData
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.local.pref.PrefStr
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.FrmLoginBinding
import brite.outdoor.ui.widgets.MyClickableSpan
import brite.outdoor.utils.resizeLayout
import brite.outdoor.viewmodel.AuthViewModel
import com.facebook.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frm_login.*
import java.util.*


@AndroidEntryPoint

class FrmLogin : BaseFragment<FrmLoginBinding>(), View.OnClickListener {

    companion object {
        fun getInstance(
            arrayList: ArrayList<BackStackData>?,
            mapData: HashMap<String, Any>?
        ): FrmLogin {
            val fragment = FrmLogin()
            arrayList?.let {
                fragment.arguments = Bundle().apply {
                    putSerializable(EXTRA_BACK_STACK, it)
                }
            }
            return fragment
        }
    }

    private val authViewModel by activityViewModels<AuthViewModel>()
    override fun loadControlsAndResize(binding: FrmLoginBinding?) {
        binding?.apply {
            //Calculator
            val widthButton = getSizeWithScale(302.0)
            val heightButton = getSizeWithScale(46.0)
            val radiusButton = getSizeWithScaleFloat(23.0)
            val textSizeButton = getSizeWithScaleFloat(15.0)
            //Resize
            cvLoginFacebook.resizeLayout(widthButton, heightButton)
            cvLoginGoogle.resizeLayout(widthButton, heightButton)
            cvLoginFacebook.radius = radiusButton
            cvLoginGoogle.radius = radiusButton
            tvLoginWithFacebook.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeButton)
            tvLoginWithGoogle.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSizeButton)

            imgLogo.resizeLayout(getSizeWithScale(149.0), getSizeWithScale(123.0))
            imgLogoFacebook.resizeLayout(getSizeWithScale(12.58), getSizeWithScale(24.0))
            imgLogoGoogle.resizeLayout(getSizeWithScale(27.0), getSizeWithScale(27.0))
            //Click listener
            btnLoginFacebook.setOnClickListener(this@FrmLogin)
            btnLoginGoogle.setOnClickListener(this@FrmLogin)
//            tvTos.setOnClickListener(this@FrmLogin)
//            tvPrivacyPolicy.setOnClickListener(this@FrmLogin)
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmLoginBinding {
        return FrmLoginBinding.inflate(inflater, container, false)
    }


    override fun initView(savedInstanceState: Bundle?) {
        setupPolicyClickable()
        observeLoginResult()
        observerCreateHash()

    }

    private fun setupPolicyClickable() {
        try {
            val spanBuilder = SpannableStringBuilder()
            insertToStringBuilder(spanBuilder,R.string.lblStartPolicy)
            setSpanToStringBuilder(spanBuilder,R.string.lblTOS,true) {
                openBrowser(URL_TERM)
            }
            insertToStringBuilder(spanBuilder,R.string.lblAnd)
            setSpanToStringBuilder(spanBuilder,R.string.lbl_privacy_policy,true) {
                openBrowser(URL_POLICY)
            }
            getBinding()?.tvPolicy?.movementMethod = LinkMovementMethod.getInstance();
            getBinding()?.tvPolicy?.text = spanBuilder
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openBrowser(link : String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    private fun insertToStringBuilder(stringBuilder: SpannableStringBuilder,
                                      resId: Int) {
        try {
            mActivity?.apply {
                val value = resources.getString(resId)
                stringBuilder.insert(stringBuilder.length, value)
                stringBuilder.insert(stringBuilder.length, " ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSpanToStringBuilder(
        stringBuilder: SpannableStringBuilder,
        resId: Int,needInsertSpace : Boolean,
        onClick : () -> Unit
    ) {
        try {
            mActivity?.apply {
                val value = resources.getString(resId)
                stringBuilder.insert(stringBuilder.length, value)
                stringBuilder.setSpan(
                    MyClickableSpan(mActivity!!, mActivity!!.resources.getColor(R.color.colorRedPlaceUtensils),onClick),
                    stringBuilder.length - value.length,
                    stringBuilder.length - value.length + value.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (needInsertSpace)
                    stringBuilder.insert(stringBuilder.length, " ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun observeLoginResult() {
        authViewModel.loginResult.observe(viewLifecycleOwner, {
            try {
                if (it.status != ApiResult.Status.CONSUMED) {
                    shareViewModel.showOrHideLoading(it)
                    if (it.status == ApiResult.Status.SUCCESS) {
                            (it.data?.response as ResponseLogin.Response?)?.let { response ->
//                                if (response.merge) {
//                                    val dialog = DialogTwoChoose(requireContext())
//                                    dialog.show("Ban co muon merge tai khoan khong?",false,object :DialogTwoChoose.OnClickListener{
//                                        override fun onClickRight() {
//                                            Log.e(TAG, "onClickRight: ", )
//                                        }
//
//                                        override fun onClickLeft() {
//                                            createHashAndSendEmail(response.id.toString())
//                                        }
//
//                                    })
//
//                                }else{
//                                    Log.e(TAG, "observeLoginResult: $response", )
//
//                                }
                                writeDataPref(response)
                                mActivity?.callApiRegisterTokenPush()
                                mActivity?.callApiNotifyUnread()
                                mActivity?.showHome()
                                mActivity?.setup()
                            }
                           
                    } else if (it.status == ApiResult.Status.ERROR) {
                        when (authViewModel.loggingWith) {
                            LOGIN_WITH_FACEBOOK -> { mActivity?.logoutFacebook() }
                            LOGIN_WITH_GOOGLE -> { mActivity?.logoutGoogle() }
                        }
                        authViewModel.loggingWith = LOGIN_WITH_UNKNOWN
                    }
                    it.status = ApiResult.Status.CONSUMED
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun onClick(v: View?) {
        if (!isClickAble()) return
        when (v?.id) {
            R.id.btnLoginFacebook -> mActivity?.loginFacebook()
            R.id.btnLoginGoogle -> mActivity?.loginGoogle()
//            R.id.tvTos -> {

//            }
//            R.id.tvPrivacyPolicy -> {

//            }

        }
    }

    private fun writeDataPref(data: ResponseLogin.Response) {
        mActivity?.apply {
            try {
                
                PrefManager.getInstance(this)
                        .writeString(PrefConst.PREF_LOGIN_WITH, authViewModel.loggingWith)

                data.token.let { updateToken(it) }
                PrefManager.getInstance(this).writeStr(
                    PrefStr(PrefConst.PREF_USER_ID,  data.id.toString()),
                    PrefStr(PrefConst.PREF_USER_AVATAR, data.avatar.toString()),
                    PrefStr(PrefConst.PREF_NAME, data.name.toString()),
                    PrefStr(PrefConst.PREF_STATUS,  data.status.toString()),
                    PrefStr(PrefConst.PREF_GENDER, data.gender.toString()),
                    PrefStr(PrefConst.PREF_BIRTHDAY,  data.gender.toString()),
                    PrefStr(PrefConst.PREF_DEVICE_TYPE, data.device_type.toString()),
                    PrefStr(PrefConst.PREF_REGISTER_DEVICE_UUID,  data.register_device_uuid.toString()),
                    PrefStr(PrefConst.PREF_CURRENT_DEVICE_UUID, data.current_device_uuid.toString()),
                    PrefStr(PrefConst.PREF_VERSION_DATA,  data.version_data.toString()),
                    PrefStr(PrefConst.PREF_USER_AVATAR, data.avatar.toString()),
                    PrefStr(PrefConst.PREF_TOKEN,  data.token.toString()),
                    PrefStr(PrefConst.PREF_FIREBASE_TOKEN, data.firebase_token.toString()),
                    PrefStr(PrefConst.PREF_IS_DELETE,  data.is_deleted.toString()),
                    PrefStr(PrefConst.PREF_CREATED_TIME, data.created_time.toString()),
                    PrefStr(PrefConst.PREF_MODIFIED_TIME, data.modified_time.toString()),
                    PrefStr(PrefConst.PREF_LIKE_COUNTS,  data.like_counts.toString()),
                    PrefStr(PrefConst.PREF_SHARE_COUNTS, data.share_counts.toString()),
                    PrefStr(PrefConst.PREF_FOLLOW_COUNTS,  data.follow_counts.toString()),
                    PrefStr(PrefConst.PREF_URL_PREFIX_AVATAR, data.url_prefix_avatar.toString()),
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun createHashAndSendEmail(userId:String?){
        mActivity?.callApiCreateHash(userId)
    }

    private fun observerCreateHash(){
        authViewModel.createHashResult.observe(viewLifecycleOwner,{
            try {
                if (it.status != ApiResult.Status.CONSUMED){
                    shareViewModel.showOrHideLoading(it)
                    if (it.status == ApiResult.Status.SUCCESS){
                        if (it.data?.response is ResponseCreateHash.CreateHashResponse){
                            (it.data.response as ResponseCreateHash.CreateHashResponse).let { response ->
                            }
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        })
    }

    override fun getCurrentFragment(): Int {
        return FRM_LOGIN
    }

    override fun finish() {

    }
}
