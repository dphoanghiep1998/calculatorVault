package com.neko.hiepdph.calculatorvault.biometric

import android.content.Context
import android.hardware.fingerprint.FingerprintManager

object BiometricUtils {
    fun checkBiometricHardwareAvailable(context: Context): Boolean {
        val fingerPrintManager =
            context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        return fingerPrintManager.isHardwareDetected
    }

    fun checkBiometricEnrolled(context: Context): Boolean {
        val fingerPrintManager =
            context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        return fingerPrintManager.hasEnrolledFingerprints()
    }


}