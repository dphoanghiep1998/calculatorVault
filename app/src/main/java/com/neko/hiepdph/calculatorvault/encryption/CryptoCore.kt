package com.neko.hiepdph.calculatorvault.encryption

import android.content.Context
import android.util.Base64
import com.neko.hiepdph.calculatorvault.common.extensions.config
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class CryptoCore(private val context: Context) {

    companion object {
        var instance: CryptoCore? = null
        fun getInstance(context: Context): CryptoCore {
            if (instance == null) {
                instance = CryptoCore(context)
            }
            return instance!!
        }
    }

    private fun generateKey(): SecretKey {
        val keyBytes = ByteArray(16)
        val secretKeyBytes = context.config.secretKey.toByteArray(StandardCharsets.UTF_8)
        System.arraycopy(
            secretKeyBytes, 0, keyBytes, 0,
            secretKeyBytes.size.coerceAtMost(keyBytes.size)
        )
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encryptFileName(fileName: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, generateKey())
            val cipherText = cipher.doFinal(fileName.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(cipherText, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            "abc"
        }
    }

    fun decryptFileName(encryptedFileName: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, generateKey())
            val cipherText = Base64.decode(encryptedFileName, Base64.DEFAULT)
            val plainText = cipher.doFinal(cipherText)
            String(plainText, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            "abc"
        }
    }

    fun encryptFile(fileName: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, generateKey())
            val cipherText = cipher.doFinal(fileName.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(cipherText, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            "abc"
        }
    }
}