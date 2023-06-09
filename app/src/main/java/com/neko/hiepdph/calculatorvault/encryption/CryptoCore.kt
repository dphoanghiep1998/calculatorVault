package com.neko.hiepdph.calculatorvault.encryption

import android.content.Context
import android.util.Base64
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
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

    fun getSecretKey(key: String): SecretKey {

        val decodedKey = Base64.decode(key, Base64.NO_WRAP)

        if(decodedKey.size > 16){
            return SecretKeySpec( decodedKey.copyOf(16), "AES")

        }
        if(decodedKey.size < 16){
            val padded = ByteArray(16)
            System.arraycopy(decodedKey, 0, padded, 0, decodedKey.size)
            return SecretKeySpec(padded, "AES")
        }

        return SecretKeySpec(decodedKey, "AES")
    }

    @Throws(Exception::class)
    fun decrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
        val decrypted: ByteArray
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.DECRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
        decrypted = cipher.doFinal(fileData)
        return decrypted
    }

    @Throws(Exception::class)
    fun encrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
        val data = yourKey.encoded
        val skeySpec = SecretKeySpec(data, 0, data.size, "AES")
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(ByteArray(cipher.getBlockSize())))
        return cipher.doFinal(fileData)
    }

    @Throws(Exception::class)
    fun readFile(filePath: String): ByteArray {
        val file = File(filePath)
        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(
            FileInputStream(file)
        )

        inputBuffer.read(fileContents)
        inputBuffer.close()

        return fileContents
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