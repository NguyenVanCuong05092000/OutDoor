package brite.outdoor.data.local.pref

import android.content.Context
import android.content.SharedPreferences
import brite.outdoor.constants.PrefConst


class PrefManager {
    companion object {
        private const val PREF_NAME = "_App_Pref"
        private var mInstance: PrefManager? = null
        private lateinit var mSettings: SharedPreferences
        fun getInstance(context: Context): PrefManager {
            if (mInstance == null) {
                mInstance = PrefManager()
                mSettings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            }
            return mInstance!!
        }
    }

    fun writeArr(vararg list: PrefEntity) {
        try {
            val ed = mSettings.edit()
            for (entity in list) {
                when (val pref = entity.value) {
                    is PrefInt -> ed.putInt(pref.key, pref.value)
                    is PrefStr -> ed.putString(pref.key, pref.value)
                    is PrefBool -> ed.putBoolean(pref.key, pref.value)
                    is PrefLng -> ed.putLong(pref.key, pref.value)
                    is PrefFlt -> ed.putFloat(pref.key, pref.value)
                }
            }
            ed.commit()
        } catch (e: Exception) {
        }
    }

    fun writeBoolean(key: String, holder: Boolean) {
        try {
            val ed = mSettings.edit()
            ed.putBoolean(key, holder)
            ed.commit()
        } catch (e: Exception) {
        }
    }

    fun writeLong(key: String, holder: Long) {
        try {
            val ed = mSettings.edit()
            ed.putLong(key, holder)
            ed.commit()
        } catch (e: Exception) {
        }
    }

    fun writeFloat(key: String, holder: Float) {
        try {
            val ed = mSettings.edit()
            ed.putFloat(key, holder)
            ed.commit()
        } catch (e: Exception) {
        }
    }

    fun writeInt(key: String, holder: Int) {
        try {
            val ed = mSettings.edit()
            ed.putInt(key, holder)
            ed.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeInt(vararg list: PrefInt) {
        try {
            val ed = mSettings.edit()
            for (entity in list)
                ed.putInt(entity.key, entity.value)
            ed.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeArrInt(list: ArrayList<PrefInt>) {
        try {
            val ed = mSettings.edit()
            for (entity in list)
                ed.putInt(entity.key, entity.value)
            ed.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeString(key: String, holder: String) {
        try {
            val ed = mSettings.edit()
            ed.putString(key, holder)
            ed.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeStr(vararg list: PrefStr) {
        try {
            val ed = mSettings.edit()
            for (entity in list)
                ed.putString(entity.key, entity.value)
            ed.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeArrStr(list: ArrayList<PrefStr>) {
        try {
            val ed = mSettings.edit()
            for (entity in list)
                ed.putString(entity.key, entity.value)
            ed.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getString(key: String): String? {
        try {
            return mSettings.getString(key, "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getString(key: String, keyDefault: String): String {
        try {
            return mSettings.getString(key, keyDefault)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return keyDefault
    }

    fun getBoolean(key: String): Boolean {
        try {
            return mSettings.getBoolean(key, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getBoolean(key: String, keyDefault: Boolean): Boolean {
        try {
            return mSettings.getBoolean(key, keyDefault)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return keyDefault
    }

    fun getLong(key: String): Long {
        try {
            return mSettings.getLong(key, -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    fun getLong(key: String, keyDefault: Long): Long {
        try {
            return mSettings.getLong(key, keyDefault)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return keyDefault
    }

    fun getFloat(key: String): Float {
        try {
            return mSettings.getFloat(key, 0F)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0F
    }

    fun getFloat(key: String, keyDefault: Float): Float {
        try {
            return mSettings.getFloat(key, keyDefault)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return keyDefault
    }

    fun getInt(key: String): Int {
        try {
            return mSettings.getInt(key, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun getInt(key: String, keyDefault: Int): Int {
        try {
            return mSettings.getInt(key, keyDefault)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return keyDefault
    }

    fun containsKey(key: String): Boolean {
        try {
            return mSettings.contains(key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun releaseMemoryFromKey(key: String) {
        val ed: SharedPreferences.Editor = mSettings.edit()
        ed.remove(key)
        ed.commit()
    }

    fun releaseMemoryFromKeys(list: ArrayList<String>) {
        val ed: SharedPreferences.Editor = mSettings.edit()
        for (key in list)
            ed.remove(key)
        ed.commit()
    }

    fun releaseMemory() {
        val ed: SharedPreferences.Editor = mSettings.edit()
        ed.clear()
        ed.commit()
    }

    fun releaseUserDataWhenLogout() {
        val ed: SharedPreferences.Editor = mSettings.edit()
        ed.remove(PrefConst.PREF_LOGIN_WITH)
        ed.remove(PrefConst.PREF_TOKEN)
        ed.remove(PrefConst.PREF_USER_ID)
        ed.remove(PrefConst.PREF_LAST_SYNC)
        ed.remove(PrefConst.PREF_FIREBASE_TOKEN)
        ed.commit()
    }
}