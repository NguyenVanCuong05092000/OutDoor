package brite.outdoor.data.remote

data class ApiResult<out T>(var status: Status, val data: T?, val message: String?) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        ERROR_TOKEN,
        CONSUMED,
        ERROR_NETWORK,
        ERROR_DELETE_POST,
        ERROR_DELETE_COMMENT
    }

    companion object {
        fun <T> success(data: T): ApiResult<T> {
            return ApiResult(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String?, data: T? = null): ApiResult<T> {
            return ApiResult(Status.ERROR, data, message)
        }

        fun <T> errorToken(message: String?, data: T? = null): ApiResult<T> {
            return ApiResult(Status.ERROR_TOKEN, data, message)
        }
        fun <T> errorDeletedPost(message: String?, data: T? = null): ApiResult<T> {
            return ApiResult(Status.ERROR_DELETE_POST, data, message)
        }
        fun <T> errorDeleteComment(message: String?,data:T?=null):ApiResult<T>{
            return ApiResult(Status.ERROR_DELETE_COMMENT,data,message)
        }

        fun <T> loading(data: T? = null): ApiResult<T> {
            return ApiResult(Status.LOADING, data, null)
        }
        fun <T> errorNetwork(data:T?=null):ApiResult<T>{
            return ApiResult(Status.ERROR_NETWORK,data,null)
        }

    }
}