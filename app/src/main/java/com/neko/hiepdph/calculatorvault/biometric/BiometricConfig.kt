package com.neko.hiepdph.calculatorvault.biometric

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.neko.hiepdph.calculatorvault.R

class BiometricConfig(
    private val ownerFragment: Fragment?,
    private val ownerFragmentActivity: FragmentActivity?,
    private val authenticateSuccess: (() -> Unit)? = null,
    private val authenticateFailed: (() -> Unit)? = null
) {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricManager: BiometricManager

    private constructor(builder: Builder) : this(
        builder.ownerFragment,
        builder.ownerFragmentActivity,
        builder.authenticateSuccess,
        builder.authenticateFailed
    )


    class Builder {
        var ownerFragment: Fragment? = null
        var ownerFragmentActivity: FragmentActivity? = null
        var authenticateSuccess: (() -> Unit)? = null
        var authenticateFailed: (() -> Unit)? = null
        fun build() = BiometricConfig(this).apply {
            ownerFragment?.let {
                setupBiometricAuthentication(it)
            }
            ownerFragmentActivity?.let {
                setupBiometricAuthentication(it)
            }
        }
    }
    companion object {
        inline fun biometricConfig(block: Builder.() -> Unit) = Builder().apply(block).build()
    }


     fun setupBiometricAuthentication(owner: Fragment) {
        biometricManager = BiometricManager.from(owner.requireContext())
        val executor = ContextCompat.getMainExecutor(owner.requireContext())
        biometricPrompt = BiometricPrompt(owner, executor, biometricCallback)
    }

     fun setupBiometricAuthentication(owner: FragmentActivity) {
        biometricManager = BiometricManager.from(owner.applicationContext)
        val executor = ContextCompat.getMainExecutor(owner.applicationContext)
        biometricPrompt = BiometricPrompt(owner, executor, biometricCallback)
    }

    private fun buildBiometricPrompt(): BiometricPrompt.PromptInfo? {
        ownerFragment?.let { fragment ->
            return BiometricPrompt.PromptInfo.Builder()
                .setTitle(fragment.requireContext().getString(R.string.biometric_for_app)).setNegativeButtonText(fragment.getString(R.string.cancel)).build()
        }

        ownerFragmentActivity?.let { fragmentActivity ->
            return BiometricPrompt.PromptInfo.Builder()
                .setTitle(fragmentActivity.applicationContext.getString(R.string.biometric_for_app)).setNegativeButtonText(fragmentActivity.getString(R.string.cancel))
                .build()
        }
        return null

    }

    fun showPrompt(){
        Log.d("TAG", "showPrompt: "+isBiometricFeatureAvailable())
        if(isBiometricFeatureAvailable()){
            buildBiometricPrompt()?.let {
                biometricPrompt.authenticate(it)
            }
        }
    }

    private val biometricCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            authenticateSuccess?.invoke()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            Log.d("TAG", "onAuthenticationError: " + errorCode)
            if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                authenticateFailed?.invoke()
            }
        }
    }


    private fun isBiometricFeatureAvailable(): Boolean {
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
            return true
        }
        return false
    }
}