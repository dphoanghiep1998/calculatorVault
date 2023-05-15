package com.neko.hiepdph.calculatorvault.encryption

import android.content.Context
import android.util.Base64
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.dialog.DialogEncryptionMode
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
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

    // Mã hóa file và lưu kết quả vào file khác
    fun encodeFile(inputFile: File, outputFile: File) {
        val inputStream: InputStream = BufferedInputStream(FileInputStream(inputFile))
        val outputStream: OutputStream = BufferedOutputStream(FileOutputStream(outputFile))

        try {
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                val base64 = Base64.encodeToString(buffer.sliceArray(0 until bytesRead), Base64.DEFAULT)
                outputStream.write(base64.toByteArray(Charsets.UTF_8))
            }
        } finally {
            inputStream.close()
            outputStream.close()
        }
    }

    // Giải mã file và lưu kết quả vào file khác
    fun decodeFile(inputFile: File, outputFile: File) {
        val inputStream: InputStream = BufferedInputStream(FileInputStream(inputFile))
        val outputStream: OutputStream = BufferedOutputStream(FileOutputStream(outputFile))

        try {
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                val decoded = Base64.decode(buffer.sliceArray(0 until bytesRead), Base64.DEFAULT)
                outputStream.write(decoded)
            }
        } finally {
            inputStream.close()
            outputStream.close()
        }
    }


    fun encryptString(password: String, plainText: String): String {
        val ivBytes = ByteArray(16)
        SecureRandom().nextBytes(ivBytes)

        val ivSpec = IvParameterSpec(ivBytes)

        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(password.toByteArray())
        val keySpec = SecretKeySpec(keyBytes.copyOf(16), "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encrypted = cipher.doFinal(plainText.toByteArray())

        val base64iv = encodeBase64ForFilename(ivBytes)
        val base64encrypted = encodeBase64ForFilename(encrypted)

        return "$base64iv+$base64encrypted"
    }

    // Hàm mã hóa chuỗi thành chuỗi Base64 phù hợp với quy chuẩn đặt tên file của Android
    private fun encodeBase64ForFilename(input: ByteArray): String {
        val base64 = Base64.encodeToString(input, Base64.URL_SAFE or Base64.NO_WRAP)
        return base64.replace("/", "_").replace(":", "-")
    }

    fun decryptString(password: String, encryptedText: String): String {
        val parts = encryptedText.split("+")
        val ivBytes = decodeBase64FromFilename(parts[0])
        val encrypted = decodeBase64FromFilename(parts[1])

        val ivSpec = IvParameterSpec(ivBytes)

        val keySpec = SecretKeySpec(password.toByteArray(StandardCharsets.UTF_8), "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        val decrypted = cipher.doFinal(encrypted)

        return String(decrypted, StandardCharsets.UTF_8)
    }

    private fun decodeBase64FromFilename(input: String): ByteArray {
        val base64 = input.replace("_", "/").replace("-", ":")
        return Base64.decode(base64, Base64.URL_SAFE or Base64.NO_WRAP)
    }

}