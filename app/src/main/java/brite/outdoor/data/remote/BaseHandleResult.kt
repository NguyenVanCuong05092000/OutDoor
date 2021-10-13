package brite.outdoor.data.remote

import android.util.Log
import brite.outdoor.constants.ApiConst
import kotlinx.coroutines.delay
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseHandleResult {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): ApiResult<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                Timber.d("getResult: ${response.isSuccessful}")
                val body = response.body()
                if (body != null) return ApiResult.success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        }catch (e:SocketTimeoutException){
            e.printStackTrace()
            return errorNetwork()
        }catch (e:IOException){
            e.printStackTrace()
            return errorNetwork()
        }catch (e: Exception) {
            e.printStackTrace()
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): ApiResult<T> {
        Timber.d(message)
        return ApiResult.error("Network call has failed for a following reason: $message")
    }

    private fun <T> errorNetwork(): ApiResult<T> {
        return ApiResult.errorNetwork()
    }
}