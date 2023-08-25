package com.neko.hiepdph.calculatorvault.encryption

import android.content.Context
import android.util.Base64
import android.util.Log
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class CryptoCore() {
    companion object {
        var instance: CryptoCore? = null
        fun getSingleInstance(): CryptoCore {
            if (instance == null) {
                instance = CryptoCore()
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
    fun decrypt(yourKey: SecretKey): Cipher {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
        cipher.init(Cipher.DECRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
        return cipher
    }

    @Throws(Exception::class)
    fun encrypt(yourKey: SecretKey): Cipher {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC")
        cipher.init(Cipher.ENCRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
        return cipher
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
}