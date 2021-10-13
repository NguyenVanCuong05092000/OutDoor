package brite.outdoor.ui.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import brite.outdoor.R
import brite.outdoor.adapter.AdapterReport
import brite.outdoor.constants.ApiConst.Companion.PARAM_API_POST_ID
import brite.outdoor.constants.ApiConst.Companion.PARAM_REPORT_REASON
import brite.outdoor.constants.AppConst
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ResponseReport
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.databinding.DialogFrmReportBinding
import brite.outdoor.utils.setSingleClick
import brite.outdoor.viewmodel.ReportViewModel
import brite.outdoor.viewmodel.ShareViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogFragmentReport(val entity: ListPostUserData?, val position: Int?, val listReport: ArrayList<ResponseReport.Report>?, var requestParam: HashMap<String, String>?) : DialogFragment() {

    private lateinit var binding:DialogFrmReportBinding
    private val reportViewModel by viewModels<ReportViewModel>()
    private val shareViewModel by activityViewModels<ShareViewModel>()
    private lateinit var callBackListener: CallBackListener
    private var adapter:AdapterReport?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(inflater, R.layout.dialog_frm_report,container,false)
        initView()
        return binding.root
    }

    private fun initView(){
        binding.imgBack.setSingleClick {
            onBack()
        }
        observerReport()
        initAdapter()
        onListenerLearMore()
    }
    private fun initAdapter(){
        try {
            if (adapter==null){
                listReport?.let {
                    adapter=AdapterReport(it,requireContext(),itemClickListener = { item:ResponseReport.Report -> callApiReport(item)})
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        binding.rcView.adapter=adapter
        adapter?.notifyDataSetChanged()
    }

    fun onCallBackListener(callBackListener: CallBackListener){
        this.callBackListener=callBackListener
    }

    private fun onBack(){
        this.dismiss()
    }
    private fun callApiReport(item: ResponseReport.Report){
        try {
            requestParam?.let {
                it[PARAM_REPORT_REASON] = item.content
                it[PARAM_API_POST_ID]=entity?.id.toString()
                reportViewModel.requestReport(it)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun observerReport(){
        try {
            reportViewModel.reportResult.observe(viewLifecycleOwner,{
                shareViewModel.showOrHideLoading(it)
                if (it.status==ApiResult.Status.SUCCESS){
                    this.dismiss()
                    val dialog= DialogNotify(requireContext())
                    dialog.show(resources.getString(R.string.str_report_post),false)


                }else if (it.status== ApiResult.Status.ERROR || it.status== ApiResult.Status.ERROR_TOKEN || it.status== ApiResult.Status.ERROR_NETWORK) this.dismiss()
                callBackListener.reportSuccess(position)
            })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    interface CallBackListener{
        fun reportSuccess(position:Int?)
    }
    private fun onListenerLearMore(){
        binding.tvLearnMore.setSingleClick {
            val statusLanguage = PrefManager.getInstance(requireContext()).getInt(PrefConst.PREF_LANGUAGE)
            openBrowser("${AppConst.URL_REPORT_VIOLATIONS}$statusLanguage")
        }
    }
    private fun openBrowser(link : String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

}