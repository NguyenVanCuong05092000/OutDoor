package brite.outdoor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import brite.outdoor.data.api_entities.response.ResponseListPostUser
import brite.outdoor.data.api_entities.response.ResponseReport
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val gson : Gson, apiController: ApiController): ViewModel() {

    // report
    private val _requestReport : MutableLiveData<HashMap<String, String>> = MutableLiveData()

    fun requestReport(requestReport : HashMap<String,String>) {
        _requestReport.value = requestReport
    }
    private val _reportResult = _requestReport.switchMap {
        performGetOperation(gson) { apiController.report(it) }
    }
    val reportResult : LiveData<ApiResult<ResponseReport.ReportResult>> = _reportResult
}