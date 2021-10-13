package brite.outdoor.secret

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecretHelper {
    fun getTextDecrypt(context: Context, text: String, tag: String): String {
        try {
            Timber.d("encrypt->$text")
            val decryptText = decrypt(context, text)
            Timber.d("encrypt->$text \ndecryptText->$decryptText")
            return decryptText
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "error"
    }

    fun printTextEncrypted(context: Context, text: String, tag: String) {
        val hash = getKeyHash(context)
        val encryptText = encrypt(context, text)
        val decryptText = decrypt(context, encryptText)
        Timber.d(
            "hash->$hash\n" +
                    "tag->$tag\n" +
                    "encryptText->$encryptText\n" +
                    "decryptText->$decryptText"
        )
    }

    private fun encrypt(context: Context, value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(context), getIvParameterSpec(context))
        return Base64.encodeToString(cipher.doFinal(value.encodeToByteArray()), Base64.DEFAULT)

    }

    private fun decrypt(context: Context, encrypted: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(context), getIvParameterSpec(context))
        return String(cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT)))
    }

    private var secretKeySpec: SecretKeySpec? = null
    private fun getSecretKeySpec(context: Context): SecretKeySpec {
        if (secretKeySpec == null)
            secretKeySpec =
                SecretKeySpec(getSecretKey(context).toByteArray(StandardCharsets.UTF_8), "AES")
        return secretKeySpec!!
    }

    private var secretKey: String? = null
    private fun getSecretKey(context: Context): String {
        if (secretKey.isNullOrEmpty())
            secretKey = getSecretKey(getKeyHash(context).trim(), 32)
        return secretKey!!
    }

    private fun getSecretKey(hash: String, length: Int): String {
        return if (hash.isNotEmpty()) {
            if (hash.length >= length)
                hash.substring(0, length)
            else {
                val stringBuilder = StringBuilder()
                stringBuilder.append(hash)
                for (i in hash.length until length) {
                    stringBuilder.append("0")
                }
                stringBuilder.toString()
            }
        } else "0000000000000000" + if (length == 32) "0000000000000000" else ""
    }

    private var keyHash: String? = null
    private fun getKeyHash(context: Context): String {
        if (keyHash.isNullOrEmpty()) {
            try {
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )

                for (signature in packageInfo.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    keyHash = String(Base64.encode(md.digest(), 0))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return if (keyHash.isNullOrEmpty()) "" else keyHash!!
    }

    private var ivParameterSpec: IvParameterSpec? = null
    private fun getIvParameterSpec(context: Context): IvParameterSpec {
        if (ivParameterSpec == null) {
            ivParameterSpec =
                IvParameterSpec(getRandomVector(context).toByteArray(StandardCharsets.UTF_8))
        }
        return ivParameterSpec!!
    }

    private var randomVector: String? = null
    private fun getRandomVector(context: Context): String {
        if (randomVector.isNullOrEmpty())
            randomVector = getSecretKey(getKeyHash(context).trim(), 16)
        return randomVector!!
    }
}