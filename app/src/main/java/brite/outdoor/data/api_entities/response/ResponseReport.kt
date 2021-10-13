package brite.outdoor.data.api_entities.response

import android.content.Context
import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize
import java.io.IOException

object ResponseReport {
    class ReportResult:BaseApiResult()
    @Parcelize
    data class ReportResponse(
            val response:Boolean?
    ):Parcelable{
        fun getListReport(){

        }
    }
    data class Report(
            val id :Int,
            val content:String,
            val cIndex:Int,
    )

}