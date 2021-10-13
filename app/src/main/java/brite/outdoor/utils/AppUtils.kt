package brite.outdoor.utils
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
class AppUtils {
    companion object {
        fun parseInt(value: String): Int {
            var des = -1
            try {
                des = value.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return des
        }

        class NullStringToEmptyAdapterFactory<T> : TypeAdapterFactory {
            override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
                val rawType = type?.rawType as Class<T>
//                Log.d("TAGStringAdapter", "create: ${rawType.simpleName} - ${type.type.javaClass.simpleName}")
                if (rawType != String::class.java) {
                    return null
                }
                return StringAdapter() as TypeAdapter<T>
            }
        }

        class StringAdapter : TypeAdapter<String?>() {
            override fun read(reader: JsonReader?): String? {
//                Log.d("TAGStringAdapter", "read: $reader - ${reader?.peek()}")
                if (reader?.peek() == JsonToken.NULL) {
                    reader.nextNull()
                    return ""
                }
                return reader?.nextString()
            }

            override fun write(writer: JsonWriter?, value: String?) {
//                Log.d("TAGStringAdapter", "write:$writer ${value}")
                if (value == null || value == "null") {
                    writer?.nullValue()
                    return
                }
                writer?.value(value)
            }
        }

        @SuppressLint("HardwareIds")
        fun getAndroidId(context: Context?): String? {
            return if (context == null) {
                null
            } else Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

        fun hideKeyboard(view: View?) {
            try {
                val manager =
                    view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        fun getRealPathFromURI(activity: Activity?,uri: Uri): String {
            try {
                var path = ""
                val proj = arrayOf(MediaStore.MediaColumns.DATA)
                val cursor: Cursor? = activity?.contentResolver?.query(uri, proj, null, null, null)
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val columnIndex: Int =
                            cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                        path = cursor.getString(columnIndex)
                    }
                }
                cursor?.close()
                return path
            } catch (e: Exception) {
            }
            return ""
        }

        fun isVideo(fileName: String) : Boolean {
            //video
            Log.d("TAG", "isVideo: $fileName")
            val suffixPatterns = arrayListOf(".mp4",".mkv",".avi",".webm",".3gp",".mov")
            for (suffix in suffixPatterns.iterator()) {
                if (fileName.endsWith(suffix)) return true
            }
            return false
        }
        fun getPathUrlThumbnail(fileName: String):String?{
            var thumbnail : String?=null
            val suffixPatterns = arrayListOf(".mp4",".mkv",".avi",".webm",".3gp",".mov")
            for (suffix in suffixPatterns.iterator()) {
                if (fileName.endsWith(suffix)) {
                    thumbnail=fileName.replace(suffix,".jpg")
                    return thumbnail
                }
            }
            return null
        }
    }



}